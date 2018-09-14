package com.tijiantest.testcase.channel.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.order.channel.MongoOrderVO;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.order.channel.RevokeOrderVO;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.testcase.main.order.BookTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class RevokeChannelOrderTest extends ChannelBase {
	@Test(description = "撤销订单", groups = { "qa" }, dataProvider = "revokeChannelOrder", dependsOnGroups = {"main_channelBook" },ignoreMissingDependencies = true)
	public void test_01_revokeChannelOrder(String... args) throws SqlException {
		String sendMsg = args[1];
		int orderId = 0;
		// 先在/m/mt/上下个已预约的订单，然后在channel上撤销
		try{
			orderId = BookTest.channelOrderId;
		}catch (AssertionError e){
		}finally {
			//main模块没有部署
			log.error("MAIN-APP未部署，或者独立执行");
			List<Order> mainList = OrderChecker.getMainChannelOrderList(defChannelid);
			if(mainList == null || mainList.size() == 0){
				log.error("无法找到C端渠道订单订单,请手动创建C端订单（或者开启MAIN-APP模块）");
				return;
			}
			if(orderId == 0)
				orderId = mainList.get(0).getId();
		}

		List<Integer> orderIds = new ArrayList<>();
		orderIds.add(orderId);

		RevokeOrderVO revokeOrderVO = new RevokeOrderVO();
		revokeOrderVO.setSendMsg(Boolean.valueOf(sendMsg));
		revokeOrderVO.setOrderIds(orderIds);
		//检查订单是否正在结算中
		Order order = OrderChecker.getOrderInfo(orderId);
		boolean cannotRevoke = SettleChecker.isOrderInSettlement(order.getId());
		if(order.getStatus()  == OrderStatus.REVOCATION.intValue() 
				|| order.getStatus() == OrderStatus.CLOSED.intValue() 
				|| order.getStatus() == OrderStatus.DELETED.intValue()){
			log.info("订单"+orderId + "状态.."+order.getStatus() + "  不能撤销...请检查是否开启了定时任务或者人为操作导致订单状态改变");
			return;
		}
			
		HttpResult result = httpclient.post(Flag.CHANNEL, Order_RevokeChannelOrder, JSON.toJSONString(revokeOrderVO));
		String body = result.getBody();
		System.out.println(body);
		if(cannotRevoke){
			Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST, "撤单失败:" + result.getBody() + "orderId:" + orderId);
			Assert.assertTrue(result.getBody().contains("撤单失败,原因：此状态订单无法撤销"));
		}else{
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"错误提示："+result.getBody());
//			Assert.assertEquals(body, "{}");

			if (checkdb) {
				String sql = "select * from tb_order where id=?";
				for (int i = 0; i < orderIds.size(); i++) {
					List<Map<String, Object>> list = DBMapper.query(sql, orderIds.get(i));
					Assert.assertEquals(OrderStatus.REVOCATION + "", list.get(0).get("status").toString());
				}
			}

			if (checkmongo) {
				for (int i = 0; i < orderIds.size(); i++) {
					String mongoSql = "{'id':" + orderIds.get(i) + "}";
					List<Map<String, Object>> list = MongoDBUtils.query(mongoSql, MONGO_COLLECTION);
					Assert.assertEquals(OrderStatus.REVOCATION + "", list.get(i).get("status").toString());
				}
			}
		}
		
	}

	@Test(description = "撤单失败", groups = { "qa" }, dataProvider = "revokeChannelOrder_fail")
	public void test_02_revokeChannelOrder(String... args) throws ParseException {
		// 撤销除预约之外的订单，会报错
		OrderQueryParams orderQueryRequestParams = OrderBase.generateOrderQueryParams(args);
		HttpResult result = httpclient.post(Flag.CHANNEL, Order_ListChannelMongoOrders,
				JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		log.info(body);
		List<MongoOrderVO> mongoOrderVOs = JSON.parseArray(JsonPath.read(body, "$.records[*]").toString(),
				MongoOrderVO.class);
		if (mongoOrderVOs.size() == 0) {
			return;
		}

		// 随机取一个order
		Random random = new Random();
		int index = random.nextInt(mongoOrderVOs.size()) % (mongoOrderVOs.size() + 1);
		int orderId = mongoOrderVOs.get(index).getId();
		log.info("----------- 订单ID:" + orderId);
		List<Integer> orderIds = new ArrayList<>();
		orderIds.add(orderId);

		RevokeOrderVO revokeOrderVO = new RevokeOrderVO();
		revokeOrderVO.setSendMsg(Boolean.valueOf(false));
		revokeOrderVO.setOrderIds(orderIds);

		result = httpclient.post(Flag.CHANNEL, Order_RevokeChannelOrder, JSON.toJSONString(revokeOrderVO));
		body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_BAD_REQUEST);
		Assert.assertTrue(body.contains("订单无法撤销"));

	}

	@DataProvider(name = "revokeChannelOrder")
	public Iterator<String[]> revokeChannelOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/revokeChannelOrder.csv", 3);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider(name = "revokeChannelOrder_fail")
	public Iterator<String[]> revokeChannelOrder_fail() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/revokeChannelOrder_fail.csv", 3);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
}
