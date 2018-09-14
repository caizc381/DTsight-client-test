package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.beust.jcommander.internal.Lists;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.BatchOrderBody;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.model.order.BatchOrderProcessRecord;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.OperateAppEnum;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;


/**
 * CRM直接预约->(订单&用户）查询订单->确认客户已到检
 * 
 * 批量下单-->查看订单
 * @author huifang
 *
 */
public class OrderOperationTest extends CrmMediaBase{

	private static List<Order> orderll = new ArrayList<Order>();
	List<Integer>accountIntList = new ArrayList<Integer>();
	private int mealid = 0 ;
	private int companyId = 0;
	public static String examedate=null;
	@Test(priority=0,description= "批量下单",dataProvider="watchOrder_success",groups = {"qa"})
	public void test_00_order_success(String ...args) throws Exception{
		System.out.println("---------------------------批量下单开始---------------------------");
		//idCard
		String idCards = args[1].replace("#", ",");
		//mealId
		mealid = sankeCommonMeal.getId();
		//examDate
		examedate = args[3];
		//accountNames
		String accountnames = "\'"+args[4].replace("#","\',\'")+"\'";
		//group
		String group = args[5];
		//companyId
		companyId = defnewcompany.getId();
		
		HospitalCompany hospitalCompany = CompanyChecker.getHospitalCompanyById(companyId);
//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());

		//step1:导入用户
	    AccountChecker.uploadAccount(httpclient, hospitalCompany.getId(), defhospital.getId(), group, "./csv/order/watchorder.xlsx",AddAccountTypeEnum.idCard);
	    //step2:获取导入用户accountid
	    String sql1 = "SELECT  DISTINCT a.id FROM tb_account a,tb_examiner r WHERE r.id_card in ("+idCards+") AND a.id = r.customer_id "
	    		+ "AND r.igroup = \'"+group+"\' AND r.name in ("+accountnames+") AND r.manager_id = ?";
	    log.info("sql:"+sql1);
	    List<Map<String,Object>>aclist = DBMapper.query(sql1,defaccountId);
	    for(Map<String,Object> al : aclist){
	    	accountIntList.add((Integer)al.get("id"));
	    }
	    
	    log.info("accountlist:"+accountIntList);
		Meal meal = ResourceChecker.getMealInfo(mealid);
		BatchOrderBody batchBody = new BatchOrderBody(defhospital.getId(), defhospital.getName(),
				meal.getId(), meal.getPrice(), meal.getGender(),meal.getName(), hospitalCompany.getId(),hospitalCompany.getName(), accountIntList, examedate);
		if(companyId == defSKXCnewcompany.getId())
			batchBody.setSitePay(true);
		else
			batchBody.setSitePay(false);
		String jbody = JSON.toJSONString(batchBody);
		
		//step3:创建套餐
		//xqa 12002
		//qa 13531
		
		//step4:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);
		log.info("body..."+response.getBody());
	    //Assert
		//Assert.assertEquals(response.getBody(), "{}");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");
		Integer failCount =0 ;

		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		//check database
		if(checkdb){
			waitto(mysqlWaitTime);
			for(Order order : OrderChecker.checkOrder(accountIntList)){
				orderll.add(order);
				log.info("订单id..."+order.getId()+"...订单状态..."+order.getStatus());
				Assert.assertTrue(order.getStatus() ==  OrderStatus.ALREADY_BOOKED.intValue()|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
				//验证交易
				PayChecker.checkCrmBatchOrderTradeOrder(defhospital.getId(), hospitalCompany, order,defaccountId);
				if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
				}
			}
		
		}
		System.out.println("---------------------------批量下单结束---------------------------");
	}
	
