package com.tijiantest.testcase.channel.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.order.OrderQueryParams;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.channel.MongoOrderVO;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class ListChannelMongoOrdersTest extends ChannelBase {

	public static List<MongoOrderVO> listOrder = new ArrayList<>();

	@Test(description = "渠道商mongo订单查询", groups = { "qa", "channel_listOrder" }, dataProvider = "listChannelMongoOrders")
	public void test_01_listChannelMongoOrders(String... args) throws ParseException, SqlException {
		
				
		OrderQueryParams orderQueryRequestParams = OrderBase.generateOrderQueryParams(args);
		HttpResult result = httpclient.post(Flag.CHANNEL, Order_ListChannelMongoOrders,
				JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		log.info(body);
		List<MongoOrderVO> mongoOrderVOs = JSON.parseArray(JsonPath.read(body, "$.records[*]").toString(),
				MongoOrderVO.class);
		if (mongoOrderVOs.size() > 0 && !mongoOrderVOs.equals("[]"))
			listOrder = mongoOrderVOs;
		System.out.println(listOrder.size());

		if (checkmongo) {

			List<Map<String, Object>> list = OrderBase.generateMongoResultList(args);
			if(list.size()>0)
				System.out.print(list.get(0));
			System.out.println("个数："+list.size());
			Assert.assertEquals(mongoOrderVOs.size(), list.size(),"失败了"+args[16]);
			for (int i = 0; i < mongoOrderVOs.size(); i++) {
				MongoOrderVO vo = mongoOrderVOs.get(i);
				log.info("订单号"+vo.getId());
				Map<String, Object> mogoMap = list.get(i);
//				log.info("订单号"+vo.getId() + "mongoMap.."+vo.getId());

				// account
				Assert.assertEquals(vo.getExaminer().getId(), ((BasicDBObject) mogoMap.get("examiner")).get("_id"));
				Assert.assertEquals(vo.getExaminer().getName(), ((BasicDBObject) mogoMap.get("examiner")).get("name"));
				Assert.assertEquals(vo.getExaminer().getIdCard(),
						((BasicDBObject) mogoMap.get("examiner")).get("idCard"));
				Assert.assertEquals(vo.getExaminer().getMobile(),
						((BasicDBObject) mogoMap.get("examiner")).get("mobile"));
				Assert.assertEquals(vo.getExaminer().getStatus(),
						((BasicDBObject) mogoMap.get("examiner")).get("status"));
				Assert.assertEquals(vo.getExaminer().getType(), ((BasicDBObject) mogoMap.get("examiner")).get("type"));
				// accountRelation
				Assert.assertEquals(vo.getExaminer().getNewCompanyId(),
						((BasicDBObject) mogoMap.get("examiner")).get("newCompanyId"));
				Assert.assertEquals(vo.getExaminer().getCustomerId(),
						((BasicDBObject) mogoMap.get("examiner")).get("customerId"));
				Assert.assertEquals(vo.getExaminer().getGender(),
						((BasicDBObject) mogoMap.get("examiner")).get("gender"));
				Assert.assertEquals(vo.getExaminer().getBirthYear(),
						((BasicDBObject) mogoMap.get("examiner")).get("birthYear"));
				Assert.assertEquals(vo.getExaminer().getAge(),
						((BasicDBObject) mogoMap.get("examiner")).get("age"));
//				if(((BasicDBObject)mogoMap.get("examiner")).get("age")==null){
//					String sql = "select * from tb_examiner where customer_id = "+vo.getAccountRelation().getCustomerId()+"" +
//							" and manager_id = "+vo.getAccountRelation().getManagerId()+" and company_id =  "+vo.getAccountRelation().getCompanyId()  ;
//					log.info(sql);
//					List<Map<String,Object>> retdblist = DBMapper.query(sql);
//					if(retdblist.size() == 1){
//						log.debug("vvvv"+vo.getAccount().getId()+"...."+vo.getAccount().getName()+"...订单id:"+vo.getId());
//						Assert.assertEquals(vo.getAccountRelation().getAge().intValue(),
//								Integer.parseInt(sy.format(new Date())) - Integer.parseInt(retdblist.get(0).get("birthYear").toString()));
//						Assert.assertEquals(vo.getAccountRelation().getBirthYear().intValue(),
//								Integer.parseInt(retdblist.get(0).get("birthYear").toString()));
//					}
//				  }	else{
//					//年龄实时计算
//					Assert.assertEquals(vo.getExaminer().getBirthYear(),
//							((BasicDBObject) mogoMap.get("accountRelation")).get("birthYear"));
//					Assert.assertEquals(vo.getExaminer().getAge().intValue(),
//							Integer.parseInt(sy.format(new Date())) - (int)((BasicDBObject) mogoMap.get("accountRelation")).get("birthYear"));
//
//				}

				// hopsital
				Assert.assertEquals(vo.getOrderHospital().getId(), ((BasicDBObject) mogoMap.get("orderHospital")).get("_id"));
				Assert.assertEquals(vo.getOrderHospital().getName(), ((BasicDBObject) mogoMap.get("orderHospital")).get("name"));
				Assert.assertEquals(vo.getOrderHospital().getOrganizationType(),
						((BasicDBObject) mogoMap.get("orderHospital")).get("organizationType"));
				Assert.assertEquals(vo.getExamCompanyId(), mogoMap.get("examCompanyId"));
				if (mogoMap.get("examCompanyId").toString().equals("1585")) {
					Assert.assertEquals(vo.getExamCompany(), "散客单位");
				}
				Assert.assertEquals(vo.getExamDate(), mogoMap.get("examDate"));
				Assert.assertEquals(vo.getExamTimeIntervalId(), mogoMap.get("examTimeIntervalId"));
				Assert.assertEquals(vo.getExamTimeIntervalName(), mogoMap.get("examTimeIntervalName"));
				Assert.assertEquals(vo.getIsExport(), mogoMap.get("isExport"));
				Assert.assertEquals(vo.getInsertTime(), mogoMap.get("insertTime"));
				Assert.assertEquals(vo.getOrderExportExtInfo().getMarriageStatusLabel(), ((BasicDBObject)mogoMap.get("orderExportExtInfo")).getString("marriageStatusLabel"));
				Assert.assertEquals(vo.getMealName(), mogoMap.get("mealName"));
				Assert.assertEquals(vo.getOperator(), mogoMap.get("operator"));
				Assert.assertEquals(vo.getId(), mogoMap.get("id"));
				Assert.assertEquals(vo.getOrderPrice(), mogoMap.get("orderPrice"));
				Assert.assertEquals(vo.getStatus().intValue(),(int)Double.parseDouble(mogoMap.get("status").toString()));
				Assert.assertEquals(vo.getOrderExtInfo().getSelfMoney(), ((BasicDBObject)mogoMap.get("orderExtInfo")).getInt("selfMoney"));
			}
		}
	}

	@DataProvider(name = "listChannelMongoOrders")
	public Iterator<String[]> listChannelMongoOrders() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/listChannelMongoOrders.csv", 16);
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
