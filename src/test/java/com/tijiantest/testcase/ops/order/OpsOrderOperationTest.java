package com.tijiantest.testcase.ops.order;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tijiantest.model.payment.trade.PayAmount;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.testcase.ops.OpsBase;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.hospital.Hospital;
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
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;
import com.tijiantest.model.payment.trade.RefundAmount;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * OPS平台->订单管理->查看/撤销/改期部分接口
 * 
 * @author huifang
 *
 */
public class OpsOrderOperationTest extends OpsBase {

	private List<Order> orderList = new ArrayList<Order>();
	private List<Order> orders = new ArrayList<Order>();
	List<Integer> accountIntList = new ArrayList<Integer>();
	private static List<Meal> mediaList = new ArrayList<Meal>();
	private int mealid = 0;
	public String examedate=null;

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws SqlException
	 */
	@Test(priority=1,description = "CRM下单", dataProvider = "crm_createOrder", groups = { "qa" })
	public void test_01_crm_createOrder(String... args) throws ParseException, IOException, SqlException {
		try{
			System.out.println("-----------------------CRM下单Start-----------------------");
			MyHttpClient crmClient = new MyHttpClient();
			onceLoginInSystem(crmClient, Flag.CRM,defCrmUsername,defCrmPasswd);
			// idCard
			String idCards = args[1].replace("#", ",");
			// mealId

			Integer companyId = CrmBase.defSKXCnewcompany.getId();
			String companyName = CrmBase.defSKXCnewcompany.getName();
			Meal sankeCrmCommonMeal = ResourceChecker.createMeal(crmClient, companyId, "散客OPS测试通用套餐", 2,1);
			mediaList.add(sankeCrmCommonMeal);
			mealid = sankeCrmCommonMeal.getId();
			// examDate
			examedate = args[3];
			// accountNames
			String accountnames = "\'" + args[4].replace("#", "\',\'") + "\'";
			// group
			String group = args[5];
			// companyId
			//companyId = Integer.parseInt(args[6]);

			// step1:导入用户
			AccountChecker.uploadAccount(crmClient, companyId, CrmBase.defhospital.getId(), group,
					"./csv/order/ops/opsorder.xlsx",AddAccountTypeEnum.idCard);

			// step2:获取导入用户accountid
			String sql1 = "SELECT  DISTINCT a.id FROM tb_account a,tb_examiner r WHERE r.id_card in (" + idCards
					+ ") AND a.id = r.customer_id " + "AND r.igroup = \'" + group + "\' AND r.name in (" + accountnames
					+ ") AND r.manager_id = ?";
			log.info("sql:" + sql1);
			List<Map<String, Object>> aclist = DBMapper.query(sql1, CrmBase.defaccountId);
			for (Map<String, Object> al : aclist) {
				accountIntList.add((Integer) al.get("id"));
			}

			log.info("accountlist:" + accountIntList);
			Meal meal = ResourceChecker.getMealInfo(mealid);
			BatchOrderBody batchBody = new BatchOrderBody(CrmBase.defhospital.getId(),
					CrmBase.defhospital.getName(), meal.getId(), meal.getPrice(), meal.getGender(), meal.getName(),
					companyId,companyName, accountIntList, examedate);
			String jbody = JSON.toJSONString(batchBody);

			// step4:批量下单
			HttpResult response = crmClient.post(Order_BatchOrder, jbody);

			//Assert
			//Assert.assertEquals(response.getBody(), "{}");
			Assert.assertEquals(response.getCode() , HttpStatus.SC_OK,"错误原因:"+response.getBody());
			Integer processId = JsonPath.read(response.getBody(),"$.result");

//		onceLoginInSystem(crmClient, Flag.CRM);
			OrderChecker.waitBatchOrderProc(crmClient,processId.intValue());

			// check database
			if (checkdb) {
				for (Order order : checkOrder(accountIntList)) {
					orderList.add(order);
					Assert.assertTrue(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
							|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
					if (checkmongo) {
						List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}",
								MONGO_COLLECTION);
						Assert.assertNotNull(list);
						Assert.assertEquals(1, list.size());
					}
				}

			}
			onceLogOutSystem(crmClient, Flag.CRM);// 退出CRM
			System.out.println("-----------------------CRM下单End-----------------------");
		}catch (AssertionError e){
			//CRM模块未开启
			log.error("CRM-APP未开启");
			if(orderList.size() == 0){
				List<Order> crmList = OrderChecker.getCrmBookedOrderList(defHospitalId);
				if(crmList == null || crmList.size() == 0){
					log.error("无法找到CRM端订单,请手动创建CRM端订单（或者开启CRM-APP模块）");
					return;
				}
				 int id = crmList.get(0).getId();
				 Order o = OrderChecker.getOrderInfo(id);
				accountIntList.add(o.getOrderAccount().getId());
				orderList.add(o);
				mealid = o.getOrderMealSnapshot().getMealSnapshot().getId();
				examedate = simplehms.format(o.getExamDate());
				log.info("orderList列表.."+o.getId()+"套餐"+mealid+"用户"+o.getOrderAccount().getId()+"时间"+examedate);
			}


		}

	}

	@Test(priority=2,description = "ops订单查看", groups = { "qa" })
	public void test_02_ops_watchOrder() throws SqlException {
		System.out.println("--------------------------ops查看订单开始-------------------------------------");
		//List<String> orderNums = new ArrayList<String>();
		Integer processId = OrderChecker.getRecentOrderProcess();
		System.err.println("最近一次下单成功记录为："+processId);
		BatchOrderProcess process = OrderChecker.getBatchProcessById(processId);
		List<BatchOrderProcessRecord> records = process.getRecords();
		records.forEach(record->{
			Order order = new Order();
			order = OrderChecker.getOrders("order_num",record.getOrderNum());
			order.setOrderNum(record.getOrderNum());
			orders.add(order);
		});;
		
		//获取套餐信息
		OrderMealSnapshot mealSnapshot = OrderChecker.getMealSnapShotByOrder(orders.get(0).getId());
		int mealId = mealSnapshot.getMealSnapshot().getOriginMeal().getId();
		
		//orderList=BatchOrderTest.orderList;
		// step5:查看订单
		log.info("crm创建orderList:" + orders);
		int orderId = orders.get(0).getId();
		HttpResult response = httpclient.get(Flag.OPS, OpsOrder_WatchOrder,  orderId + "");
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
	    JSONObject retInfo = JSON.parseObject(body);

		if (checkdb) {
			String sql = "select item_id  from tb_meal_examitem where meal_id = ? and selected = 1 order by item_id";
			List<Map<String, Object>> list = DBMapper.query(sql, mealId);
			for (int i = 0; i < list.size(); i++) {
				int itemid = Integer.parseInt(list.get(i).get("item_id").toString());
				Assert.assertEquals(itemid,
						Integer.parseInt(JsonPath.read(body, "$.itemSnap." + itemid + ".id").toString()));
			}

		}
		
		 Order order = OrderChecker.getOrderInfo(orderId);
		 //精度
		 String calculator = HospitalChecker.getHospitalSetting(order.getHospital().getId(), HospitalParam.CALCULATOR_SERVICE).get(HospitalParam.CALCULATOR_SERVICE).toString();
		 if(calculator == null ||calculator.equals("") ||calculator.equals("defaultCalculator"))
			   Assert.assertEquals(retInfo.get("currency").toString(),"元");
		 else if(calculator.equals("fenRoundCalculator") )
			   Assert.assertEquals(retInfo.get("currency").toString(),"分");
		 else
			   Assert.assertEquals(retInfo.get("currency").toString(),"角");
		 //应退客户的金额
		 RefundAmount refunds = PayChecker.getRefundAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
		 long amount = refunds.getOnlineRefundAmount();
		 if(amount != 0 )
			 Assert.assertEquals(Long.parseLong(retInfo.get("customerPayRefund").toString()),amount);
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

		//调整金额显示
		Assert.assertEquals(retOrderInfo.getJSONObject("orderExtInfo").getIntValue("adjustPrice"),((BasicDBObject)moMap.get("orderExtInfo")).getInt("adjustPrice"));
		//优惠金额
		if(checkdb){
			String orderNum = moMap.get("orderNum").toString();
			PayAmount payAmount = PayChecker.getPayAmountByOrderNum(orderNum,PayConstants.OrderType.MytijianOrder);
			long dbCoupAmount = payAmount.getHospitalCouponAmount()+payAmount.getPlatformCouponAmount()+payAmount.getChannelCouponAmount();
			Assert.assertEquals(retInfo.getLongValue("couponAmount"),dbCoupAmount);
		}

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
		JSONObject jremark = JSONObject.parseObject(retOrderInfo.get("remark").toString());
		if(retOrderInfo.get("remark") !=null){
			JSONObject jmongoremark = JSONObject.parseObject(moMap.get("remark").toString());
			Assert.assertEquals(jremark.getString("remarks"),jmongoremark.getString("remarks"));
			Assert.assertEquals(jremark.getString("timeRemarks"),jmongoremark.getString("timeRemarks"));
		}
		Assert.assertEquals(retOrderInfo.getString("orderNum"),moMap.get("orderNum"));

		//订单状态
		Assert.assertEquals(retOrderInfo.getIntValue("status"),Integer.parseInt(moMap.get("status").toString()));
		
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
		}


		System.out.println("--------------------------ops查看订单结束-------------------------------------");
	}

	@Test(priority=3,description = "ops获取订单单项id数组", groups = { "qa" })
	public void test_03_ops_orderExamItems() throws SqlException {
		System.out.println("--------------------------ops获取订单单项id数组开始-------------------------------------");
		// step6:订单改期
		HttpResult response = httpclient.get(Flag.OPS, OpsOrder_OrderExamItems, orderList.get(0).getOrderNum() + "");
		String body = response.getBody();
		System.out.println(body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);		
		String rets = body.toString();
		log.info("单项id list:" + body);
		if (checkdb) {
			String sql = "select item_id  from tb_meal_examitem where meal_id = ? and selected = 1 order by item_id";
			List<Map<String, Object>> list = DBMapper.query(sql, mealid);
			String itemids = "";
			for (int i = 0; i < list.size(); i++) {
				int itemid = Integer.parseInt(list.get(i).get("item_id").toString().trim());
				itemids = itemids + itemid + ",";
			}
			Assert.assertEquals(itemids.substring(0, itemids.length() - 1), rets.substring(1, rets.length() - 1));

		}
		System.out.println("--------------------------ops获取订单单项id数组结束-------------------------------------");
	}

	@Test(priority=4,dataProvider = "changeExamDate", groups = { "qa" })
	public void test_04_changeExamDate(String... args) throws SqlException, ParseException {
		System.out.println("-----------------------------ops端改期测试Start---------------------------");
		String oldExamDate = examedate;
		examedate = args[1];
		System.out.println("newExamDate:" + examedate);
		orderList=orderList.stream().filter(order->!order.getIsExport()).collect(Collectors.toList());
		if (orderList.size()>0) {
			for(Order order:orderList){
				String newDateIntervalId = order.getExamTimeIntervalId().toString();
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", String.valueOf(order.getId()));
				map.put("examDate", examedate);
				map.put("intervalId", newDateIntervalId);
				
				//获取单位/体检中心人数
				List<HospitalCapacityUsed> oldDateCounter = CounterChecker.getHospitalCount(order.getHospital().getId(),oldExamDate)
						.stream().filter(hcu->hcu.getPeriodId().intValue()==order.getExamTimeIntervalId()).collect(Collectors.toList());
				List<HospitalCapacityUsed> newDateCounter = CounterChecker.getHospitalCount(order.getHospital().getId(),examedate)
						.stream().filter(hcu->hcu.getPeriodId().intValue()==Integer.valueOf(newDateIntervalId)).collect(Collectors.toList());
				List<CompanyCapacityUsed> oldDateCompanyCounter = null;
				List<CompanyCapacityUsed> newDateCompanyCounter = null;
				oldDateCompanyCounter=CounterChecker.getCompanyCount(order.getHospital().getId(),order.getExamCompanyId(),oldExamDate)
						.stream().filter(ccu->ccu.getPeriodId().intValue()==order.getExamTimeIntervalId()).collect(Collectors.toList());
				newDateCompanyCounter=CounterChecker.getCompanyCount(order.getHospital().getId(),order.getExamCompanyId(),examedate)
						.stream().filter(ccu->ccu.getPeriodId().intValue() ==Integer.valueOf(newDateIntervalId)).collect(Collectors.toList());

				HttpResult result = httpclient.post(Flag.OPS, OpsOrder_ChangeExamDate, map);
				System.out.println(result.getBody());
				Integer orderId = order.getId();
				System.out.println("ops端改期orderId：" + orderId);
				String sql = "select * from tb_order where id=?";
				List<Map<String, Object>> list = DBMapper.query(sql, orderId);
				Map<String, Object> map2 = list.get(0);
				
				System.out.println("orderId=" + orderId + "   orderNum=" + map2.get("order_num").toString() + "  orderDate=" + map2.get("exam_date").toString()
						+ "       status=" + map2.get("status").toString() + "      is_export=" + map2.get("is_export")
						+ "      soruce=" + map2.get("source"));
				
				if (checkdb) {
					// 验证订单操作日志
					List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
					Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.CHANGE_DATE.getCode());
					Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.OPS.getCode());
					
					List<HospitalCapacityUsed> oldDateCounter1 = CounterChecker.getHospitalCount(order.getHospital().getId(),oldExamDate)
							.stream().filter(hcu->hcu.getPeriodId().intValue()==order.getExamTimeIntervalId()).collect(Collectors.toList());
					List<HospitalCapacityUsed> newDateCounter1 = CounterChecker.getHospitalCount(order.getHospital().getId(),examedate)
							.stream().filter(hcu->hcu.getPeriodId().intValue()==Integer.valueOf(newDateIntervalId)).collect(Collectors.toList());
					List<CompanyCapacityUsed> oldDateCompanyCounter1 = null;
					List<CompanyCapacityUsed> newDateCompanyCounter1 = null;
					oldDateCompanyCounter1=CounterChecker.getCompanyCount(order.getHospital().getId(),order.getExamCompanyId(),oldExamDate)
							.stream().filter(ccu->ccu.getPeriodId().intValue()==order.getExamTimeIntervalId()).collect(Collectors.toList());
					newDateCompanyCounter1=CounterChecker.getCompanyCount(order.getHospital().getId(),order.getExamCompanyId(),examedate)
							.stream().filter(ccu->ccu.getPeriodId().intValue()==Integer.valueOf(newDateIntervalId)).collect(Collectors.toList());
					CounterChecker.recycleCounterCheck(-1,oldDateCompanyCounter,oldDateCompanyCounter1,oldDateCounter,oldDateCounter1,1);
					CounterChecker.reduceCounter(-1,newDateCompanyCounter,newDateCompanyCounter1,newDateCounter,newDateCounter1,1);//验证新体检日人数扣除
				}
			}
		}
		System.out.println("-----------------------------ops端改期测试End---------------------------");
	}
	
	@Test(priority=5,description = "ops撤销订单", groups = { "qa" })
	public void test_05_revokeOrder() throws SqlException {
		System.out.println("--------------------------ops撤销订单开始-------------------------------------");
		/***** 撤销订单 ******/
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		log.info("orderList:" + orderList);
		Integer newCompanyId = orderList.get(0).getExamCompanyId();
		Integer hospitalId = orderList.get(0).getHospital().getId();
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds[]", orderIds.substring(0, orderIds.length() - 1));
			params.add(nvp);
		}
		
		//撤单前获取单位/体检中心余量
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(hospitalId,newCompanyId,examedate);
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(hospitalId,examedate);

		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_RevokeOrder, params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "ops撤单失败:" + result.getBody());
		Assert.assertEquals(result.getBody(), "");
		// database
		if (checkdb) {
			waitto(5);
			String sql = "SELECT * FROM tb_order WHERE id in (" + orderIds.substring(0, orderIds.length() - 1) + ")";
			List<Map<String, Object>> retlist = DBMapper.query(sql);
			for (Map<String, Object> r : retlist)
				Assert.assertEquals(Integer.parseInt(r.get("status").toString()), OrderStatus.REVOCATION.intValue());
			
			//撤单后获取单位/体检中心余量
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(hospitalId,newCompanyId,examedate);
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(hospitalId,examedate);
			CounterChecker.recycleCounterCheck(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,orderList.size());
		}
		log.info("订单已撤销!");

		try{
			MyHttpClient crmClient = new MyHttpClient();
			onceLoginInSystem(crmClient, Flag.CRM,defCrmUsername,defCrmPasswd); // crm登陆
			/***** 移除用户 ******/
			String acStr = "";
			for (Integer ai : accountIntList) {
				acStr = acStr + ai + ",";
			}
//		Map<String, Object> paramss = new HashMap<String, Object>();
			int organizationId = CrmBase.defhospital.getId();
			int organizationType = HospitalChecker.getOrganizationType(organizationId);


			List<NameValuePair> parames = new ArrayList<NameValuePair>();

			parames.add(new BasicNameValuePair("accountIds", acStr.substring(0, acStr.length()-1)+""));
			parames.add(new BasicNameValuePair("newCompanyId", newCompanyId+""));
			parames.add(new BasicNameValuePair("organizationType", organizationType+""));
			HttpResult delete = crmClient.post(Account_RemoveCustomer, parames);


			// assert
			Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);
			Assert.assertEquals(delete.getBody(), "{\"result\": " + accountIntList.size() + "}");
			onceLogOutSystem(crmClient, Flag.CRM); // crm退出登录
		}catch (AssertionError e){
			log.info("CRM-APP未开启");
		}

		System.out.println("--------------------------ops撤销订单结束-------------------------------------");
	}

	@AfterClass(description = "回到登陆ops", groups = { "qa" }, alwaysRun = true)
	public static void afterClass() {
		try{
			MyHttpClient crmClient = new MyHttpClient();
			onceLoginInSystem(crmClient, Flag.CRM,defCrmUsername,defCrmPasswd); // crm登陆
			String mealId = null;
			for (int i = 0; i < mediaList.size(); i++) {
				mealId = mediaList.get(i).getId().toString();
				HttpResult response = crmClient.get(Meal_DeleteMeal, mealId);
				System.out.println("response of delete:......" + response.getBody());
				Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
				log.info("id为" + mealId + "的套餐已经删除");
			}

			onceLogOutSystem(crmClient, Flag.CRM); // crm退出登录
		}catch (AssertionError e){
			log.error("CRM-APP未开启");
		}

	}

	@DataProvider
	public Iterator<String[]> crm_createOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/ops/opsOrder_success.csv", 10);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
	
	@DataProvider
	public Iterator<String[]> changeExamDate() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/ops/opsChangeExamDate.csv", 3);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Table tb_order
	 */
	public List<Order> checkOrder(List<Integer> accountlist) {
		List<Order> retlist = new ArrayList<Order>();
		String accounts = "(" + ListUtil.IntegerlistToString(accountlist) + ")";

		String sqlStr = "select id,order_num,status,old_exam_company_id,hospital_company_id,hospital_id,is_export,exam_time_interval_id from tb_order where account_id in "+accounts+" order by insert_time desc limit 1" ;

		log.info("sql:" + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				Order order = new Order();
				
		    	order.setId(Integer.parseInt(m.get("id").toString()));
		    	order.setOrderNum(m.get("order_num").toString());
		    	order.setStatus(Integer.parseInt(m.get("status").toString()));
		    	if(m.get("old_exam_company_id")!=null)
		    		order.setOldExamCompanyId(Integer.valueOf(m.get("old_exam_company_id").toString()));
		    	order.setExamTimeIntervalId(Integer.valueOf(m.get("exam_time_interval_id").toString()));
		    	order.setExamCompanyId(Integer.valueOf(m.get("hospital_company_id").toString()));
		    	order.setHospital(new Hospital(Integer.valueOf(m.get("hospital_id").toString())));
		    	order.setIsExport(Integer.valueOf(m.get("is_export").toString())==1?true:false);
		    	retlist.add(order);
			}

			System.out.println("relist:" + retlist);
			return retlist;

		} catch (SqlException e) {
			log.error("catch exception while get order status from db!", e);
			e.printStackTrace();
		}
		return null;
	}

}