	@Test(priority=2,description = "订单查看" ,groups = {"qa"})
	public void test_01_watchOrder() throws SqlException{
		System.out.println("---------------------------查看订单开始---------------------------");
		//step5:查看订单
		int orderId = orderll.get(0).getId();
		HttpResult response = httpclient.get(Order_WatchOrder,orderId+"");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
	    JSONObject retInfo = JSON.parseObject(body);

		if(checkdb){
			String sql = "select item_id  from tb_meal_examitem where meal_id = ? and selected = 1 order by item_id";
			List<Map<String,Object>> list = DBMapper.query(sql,mealid);
			for(int i = 0 ;i<list.size(); i++){
				int itemid = Integer.parseInt(list.get(i).get("item_id").toString());
				Assert.assertEquals(itemid,Integer.parseInt(JsonPath.read(body,"$.itemSnap."+itemid+".id").toString()));
			}
					
		}
		
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
			
			//是否显示手动退款按钮
			Assert.assertEquals(Boolean.parseBoolean(retInfo.get("showManualRefund").toString()),OrderChecker.isRefundButtonShow(order, defaccountId));
		}


	
		System.out.println("---------------------------批量下单结束---------------------------");
	}
	
	@Test(priority=3,description = "获取订单单项id数组" ,groups = {"qa"})
	public void test_03_orderExamItems() throws SqlException{
		System.out.println("---------------------------获取订单单项id数组开始---------------------------");
		//step6:订单改期
		HttpResult response = httpclient.get(Order_OrderExamItems,orderll.get(0).getId()+"");
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		String rets = body.toString();
		if(checkdb){
			String sql = "select item_id  from tb_meal_examitem where meal_id = ? and selected = 1 order by item_id";
			List<Map<String,Object>> list = DBMapper.query(sql,mealid);
			String itemids = "";
			for(int i = 0 ;i<list.size(); i++){
				int itemid = Integer.parseInt(list.get(i).get("item_id").toString().trim());
				itemids = itemids + itemid + ",";
			}
			Assert.assertEquals(itemids.substring(0, itemids.length()-1),rets.substring(1, rets.length()-1));
					
		}
		System.out.println("---------------------------获取订单单项id数组结束---------------------------");
	}

	@Test(priority=1,description = "确认客户已到检" ,groups = {"qa"})
	public void test_05_confirmmedical() throws SqlException{
		System.out.println("---------------------------确认客户已到检开始---------------------------");
		Integer orderId = orderll.get(0).getId();
		Map<String,String> map = new HashMap<>();
		map.put("orderId", orderId.toString());
//		String jsonObj = JSON.toJSONString(orderId);
		HttpResult response = httpclient.post(Order_confirmmedical,map);
		System.out.println(response.getBody());
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
//		String body = response.getBody();
//		String rets = body.toString();
		if(checkdb){
			waitto(mysqlWaitTime);
			String sql = "SELECT order_num,is_export FROM tb_order WHERE id = ?;";
			List<Map<String,Object>> list = DBMapper.query(sql,orderId);
			Assert.assertEquals(Integer.parseInt(list.get(0).get("is_export").toString()), 1);
			
			String order_num = list.get(0).get("order_num").toString();
			//验证订单操作日志
			  List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order_num);
		   	  Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.RXPORT_ORDER.getCode());
		   	  Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CRM.getCode());
		   	  Assert.assertEquals(logs.get(0).getOperator().intValue(), defaccountId);
		   	  
			if(checkmongo){
				waitto(mongoWaitTime);
				List<Map<String,Object>> monlist = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
				Assert.assertEquals(monlist.get(0).get("isExport").toString(),"true");
			}
		}
		System.out.println("---------------------------确认客户已到检结束---------------------------");
	}

	@Test(priority=40,description = "给订单打结算标记" ,groups = {"qa"})
	public void test_04_signOrderSettle() throws SqlException{
		System.out.println("---------------------------开始标记为已经结算---------------------------");
		//订单非法请求撤销结算
		int orderId = orderll.get(0).getId();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId",defhospital.getId()+""));
		params.add(new BasicNameValuePair("orderIds[]",orderId+""));
		params.add(new BasicNameValuePair("signSettle","false"));
		HttpResult response = httpclient.post(Order_SignOrderSettle,params);
		log.info(response.getCode() +"返回"+response.getBody());
		//订单合法请求标记结算
		params.clear();
		params.add(new BasicNameValuePair("hospitalId",defhospital.getId()+""));
		params.add(new BasicNameValuePair("orderIds[]",orderId+""));
		params.add(new BasicNameValuePair("signSettle","true"));
		response = httpclient.post(Order_SignOrderSettle,params);
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK,"返回"+response.getBody());
		String body = response.getBody();
		log.info("结算标记"+body);
		if(checkmongo){
			List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
			Assert.assertNotNull(list);
			Assert.assertEquals(1, list.size());
			Map<String,Object> moMap = list.get(0);
			Assert.assertEquals(body,"\""+moMap.get("settleBatch").toString()+"\"");//结算批次号
			Assert.assertEquals(Integer.parseInt(moMap.get("settleSign").toString()),5);//标记为结算

		}
		System.out.println("---------------------------结束标记为已经结算---------------------------");
	}

	@Test(priority=50,description = "给订单撤销结算标记" ,groups = {"qa"})
	public void test_05_signOrderSettle() throws SqlException{
		System.out.println("---------------------------开始标记为撤销结算---------------------------");
		//step6:订单改期
		int orderId = orderll.get(0).getId();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId",defhospital.getId()+""));
		params.add(new BasicNameValuePair("orderIds[]",orderId+""));
		params.add(new BasicNameValuePair("signSettle","false"));
		HttpResult response = httpclient.post(Order_SignOrderSettle,params);
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK,"返回"+response.getBody());
		String body = response.getBody();
		log.info("撤销结算标记"+body);
		Assert.assertTrue(body.equals("")||body.equals("{}"));
		if(checkmongo){
			List<Map<String,Object>> list = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
			Assert.assertNotNull(list);
			Assert.assertEquals(1, list.size());
			Map<String,Object> moMap = list.get(0);
			Assert.assertEquals(Integer.parseInt(moMap.get("settleSign").toString()),6);//标记为撤销结算

		}
		System.out.println("---------------------------结束标记为撤销结算---------------------------");
	}


	@Test(priority=99,description="撤销订单",groups = {"qa"})
	//@AfterClass(alwaysRun = true)
	 public static void  test_revokeOrder() throws SqlException{
		System.out.println("---------------------------撤销订单开始---------------------------");
		/*****撤销订单******/
//		Integer oldCompanyId = orderll.get(0).getOldExamCompanyId();
		Integer newCompanyId = orderll.get(0).getExamCompanyId();
		System.out.println("newCompanyId:"+newCompanyId);
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),newCompanyId,examedate);
		//撤单前获取单位/体检中心余量
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		log.info("orderll:"+orderll);
		String orderIds = "";
		List<Integer> successList = Lists.newArrayList();
		if(orderll.size()!=0){
			for(Order order : orderll){
				orderIds = orderIds + order.getId() + ",";
				successList.add(order.getId());
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds[]", orderIds.substring(0, orderIds.length()-1));
			params.add(nvp);
		}
		 	params.add(new BasicNameValuePair("sendMsg", "false"));
			log.info("开始检测这个方法的撤单问题...");
			HttpResult result = httpclient.post(Order_RevokeOrder, params);

			Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"撤单失败:"+result.getBody());
			log.info("结束检测这个方法的撤单问题...");

			List<Integer> successes = JsonPath.read(result.getBody(),"$.successes");
			Assert.assertEquals(successes, successList);
			
			//Assert.assertEquals(result.getBody(),"{}");
			//database
			if(checkdb){
				//验证单位/体检中心人数			
				List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),examedate);
				List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),newCompanyId,examedate);	
				CounterChecker.recycleCounterCheck(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,orderll.size());
				waitto(5);
				String sql = "SELECT * FROM tb_order WHERE id in (" + orderIds.substring(0, orderIds.length()-1)+")";
				List<Map<String,Object>> retlist = DBMapper.query(sql);
				for(Map<String,Object> r :retlist)
					Assert.assertEquals(Integer.parseInt(r.get("status").toString()), OrderStatus.REVOCATION.intValue());

			}
			System.out.println("---------------------------撤销订单结束---------------------------");

			System.out.println("---------------------------订单已经撤销再次撤销应该提示错误 开始---------------------------");
			result = httpclient.post(Order_RevokeOrder, params);
			log.info("再次撤销返回..."+result.getBody());
			Assert.assertEquals(result.getCode(),HttpStatus.SC_BAD_REQUEST,"撤单失败:"+result.getBody());
			Assert.assertTrue(result.getBody().contains("撤单失败,原因：此状态订单无法撤销"),"返回值"+result.getBody());

			System.out.println("---------------------------订单已经撤销再次撤销应该提示错误 结束---------------------------");

	}
	
	@DataProvider
	public Iterator<String[]> watchOrder_success(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/watchOrder_success.csv",10);
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