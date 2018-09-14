package com.tijiantest.testcase.channel.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.channel.SendMsgVO;
import com.tijiantest.model.order.channel.MongoOrderVO;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;

public class BatchSendMsgTest extends ChannelBase {

	@Test(description = "批量发送短信", groups = { "qa" }, dataProvider = "batchSendMsg")
	public void test_01_batchSendMsg(String... args) throws SqlException, ParseException {
		String message = args[16];

		OrderQueryParams orderQueryRequestParams = OrderBase.generateOrderQueryParams(args);

		HttpResult result = httpclient.post(Flag.CHANNEL, Order_ListChannelMongoOrders,
				JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<MongoOrderVO> mongoOrderVOs = JSON.parseArray(JsonPath.read(body, "$.records[*]").toString(),
				MongoOrderVO.class);

		if (mongoOrderVOs.size() == 0) {
			return;
		}
		// 随机获取一个订单发送短信
		Random random = new Random();
		int index = random.nextInt(mongoOrderVOs.size()) % (mongoOrderVOs.size() + 1);
		int orderId = mongoOrderVOs.get(index).getId();
		List<Integer> orderIds = new ArrayList<>();
		orderIds.add(orderId);
		SendMsgVO sendMsgVO = new SendMsgVO();
		sendMsgVO.setMsgContent(message);
		sendMsgVO.setOrderIds(orderIds);

		result = httpclient.post(Flag.CHANNEL, Order_BatchSendMsg, JSON.toJSONString(sendMsgVO));
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		//Assert.assertEquals(result.getBody(), "{}","错误提示："+message+" body:"+result.getBody());

	}

	@DataProvider(name = "batchSendMsg")
	public Iterator<String[]> batchSendMsg() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/batchSendMsg.csv", 16);
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
