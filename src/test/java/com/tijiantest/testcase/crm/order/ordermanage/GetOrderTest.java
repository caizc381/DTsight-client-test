package com.tijiantest.testcase.crm.order.ordermanage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 单位订单->查看订单
 * @author huifang
 *
 */
public class GetOrderTest extends CrmBase{
	
	@Test(groups = {"qa"}, description = "CRM->单位体检->查看订单详情")
	public void test_01_getOrderTEST()throws IOException, ParseException, java.text.ParseException, SqlException {
		if (checkmongo) {
			String queryExamCompanyId = "'examCompanyId':{'$eq':" + defSKXCnewcompany.getId() + "}";
			String querySql = "{" + queryExamCompanyId + ", 'managerId':" + defaccountId + "}";
			List<Map<String, Object>> mongoList = MongoDBUtils.queryByPage(querySql, "insertTime", -1, 0,
					10, MONGO_COLLECTION);
			//取第1条mongo记录
			Map<String, Object> mogoMap = mongoList.get(0);
			int orderId = Integer.parseInt(mogoMap.get("id").toString());
			// make parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("orderId", orderId+""));
			//get
		   HttpResult response = httpclient.get(Order_GetCrmOrder, params);
		   //assert
		   Assert.assertEquals(response.getCode() , HttpStatus.SC_OK,"错误原因:"+response.getBody());
		   String body = response.getBody();
		   JSONObject retInfo = JSON.parseObject(body);
		   log.info("返回.."+body);
		   //精度
		   String calculator = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.CALCULATOR_SERVICE).get(HospitalParam.CALCULATOR_SERVICE).toString();
		   if(calculator == null || calculator.equals("defaultCalculator"))
			   Assert.assertEquals(retInfo.get("currency").toString(),"元");
		   else if(calculator.equals("fenRoundCalculator"))
			   Assert.assertEquals(retInfo.get("currency").toString(),"分");
		   else
			   Assert.assertEquals(retInfo.get("currency").toString(),"角");
		   Order order = OrderChecker.getOrderInfo(orderId);
		   //应退客户的金额
		   RefundAmount refunds = PayChecker.getRefundAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
		   long amount = refunds.getOnlineRefundAmount();
		   if(amount != 0 )
			 Assert.assertEquals(Long.parseLong(retInfo.get("customerPayRefund").toString()),amount);
		   if(checkmongo){
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
			   if(((BasicDBObject)moMap.get("examiner")).getString("address") !=null )
				   Assert.assertEquals(retOrderInfo.getJSONObject("examiner").getString("address"),
						   ((BasicDBObject)moMap.get("examiner")).getString("address"));
			   Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("genderLabel"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("genderLabel"));
			   Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("marriageStatusLabel"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("marriageStatusLabel").toString());
			   if(((BasicDBObject)moMap.get("orderExportExtInfo")).getString("exportExamDate") !=null )
			  	 Assert.assertEquals(retOrderInfo.getJSONObject("orderExportExtInfo").getString("exportExamDate"),((BasicDBObject)moMap.get("orderExportExtInfo")).get("exportExamDate").toString());


			   //预约信息
			Assert.assertEquals(retOrderInfo.getString("mealName"),moMap.get("mealName"));
			   Assert.assertEquals(retOrderInfo.getJSONObject("orderExtInfo").getString("examCompany"),((BasicDBObject)moMap.get("orderExtInfo")).get("examCompany"));
			Assert.assertEquals(retOrderInfo.getString("examTimeIntervalName"),moMap.get("examTimeIntervalName"));
			Assert.assertEquals(retOrderInfo.getString("operator"),moMap.get("operator"));
			Assert.assertEquals(retOrderInfo.getJSONObject("orderHospital").getString("name"),((BasicDBObject)moMap.get("orderHospital")).getString("name"));
			if(retOrderInfo.get("remark")!=null){
				JSONObject jremark = JSONObject.parseObject(retOrderInfo.get("remark").toString());	
				JSONObject jmongoremark = JSONObject.parseObject(moMap.get("remark").toString());
				Assert.assertEquals(jremark.getString("remarks"),jmongoremark.getString("remarks"));
				Assert.assertEquals(jremark.getString("timeRemarks"),jmongoremark.getString("timeRemarks"));
			}
			Assert.assertEquals(retOrderInfo.getString("orderNum"),moMap.get("orderNum"));

			//订单状态
			Assert.assertEquals(retOrderInfo.getIntValue("status"),Integer.parseInt(moMap.get("status").toString()));

			//调整金额显示
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
				List<ExamItemSnap> jaMealItems = JSONArray.parseArray(replaceJret,ExamItemSnap.class);
				net.sf.json.JSONObject examItemSnapshotInMongoMeal = OrderChecker.getMongoMealSnapshot(moMap.get("orderNum").toString(),"examItemSnapshot");
				String examItemSnapStr = examItemSnapshotInMongoMeal.getString("value");
				List<ExamItemSnap> jmongoItems =  JSONArray.parseArray(examItemSnapStr,ExamItemSnap.class);
				
				 Collections.sort(jmongoItems, new Comparator<ExamItemSnap>() {
				    	@Override
				    	public int compare(ExamItemSnap o1,
				    			ExamItemSnap o2) {
				    		return o1.getName().compareTo(o2.getName());				    	}
					});
					
				 Collections.sort(jaMealItems, new Comparator<ExamItemSnap>() {
				    	@Override
				    	public int compare(ExamItemSnap o1,
				    			ExamItemSnap o2) {
				    		return o1.getName().compareTo(o2.getName());				    	}
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
			//是否显示手动退款按钮
			Assert.assertEquals(Boolean.parseBoolean(retInfo.get("showManualRefund").toString()),OrderChecker.isRefundButtonShow(order, defaccountId));

		}		
		
	  }
	}
}
