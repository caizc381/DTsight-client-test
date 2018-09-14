package com.tijiantest.testcase.ops.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.testcase.main.order.GetorderListPageTest;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class OrderDetailTest extends OpsBase {

	@Test(description = "获取订单详情", groups = { "qa" }, dataProvider = "orderDetail",dependsOnGroups={"main_getOrderList"},ignoreMissingDependencies = true)
	public void test_01_orderDetail(String... args) throws SqlException {
		int orderId = -1;
		try{
		List<Integer> orderList = GetorderListPageTest.orderList;
		if(orderList.size() == 0){
			MyHttpClient mainHttpClinet = new MyHttpClient();
			onceLoginInSystem(mainHttpClinet, Flag.MAIN, defMainUsername, defMainPasswd);
			orderId = OrderChecker.main_createOrder(mainHttpClinet, "2018-05-05",
					HospitalChecker.getHospitalPeriodSettings(1).get(0).getId().intValue(),
					AccountChecker.getUserInfo(defMainUsername).getAccount_id(), 1);
			onceLogOutSystem(mainHttpClinet, Flag.MAIN);
			}else
				orderId = GetorderListPageTest.orderList.get(0);
		}catch (AssertionError e){
			//main模块没有部署
			log.error("MAIN-APP未部署");
			List<Order> mainList = OrderChecker.getMainOrderList(defHospitalId);
			if(mainList == null || mainList.size() == 0){
				log.error("无法找到C端订单,请手动创建C端订单（或者开启MAIN-APP模块）");
				return;
			}
			orderId = mainList.get(0).getId();
		}
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("orderId", orderId+""));
		HttpResult result = httpclient.get(Flag.OPS, OpsOrder_OrderDetail, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		Integer examItemPrice = JsonPath.read(body, "$.examItemPrice");
	    JSONObject retInfo = JSON.parseObject(body);

		Map<String, net.minidev.json.JSONObject> itemSnap = JsonPath.read(body, "$.itemSnap.[*]");		

		if (checkdb) {
			waitto(mysqlWaitTime);
			String sql = "select tb_order.id, batch_id, order_num, account_id, tb_order.hospital_id, tb_order.status, order_price, difference_price, exam_date, entry_card_id, discount, is_export, source, insert_time, tb_order.update_time, exam_time_interval_id, remark, from_site from tb_order  where tb_order.id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, orderId);

			Assert.assertEquals(examItemPrice, list.get(0).get("order_price"));

			sql = "select items_detail from tb_order where id = ?";
			list = DBMapper.query(sql, orderId);
			//包含套餐内项目，加项目，减项目
			List<ExamItemSnap> snapList = JSON.parseArray(list.get(0).get("items_detail").toString(),
					ExamItemSnap.class);
			if (snapList != null && !snapList.isEmpty()) {
				for (int i = 0; i < snapList.size(); i++) {
					ExamItemSnap snap = snapList.get(i);
					net.minidev.json.JSONObject snap1 = itemSnap.get(String.valueOf(snap.getId()));
					Assert.assertEquals(snap1.get("originalPrice"), snap.getOriginalPrice());
					Assert.assertEquals(snap1.get("hisId"), snap.getHisId());
					Assert.assertEquals(snap1.get("price"), snap.getPrice());
					Assert.assertEquals(snap1.get("name"), snap.getName());
					Assert.assertEquals(snap1.get("typeToMeal"), snap.getTypeToMeal());
					Assert.assertEquals(snap1.get("type"), snap.getType());
				}
			}
		}
		
		if (checkmongo) {
			 Order ordert = OrderChecker.getOrderInfo(orderId);
			 //精度
			 String calculator = HospitalChecker.getHospitalSetting(ordert.getHospital().getId(), HospitalParam.CALCULATOR_SERVICE).get(HospitalParam.CALCULATOR_SERVICE).toString();
			 if(calculator == null || calculator.equals("defaultCalculator"))
				   Assert.assertEquals(retInfo.get("currency").toString(),"元");
			 else if(calculator.equals("fenRoundCalculator"))
				   Assert.assertEquals(retInfo.get("currency").toString(),"分");
			 else
				   Assert.assertEquals(retInfo.get("currency").toString(),"角");
			 //应退客户的金额
			 RefundAmount refunds = PayChecker.getRefundAmountByOrderNum(ordert.getOrderNum(),PayConstants.OrderType.MytijianOrder);
			 long amount = refunds.getOnlineRefundAmount();
			 if(amount != 0 )
				 Assert.assertEquals(Long.parseLong(retInfo.get("customerPayRefund").toString()),amount);
			 else
				 Assert.assertNull(retInfo.get("customerPayRefund"));
			String sql = "{\"id\":"+orderId+"}";
			List<Map<String, Object>> dbList = MongoDBUtils.query(sql, MONGO_COLLECTION);
			Assert.assertEquals(dbList.size(),1);
			Map<String,Object> moMap = dbList.get(0);
			//订单详情-姓名/性别/年龄/婚姻/联系电话/套餐名/单位/体检日期/预约人/体检中心/时间备注/导引单备注/订单编号/订单状态
			JSONObject retOrderInfo = retInfo.getJSONObject("order");
			log.info("mongoOrder..."+moMap);
			//订单金额/折扣
			Assert.assertEquals(retOrderInfo.getLongValue("orderPrice"),Long.parseLong(moMap.get("orderPrice").toString()));
			Assert.assertEquals(retOrderInfo.getDoubleValue("discount"),Double.parseDouble(moMap.get("discount").toString()));
			//用户基本信息
			Assert.assertEquals(retOrderInfo.getJSONObject("examiner").getString("name"),((BasicDBObject)moMap.get("examiner")).getString("name"));
			Assert.assertEquals(retOrderInfo.getJSONObject("examiner").getIntValue("age"),((BasicDBObject)moMap.get("examiner")).getInt("age"));
			Assert.assertEquals(retOrderInfo.getJSONObject("examiner").getString("mobile"),((BasicDBObject)moMap.get("examiner")).getString("mobile"));
			if(((BasicDBObject)moMap.get("examiner")).getString("address") !=null)
				Assert.assertEquals(retOrderInfo.getJSONObject("examiner").getString("address"),
						((BasicDBObject)moMap.get("examiner")).getString("address"));
			Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("genderLabel"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("genderLabel"));
			Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("marriageStatusLabel"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("marriageStatusLabel").toString());
			if(((BasicDBObject)moMap.get("orderExportExtInfo")).getString("exportExamDate") !=null )
				Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("exportExamDate"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("exportExamDate").toString());

			//预约信息
			Assert.assertEquals(retOrderInfo.getString("mealName"),moMap.get("mealName"));
			Assert.assertEquals(retOrderInfo.getJSONObject("hospitalCompany").getString("name"),((BasicDBObject)moMap.get("hospitalCompany")).getString("name"));
			Assert.assertEquals(retOrderInfo.getJSONObject("hospitalCompany").getIntValue("id"),((BasicDBObject)moMap.get("hospitalCompany")).getInt("_id"));
			if(moMap.get("channelCompany")!=null){
				Assert.assertEquals(retOrderInfo.getJSONObject("channelCompany").getString("name"),((BasicDBObject)moMap.get("channelCompany")).getString("name"));
				Assert.assertEquals(retOrderInfo.getJSONObject("channelCompany").getIntValue("id"),((BasicDBObject)moMap.get("channelCompany")).getInt("_id"));

			}

			Assert.assertEquals(retOrderInfo.getJSONObject("orderExtInfo").getString("examCompany"),((BasicDBObject)moMap.get("orderExtInfo")).get("examCompany"));
			Assert.assertEquals(retOrderInfo.getString("examTimeIntervalName"),moMap.get("examTimeIntervalName"));
			Assert.assertEquals(retOrderInfo.getString("operator"),moMap.get("operator"));
			Assert.assertEquals(retOrderInfo.getJSONObject("orderHospital").getString("name"),((BasicDBObject)moMap.get("orderHospital")).getString("name"));
			if(retOrderInfo.get("remark") !=null){
				JSONObject jremark = JSONObject.parseObject(retOrderInfo.get("remark").toString());
				JSONObject jmongoremark = JSONObject.parseObject(moMap.get("remark").toString());
				Assert.assertEquals(jremark.getString("remarks"),jmongoremark.getString("remarks"));
				Assert.assertEquals(jremark.getString("timeRemarks"),jmongoremark.getString("timeRemarks"));
			}
			Assert.assertEquals(retOrderInfo.getString("orderNum"),moMap.get("orderNum"));

			//订单状态
			Assert.assertEquals(retOrderInfo.getIntValue("status"),Integer.parseInt(moMap.get("status").toString()));
			//调整金额显示
			if(((BasicDBObject) moMap.get("orderExtInfo")).get("adjustPrice") != null)
				Assert.assertEquals(retOrderInfo.getJSONObject("orderExtInfo").getIntValue("adjustPrice"),((BasicDBObject)moMap.get("orderExtInfo")).getInt("adjustPrice"));

			//优惠金额
			if(checkdb){
				String orderNum = moMap.get("orderNum").toString();
				PayAmount payAmount = PayChecker.getPayAmountByOrderNum(orderNum,PayConstants.OrderType.MytijianOrder);
				long dbCoupAmount = payAmount.getHospitalCouponAmount()+payAmount.getPlatformCouponAmount()+payAmount.getChannelCouponAmount();
				Assert.assertEquals(retInfo.getLongValue("couponAmount"),dbCoupAmount);
			}
			JSONObject retManualLog = retInfo.getJSONObject("manualRefundLog");
			if(checkdb){
				String orderNum = moMap.get("orderNum").toString();
				//退款金额/预计退款金额
				String refSql = "select * from tb_order_refund_apply where order_num = '"+orderNum + "'  and is_deleted = 0 order by gmt_created desc  limit 1";
				List<Map<String,Object>> refList = DBMapper.query(refSql);
				if(refList != null && refList.size() > 0)
					Assert.assertEquals(retInfo.get("refundPrice"),refList.get(0).get("amount"));
				//手动修改日志
				String refundSql = "select * from  tb_order_refund_apply a  where  a.order_num = '"+orderNum+"'  and amount > 0 and scene = 5 and is_deleted = 0 and status = 1";
				List<Map<String,Object>> refundList = DBMapper.query(refundSql);
				if(refundList!=null && refundList.size() > 0){
					int refundId = Integer.parseInt(refundList.get(0).get("id").toString());
					String refundLogSql = "select * from tb_order_refund_apply_log where refund_id =  " + refundId +" and status = 1 and is_deleted = 0";
					List<Map<String,Object>> refundLogList = DBMapper.query(refundLogSql);
					Assert.assertEquals(retManualLog.getLongValue("accountAmount"),amount);
					Assert.assertEquals(retManualLog.getLongValue("amount"),Long.parseLong(refundLogList.get(0).get("amount").toString()));
					Assert.assertEquals(retManualLog.getIntValue("beforeRefundStatus"),Integer.parseInt(refundLogList.get(0).get("before_refund_status").toString()));
					Assert.assertEquals(retManualLog.getIntValue("afterRefundStatus"),Integer.parseInt(refundLogList.get(0).get("after_refund_status").toString()));
					Assert.assertEquals(retManualLog.getString("remarks"),refundLogList.get(0).get("reason").toString());
					Assert.assertEquals(retManualLog.getString("operator"),refundLogList.get(0).get("operator_name").toString());
				}
			}
			
			//订单医院设置
			if(((JSONObject) retOrderInfo.get("orderHospital")).get("hospitalSettings")!=null && ((JSONObject)((JSONObject) retOrderInfo.get("orderHospital")).get("hospitalSettings")).get("exportWithXls")!=null){
				Assert.assertEquals(((JSONObject)((JSONObject) retOrderInfo.get("orderHospital")).get("hospitalSettings")).getBooleanValue("exportWithXls"),
						((BasicDBObject)((BasicDBObject) moMap.get("orderHospital")).get("hospitalSettings")).getBoolean("exportWithXls"));
				Assert.assertEquals(((JSONObject)((JSONObject) retOrderInfo.get("orderHospital")).get("hospitalSettings")).getBooleanValue("supportManualRefund"),
						((BasicDBObject)((BasicDBObject) moMap.get("orderHospital")).get("hospitalSettings")).getBoolean("supportManualRefund"));
				Assert.assertEquals(((JSONObject)((JSONObject) retOrderInfo.get("orderHospital")).get("hospitalSettings")).getBooleanValue("mobileFieldOrder"),
						((BasicDBObject)((BasicDBObject) moMap.get("orderHospital")).get("hospitalSettings")).getBoolean("mobileFieldOrder"));
			}
			//套餐内&增加项详情
			if(retInfo.get("itemSnap") != null){
				String jaMeal = retInfo.get("itemSnap").toString();//替换成array
				char[] items = jaMeal.toCharArray();
				items[items.length-1]=']';
				items[0]='[';
				String replaceJret = String.valueOf(items).replaceAll("\"[0-9]+\":", "");
				log.info("replaceJret..."+replaceJret);
				List<ExamItemSnap> jaMealItems = JSONArray.parseArray(replaceJret,ExamItemSnap.class);
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(moMap.get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				List<ExamItemSnap> jmongoItems =  JSONArray.parseArray(examItemSnapStr,ExamItemSnap.class);
				//排序
			    Collections.sort(jmongoItems, new Comparator<ExamItemSnap>() {
			    	@Override
			    	public int compare(ExamItemSnap o1,
			    			ExamItemSnap o2) {
			    		return o1.getName().compareTo(o2.getName());
			    	}
				});
				
			    Collections.sort(jaMealItems, new Comparator<ExamItemSnap>() {
			    	@Override
			    	public int compare(ExamItemSnap o1,
			    			ExamItemSnap o2) {
			    		return o1.getName().compareTo(o2.getName());
			    	}
				});

				if(jmongoItems !=null && jmongoItems.size() > 0){
					for(int s=0;s<jmongoItems.size();s++){
						Assert.assertEquals(jaMealItems.get(s).getHisId(),jmongoItems.get(s).getHisId());
						Assert.assertEquals(jaMealItems.get(s).getName(),jmongoItems.get(s).getName());
						Assert.assertEquals(jaMealItems.get(s).getPrice(),jmongoItems.get(s).getPrice());
						Assert.assertEquals(jaMealItems.get(s).getOriginalPrice(),jmongoItems.get(s).getOriginalPrice());
						Assert.assertEquals(jaMealItems.get(s).isDiscount(),jmongoItems.get(s).isDiscount());
						Assert.assertEquals(jaMealItems.get(s).getTypeToMeal(),jmongoItems.get(s).getTypeToMeal());
						Assert.assertEquals(jaMealItems.get(s).getType(),jmongoItems.get(s).getType());
					}
				}

			}	

			
			//退款单项详情
			if(retInfo.get("refundItemsClassifySnap") != null){
				String jret = retInfo.get("refundItemsClassifySnap").toString();//替换成array
				char[] items = jret.toCharArray();
				items[items.length-1]=']';
				items[0]='[';
				String replaceJret = String.valueOf(items).replaceAll("\"[0-9]+\":", "");
				List<ExamItemSnapshot> jaRefunds = JSONArray.parseArray(replaceJret,ExamItemSnapshot.class);
				List<ExamItemSnapshot> jmongoRefunds =  JSONArray.parseArray(moMap.get("refundItemsClassify").toString(),ExamItemSnapshot.class);
				
				
				if(jmongoRefunds !=null && jmongoRefunds.size() > 0){
					for(int s=0;s<jmongoRefunds.size();s++){
						Assert.assertEquals(jaRefunds.get(s).getCheckState(),jmongoRefunds.get(s).getCheckState());
						Assert.assertEquals(jaRefunds.get(s).getRefundState(),jmongoRefunds.get(s).getRefundState());
						Assert.assertEquals(jaRefunds.get(s).getRefuseStatus(),jmongoRefunds.get(s).getRefuseStatus());
						Assert.assertEquals(jaRefunds.get(s).getHisId(),jmongoRefunds.get(s).getHisId());
						Assert.assertEquals(jaRefunds.get(s).getName(),jmongoRefunds.get(s).getName());
						Assert.assertEquals(jaRefunds.get(s).getPrice(),jmongoRefunds.get(s).getPrice());
						Assert.assertEquals(jaRefunds.get(s).getOriginalPrice(),jmongoRefunds.get(s).getOriginalPrice());
						Assert.assertEquals(jaRefunds.get(s).isAddExam(),jmongoRefunds.get(s).isAddExam());
						Assert.assertEquals(jaRefunds.get(s).isDiscount(),jmongoRefunds.get(s).isDiscount());
						Assert.assertEquals(jaRefunds.get(s).getTypeToMeal(),jmongoRefunds.get(s).getTypeToMeal());
						Assert.assertEquals(jaRefunds.get(s).getType(),jmongoRefunds.get(s).getType());


					}
				}
			}
		}

	}

	@DataProvider(name = "orderDetail")
	public Iterator<String[]> orderDetail() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/manage/orderDetail.csv", 10);
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
