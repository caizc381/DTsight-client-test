package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.*;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealExamitemGroup;
import com.tijiantest.model.resource.meal.MealSnap;
import com.tijiantest.util.ListUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.FileAccountImportInfo;
import com.tijiantest.model.account.ModifyAccountType;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.CompanyCapacityUsed;
import com.tijiantest.model.counter.HospitalCapacityUsed;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.validator.GenderIdCardValidator;
import com.tijiantest.util.validator.MobileValidator;

import static com.tijiantest.base.dbcheck.ResourceChecker.getMealInfo;

/**
 * 
 * @author huifang
 * 极速预约
 * case01 散客单位极速预约（直接预约）
 * case02 散客单位极速预约（改项目预约）
 * case03 体检单位极速预约（直接预约）
 * case04 体检单位极速预约（改项目预约）
 *
 */
public class FastBookTest extends CrmMediaBase{
	
	private static boolean payOnline=false;
	private int orderId = 0;
	private int healtherId = 0;
	private String nowday = sdf.format(new Date());
	private int status = 0;
	
	@Test(description="极速预约-散客直接预约",dataProvider="sanke_fastbook_1",groups = {"qa"})
	public void test_01_sanke_fastbook(String ...args) throws Exception{
		System.out.println("----------------------------极速预约-散客直接预约Start------------------------------");
		Map<String,Object> mas = parseArgs(args);
		FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
		FastBookVo fast = (FastBookVo)mas.get("bookDto");
		BookDto bookDto = fast.getBookDto();
		int mealPrice = bookDto.getMealPrice();
		int mealId = bookDto.getMealId();
		//实际订单价格
		int reducePrice = bookDto.getReducedPrice();
		System.out.println("mealPrice:"+mealPrice+";订单砍价后.."+reducePrice);
		int companyId = Integer.parseInt(mas.get("companyId").toString());
		HospitalCompany hCompany = CompanyChecker.getHospitalCompanyById(companyId);
		boolean onlinePay = Boolean.parseBoolean(mas.get("onlinePay").toString());
		List<Integer> itemList = (List<Integer>) mas.get("itemList");
		List<Integer> groupExamIds = (List<Integer>) mas.get("groupExamIds");
		String dbInitialMobile = getDBInitialMobile(customer);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));

		//Step1:极速增加体检人
		HttpResult result = httpclient.post(Account_ModifyAccount, params, JSON.toJSONString(customer));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"错误原因："+result.getBody());
		System.out.println("急速预约。。。"+result.getBody());
		healtherId = JsonPath.read(result.getBody(),"$.result");
		//下单前获取体检中心余量
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
		
		//Step2:极速下单
		String jbody = JSON.toJSONString(fast);
		params.add(new BasicNameValuePair("healtherId", healtherId+""));
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		result = httpclient.post(Order_FastBook, params,jbody);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		orderId= Integer.valueOf(JsonPath.read(result.getBody(), "$.orderId").toString());
		
		String orderSql = "select * from tb_order where account_id=?";
		List<Map<String, Object>> orderList = DBMapper.query(orderSql, healtherId);
		if (orderList.size()>0) {
			for (int i = 0; i < orderList.size(); i++) {
				//System.out.println("订单ID:"+ orderList.get(i).get("id") + "   状态："+orderList.get(i).get("status"));				
			}
			System.out.println("订单个数："+orderList.size());
		}else{
			System.out.println(healtherId+" 这个用户没有订单");
		}
		Assert.assertEquals(result.getCode() , HttpStatus.SC_OK,"错误原因："+result.getBody());
		Assert.assertNotNull(result.getBody());
		Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), reducePrice);
		
		
		if(checkdb){
			//验证用户
			checkAccount(mas,healtherId,dbInitialMobile);	
			//验证体检中心余量
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
			CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,1);
				
			//tb_order
			//散客极速预约=现场支付
			String sql3 = "select * from tb_order where account_id = ? and hospital_id = ? and status = ? and exam_date like '"+nowday+"%'  and insert_time like '"+nowday+"%'";
			
			if(payOnline){
				status=0;
			}else {
				status=2;
			}
			List<Map<String,Object>> list3 = DBMapper.query(sql3,healtherId,defhospital.getId(),status);
			Assert.assertEquals(list3.size(),1);	
			Assert.assertEquals(Integer.parseInt(list3.get(0).get("order_price").toString()),reducePrice); //直接预约，订单价格=砍价后价格
			//orderId = Integer.parseInt(list3.get(0).get("id").toString());
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString()), orderId);

			//订单快照/调整金额
			Order order = OrderChecker.getOrderInfo(orderId);
			//快照1--tb_order表
			MealSnapshot mss = JSONObject.parseObject(order.getMealDetail(),MealSnapshot.class);
			int mOrderAdjustPrice = mss.getAdjustPrice();
			//快照2---tb_exam_order_meal_snapshot表
			OrderMealSnapshot snapshot = order.getOrderMealSnapshot();
			MealSnapshot mealSnapshot = snapshot.getMealSnapshot();
			int retAdjustPrice = mealSnapshot.getAdjustPrice();
			log.info("retAdjustPrice.."+retAdjustPrice);
			Assert.assertEquals(mOrderAdjustPrice,retAdjustPrice);
			Assert.assertEquals(retAdjustPrice,
					(mealPrice - reducePrice)+ getMealInfo(mealId).getMealSetting().getAdjustPrice()); //调整金额=  砍价差额 + 套餐调整金额
			//单项列表
			Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderId)));

			//tb_paymentrecord 散客单位订单存入tb_paymentrecord
			String sql4 = "select method.name,record.amount,record.status from tb_paymentrecord record , tb_payment_method method where record.payment_method_id = method.id and record.order_id = ? ";
			List<Map<String,Object>> list4 = DBMapper.query(sql4, orderId);
			Assert.assertEquals(list4.size(),1); //散客极速预约记录1条支付记录
			if(payOnline){
				//线上支付记录，状态为0
				Assert.assertEquals(list4.get(0).get("name").toString(),"线上支付");
				Assert.assertEquals(list4.get(0).get("status").toString(), "0");
			}else {
				//线下支付记录
				Assert.assertEquals(list4.get(0).get("name").toString(),"线下支付");
				Assert.assertEquals(list4.get(0).get("status").toString(), "1");
			}
			Assert.assertEquals(Integer.parseInt(list4.get(0).get("amount").toString()), reducePrice);

			if(checkmongo){
				String moSql1 = "{'orderAccount._id':"+healtherId+",'orderHospital._id':"+defhospital.getId()+",'id':"+orderId+",'examCompanyId':"+companyId+"}";
				System.out.println("moSql1:"+moSql1);
				List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
				Assert.assertEquals(mogoList1.size(),1);
				Map<String,Object> moMap = mogoList1.get(0);
				Assert.assertEquals(reducePrice,Integer.parseInt(moMap.get("orderPrice").toString()));
				//调整金额
				MealSnapshot mealSnapshot1 = JSON.parseObject(order.getMealDetail(), MealSnapshot.class);
				Assert.assertEquals(mealSnapshot1.getAdjustPrice().intValue(),retAdjustPrice);
				
			}
			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			//交易记录验证
			PayChecker.checkCrmFastBookTradeOrder(defhospital.getId(), hCompany, order, healtherId,onlinePay,defaccountId);
		}
		System.out.println("----------------------------极速预约-散客直接预约End------------------------------");
	}

	
	@Test(description="散客极速改项预约",groups = {"qa"},dataProvider="sanke_fastbook_2")
	public void test_02_sanke_fastbook_changeItem(String...args) throws Exception{
		System.out.println("----------------------------散客极速改项预约Start------------------------------");
		Map<String,Object> mas = parseArgs(args);
		FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
		FastBookVo fast = (FastBookVo)mas.get("bookDto");
		BookDto bookDto = fast.getBookDto();
		int mealPrice = bookDto.getMealPrice();
		int mealId = bookDto.getMealId();
		Meal meal = ResourceChecker.getMealInfo(mealId);
		//实际订单价格
		int reducePrice = bookDto.getReducedPrice();
		System.out.println("改项极速预约mealPrice:"+mealPrice+";订单砍价后.."+reducePrice);

		String examItemStrs = mas.get("examItemStrs").toString();
		int companyId = Integer.parseInt(mas.get("companyId").toString());
		HospitalCompany hCompany = CompanyChecker.getHospitalCompanyById(companyId);
		boolean onlinePay = Boolean.parseBoolean(mas.get("onlinePay").toString());
		List<Integer> itemList = (List<Integer>) mas.get("itemList");
		List<Integer> groupExamIds = (List<Integer>) mas.get("groupExamIds");
		String dbInitialMobile = getDBInitialMobile(customer);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));
		
		//Step1:极速增加体检人
		HttpResult result = httpclient.post(Account_ModifyAccount, params, JSON.toJSONString(customer));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"错误原因："+result.getBody());
		System.out.println("散客改项极速添加体检人..."+result.getBody());
		healtherId = JsonPath.read(result.getBody(),"$.result");
		
		//下单前获取体检中心余量
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
		
		//Step2:极速下单
		String jbody = JSON.toJSONString(fast);
		
		params.add(new BasicNameValuePair("healtherId", healtherId+""));
		result = httpclient.post(Order_FastBook, params,jbody);
		Assert.assertEquals(result.getCode() , HttpStatus.SC_OK,"错误原因："+result.getBody());
		log.info("改项极速预约返回"+result.getBody());
		Assert.assertNotNull(result.getBody());
		orderId= Integer.valueOf(JsonPath.read(result.getBody(), "$.orderId").toString());
		Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), reducePrice);

		if(checkdb){
			//验证用户
			checkAccount(mas,healtherId,dbInitialMobile);			
			
			//验证体检中心余量
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
			CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,1);
			
			//tb_order
			if(payOnline){
				status=0;
			}else {
				status=2;
			}
			String sql3 = "select * from tb_order where account_id = ? and hospital_id = ? and status = ? and exam_date like '"+nowday+"%'  and insert_time like '"+nowday+"%'";
			List<Map<String,Object>> list3 = DBMapper.query(sql3,healtherId,defhospital.getId(),status);
			Assert.assertEquals(list3.size(),1);	
			int itemsPrice = 0;
			String sql_t = "select * from tb_examitem where id in ("+examItemStrs+")";
			List<Map<String,Object>> listt = DBMapper.query(sql_t);
			for(Map<String,Object> lt : listt)
				itemsPrice += HospitalChecker.calculator_data(defhospital.getId(),(int)(meal.getDiscount()*Integer.parseInt(lt.get("price").toString())));
			Assert.assertEquals(Integer.parseInt(list3.get(0).get("order_price").toString()),reducePrice); //改项预约，订单价格=砍价金额
			//orderId = Integer.parseInt(list3.get(0).get("id").toString());
			System.out.println("散客极速改项预约--orderId:"+orderId);
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), Integer.parseInt(list3.get(0).get("order_price").toString()));
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString()), orderId);
			
			//tb_paymentrecord 散客单位订单不存入tb_paymentrecord
			String sql4 = "select method.name,record.amount,record.status from tb_paymentrecord record , tb_payment_method method where record.payment_method_id = method.id and record.order_id = ? ";
			List<Map<String,Object>> list4 = DBMapper.query(sql4, orderId);
			Assert.assertEquals(list4.size(),1); //散客极速预约记录1条支付记录
			if(payOnline){
				//线上支付记录，状态为0
				Assert.assertEquals(list4.get(0).get("name").toString(),"线上支付");
				Assert.assertEquals(list4.get(0).get("status").toString(), "0");
			}else {
				//线下支付记录
				Assert.assertEquals(list4.get(0).get("name").toString(),"线下支付");
				Assert.assertEquals(list4.get(0).get("status").toString(), "1");
			}
			Assert.assertEquals(Integer.parseInt(list4.get(0).get("amount").toString()), reducePrice);

			//订单快照/调整金额
			Order order = OrderChecker.getOrderInfo(orderId);
			//快照1--tb_order表
			MealSnapshot mss = JSONObject.parseObject(order.getMealDetail(),MealSnapshot.class);
			int mOrderAdjustPrice = mss.getAdjustPrice();
			//快照2---tb_exam_order_meal_snapshot表
			OrderMealSnapshot snapshot = order.getOrderMealSnapshot();
			MealSnapshot mealSnapshot = snapshot.getMealSnapshot();
			int retAdjustPrice = mealSnapshot.getAdjustPrice();
			log.info("retAdjustPrice.."+retAdjustPrice);
			Assert.assertEquals(mOrderAdjustPrice,retAdjustPrice);
			Assert.assertEquals(retAdjustPrice,(itemsPrice - reducePrice)+OrderChecker.calculateCrmOrderAdjustPrice(mealId,groupExamIds));//调整金额=  砍价差额 + 套餐调整金额
			//单项列表
			Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderId)));
			if(checkmongo){
				String moSql1 = "{'orderAccount._id':"+healtherId+",'orderHospital._id':"+defhospital.getId()+",'id':"+orderId+",'examCompanyId':"+companyId+"}";
				List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
				Assert.assertEquals(mogoList1.size(),1);
				Map<String,Object> moMap = mogoList1.get(0);
				Assert.assertEquals(reducePrice,Integer.parseInt(mogoList1.get(0).get("orderPrice").toString()));
				//调整金额
				MealSnapshot mealSnapshot1 = JSON.parseObject(order.getMealDetail(), MealSnapshot.class);
				Assert.assertEquals(mealSnapshot1.getAdjustPrice().intValue(),retAdjustPrice);
			}
			
			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			
			//交易记录验证
			PayChecker.checkCrmFastBookTradeOrder(defhospital.getId(), hCompany, order, healtherId,onlinePay,defaccountId);

		}
		System.out.println("----------------------------散客极速改项预约End------------------------------");
	}
	
	
	@Test(description="单位用户极速直接预约",groups = {"qa"},dataProvider="company_fastbook_1")
	public void test_03_company_fastbook(String...args) throws Exception{
		System.out.println("----------------------------单位用户极速直接预约Start------------------------------");
		//入参
		Map<String,Object> mas = parseArgs(args);
		FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
		FastBookVo fast = (FastBookVo)mas.get("bookDto");
		int companyId = Integer.parseInt(mas.get("companyId").toString());
		BookDto bookDto = fast.getBookDto();

		int mealPrice = bookDto.getMealPrice();
		int mealId = bookDto.getMealId();
		int argsCompanyMoney = Integer.parseInt(mas.get("companyMoney").toString());
		//实际订单价格
		int reducePrice = bookDto.getReducedPrice();
		int companyMoney = (argsCompanyMoney >=reducePrice) ? reducePrice-1:argsCompanyMoney;
		System.out.println("单位用户极速预约mealPrice:"+mealPrice+";订单砍价后.."+reducePrice+";单位支付:"+companyMoney);
		HospitalCompany hCompany = CompanyChecker.getHospitalCompanyById(companyId);
		boolean onlinePay = Boolean.parseBoolean(mas.get("onlinePay").toString());
		List<Integer> itemList = (List<Integer>) mas.get("itemList");
		List<Integer> groupExamIds = (List<Integer>) mas.get("groupExamIds");
		log.info("companyId:"+companyId);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));
		String dbInitialMobile = getDBInitialMobile(customer);
		
		int entryCardBalance = 0; //入口卡金额初始化
		if(checkdb){
			//记录下入口卡金额
			String sql = "select * from tb_card where account_id = ?";
			List<Map<String,Object>> list = DBMapper.query(sql,defaccountId);
			entryCardBalance = Integer.parseInt(list.get(0).get("balance").toString());  //获取入口卡金额
		}
		//Step1:极速增加体检人
		HttpResult result = httpclient.post(Account_ModifyAccount, params, JSON.toJSONString(customer));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"错误原因："+result.getBody());
		System.out.println("单位极速预约..."+result.getBody());
		healtherId = JsonPath.read(result.getBody(),"$.result");
			
		//下单前获取单位余量
		List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
		List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
		
		//Step2:极速下单
		String jbody = JSON.toJSONString(fast);
		
		params.add(new BasicNameValuePair("healtherId", healtherId+""));
		result = httpclient.post(Order_FastBook, params,jbody);
		Assert.assertEquals(result.getCode() , HttpStatus.SC_OK,"错误原因："+result.getBody());
		Assert.assertNotNull(result.getBody());
		orderId= Integer.valueOf(JsonPath.read(result.getBody(), "$.orderId").toString());
		Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), reducePrice-companyMoney);
		
		if(checkdb){
			//验证用户
			checkAccount(mas,healtherId,dbInitialMobile);
						
			//验证单位/体检中心人数			
			List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
			List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);	
			CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,1);
			
			//tb_order 下单成功
			String sql3 = "select * from tb_order where account_id = ? and hospital_id = ? and status = ? and exam_date like '"+nowday+"%'  and insert_time like '"+nowday+"%'";
			if (payOnline) {
				status=0;
			}else {
				status=2;
			}
			List<Map<String,Object>> list3 = DBMapper.query(sql3,healtherId,defhospital.getId(),status);
			Assert.assertEquals(list3.size(),1);	
			Assert.assertEquals(Integer.parseInt(list3.get(0).get("order_price").toString()),reducePrice); //直接预约，订单价格=砍价后金额
			//orderId = Integer.parseInt(list3.get(0).get("id").toString());
			System.out.println("单位用户极速直接预约--orderId:"+orderId);
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString()), orderId);

			//订单快照/调整金额
			Order order = OrderChecker.getOrderInfo(orderId);
			//快照1--tb_order表
			MealSnapshot mss = JSONObject.parseObject(order.getMealDetail(),MealSnapshot.class);
			int mOrderAdjustPrice = mss.getAdjustPrice();
			//快照2---tb_exam_order_meal_snapshot表
			//调整金额
			OrderMealSnapshot snapshot = order.getOrderMealSnapshot();
			MealSnapshot mealSnapshot = snapshot.getMealSnapshot();
			int retAdjustPrice = mealSnapshot.getAdjustPrice();
			log.info("retAdjustPrice.."+retAdjustPrice);
			Assert.assertEquals(mOrderAdjustPrice,retAdjustPrice);
			Assert.assertEquals(retAdjustPrice,(mealPrice - reducePrice)+getMealInfo(mealId).getMealSetting().getAdjustPrice());//调整金额=  砍价差额 + 套餐调整金额
			//单项列表
			Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderId)));
			//tb_paymentrecord 单位用户极速预约会写入此表
			String sql4 = "select method.name,record.amount,record.status from tb_paymentrecord record , tb_payment_method method where record.payment_method_id = method.id and record.order_id = ? ";
			List<Map<String,Object>> list4 = DBMapper.query(sql4, orderId);
			if(companyMoney == reducePrice){
				log.info("单位付款金额："+companyMoney);
				Assert.assertEquals(list4.size(),1);
				String payname = list4.get(0).get("name").toString();
				int amount = Integer.parseInt(list4.get(0).get("amount").toString());
				Assert.assertEquals(amount,companyMoney);
				Assert.assertEquals(payname,"卡支付");
				
			}else{
				Assert.assertEquals(list4.size(),2);
				for(Map<String,Object> m : list4){
					String payname = m.get("name").toString();
					int paystatus = Integer.parseInt(m.get("status").toString());
					int amount = Integer.parseInt(m.get("amount").toString());
					if(payOnline){
						Assert.assertTrue(payname.equals("卡支付")||payname.equals("线上支付"));				
					}else {
						Assert.assertTrue(payname.equals("卡支付")||payname.equals("线下支付"));
					}
					
					if(payname.equals("卡支付")){
						Assert.assertEquals(amount,companyMoney);
					    Assert.assertEquals(paystatus, 1);
					}	
					if(payname.equals("线下支付")){
						Assert.assertEquals(amount,reducePrice-companyMoney);
					    Assert.assertEquals(paystatus, 1);
					}
					if(payname.equals("线上支付")){
						Assert.assertEquals(amount,reducePrice-companyMoney);
					    Assert.assertEquals(paystatus, 0);
					}
				}
			}
			
			
			//tb_card  主管账户入口卡金额减少
			String sql5 = "select * from tb_card where account_id = ?";
			List<Map<String,Object>> list5 = DBMapper.query(sql5,defaccountId);
			Assert.assertEquals(entryCardBalance,Integer.parseInt(list5.get(0).get("balance").toString())); //母卡金额不会马上减少，等待定时任务减少母卡金额

			if(checkmongo){
				log.info("单位订单...."+orderId);
				String moSql1 = "{'orderAccount._id':"+healtherId+",'orderHospital._id':"+defhospital.getId()+",'id':"+orderId+",'examCompanyId':"+companyId+"}";
				List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
				Assert.assertEquals(mogoList1.size(),1);
				Map<String,Object> moMap = mogoList1.get(0);
				Assert.assertEquals(reducePrice,Integer.parseInt(moMap.get("orderPrice").toString()));
				//调整金额
				//调整金额
				MealSnapshot mealSnapshot1 = JSON.parseObject(order.getMealDetail(), MealSnapshot.class);
				Assert.assertEquals(mealSnapshot1.getAdjustPrice().intValue(),retAdjustPrice);
				
			}
			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			
			//交易记录验证
			PayChecker.checkCrmFastBookTradeOrder(defhospital.getId(), hCompany, order, healtherId,onlinePay,defaccountId);

		}
		System.out.println("----------------------------单位用户极速直接预约End------------------------------");
	}
	
	
	@Test(description="单位用户极速改项预约",groups = {"qa"},dataProvider="company_fastbook_2")
	public void test_04_company_fastbook_changeItem(String...args) throws Exception{
		System.out.println("----------------------------单位用户极速改项预约Start------------------------------");
				//入参
				Map<String,Object> mas = parseArgs(args);
				FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
				FastBookVo fast = (FastBookVo)mas.get("bookDto");
				String examItemStrs = mas.get("examItemStrs").toString();
				int companyId = Integer.parseInt(mas.get("companyId").toString());
				HospitalCompany hCompany = CompanyChecker.getHospitalCompanyById(companyId);
				BookDto bookDto = fast.getBookDto();
				int mealPrice = bookDto.getMealPrice();
				int mealId = bookDto.getMealId();
				Meal meal = ResourceChecker.getMealInfo(mealId);
				//实际订单价格
				int reducePrice = bookDto.getReducedPrice();
				int companyMoney = (bookDto.getCompanyMoney() >=reducePrice) ? reducePrice-1:bookDto.getCompanyMoney();
				System.out.println("单位用户改项极速预约mealPrice:"+mealPrice+";订单砍价后.."+reducePrice+";单位支付:"+companyMoney);
				boolean onlinePay = Boolean.parseBoolean(mas.get("onlinePay").toString());
				List<Integer> itemList = (List<Integer>) mas.get("itemList");
				List<Integer> groupExamIds = (List<Integer>) mas.get("groupExamIds");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
				params.add(new BasicNameValuePair("type", ModifyAccountType.NEW+""));
				String dbInitialMobile = getDBInitialMobile(customer);
				
				int entryCardBalance = 0; //入口卡金额初始化
				if(checkdb){
					//记录下入口卡金额
					String sql = "select * from tb_card where account_id = ?";
					List<Map<String,Object>> list = DBMapper.query(sql,defaccountId);
					entryCardBalance = Integer.parseInt(list.get(0).get("balance").toString());  //获取入口卡金额
				}
				//Step1:极速增加体检人
				HttpResult result = httpclient.post(Account_ModifyAccount, params, JSON.toJSONString(customer));
				Assert.assertEquals(result.getCode(),HttpStatus.SC_OK,"错误原因："+result.getBody());
				System.out.println("单位用户急速预约..."+ result.getBody());
				healtherId = JsonPath.read(result.getBody(),"$.result");
				
				List<CompanyCapacityUsed> companyCounter = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);
				List<HospitalCapacityUsed> hospitalCounter = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
				
				//Step2:极速下单
				String jbody = JSON.toJSONString(fast);
				
				params.add(new BasicNameValuePair("healtherId", healtherId+""));
				result = httpclient.post(Order_FastBook, params,jbody);
				Assert.assertEquals(result.getCode() , HttpStatus.SC_OK,"错误原因："+result.getBody());
				Assert.assertNotNull(result.getBody());
				orderId= Integer.valueOf(JsonPath.read(result.getBody(), "$.orderId").toString());
				
				if(checkdb){
					//验证用户
					checkAccount(mas,healtherId,dbInitialMobile);
					
					//验证单位/体检中心人数			
					List<HospitalCapacityUsed> hospitalCounter1 = CounterChecker.getHospitalCount(defhospital.getId(),nowday);
					List<CompanyCapacityUsed> companyCounter1 = CounterChecker.getCompanyCount(defhospital.getId(),companyId,nowday);	
					CounterChecker.reduceCounter(-1,companyCounter,companyCounter1,hospitalCounter,hospitalCounter1,1);
					
					//tb_order 下单成功
					String sql3 = "select * from tb_order where account_id = ? and hospital_id = ? and status = ? and exam_date like '"+nowday+"%'  and insert_time like '"+nowday+"%'";
					if(payOnline){
						status=0;
					}else {
						status=2;
					}
					List<Map<String,Object>> list3 = DBMapper.query(sql3,healtherId,defhospital.getId(),status);
					Assert.assertEquals(list3.size(),1);	
					int itemsPrice = 0;
					String sql_t = "select * from tb_examitem where id in ("+examItemStrs+")";
					List<Map<String,Object>> listt = DBMapper.query(sql_t);
					for(Map<String,Object> lt : listt)
						itemsPrice += HospitalChecker.calculator_data(defhospital.getId(),(int)(meal.getDiscount()*Integer.parseInt(lt.get("price").toString())));
					Assert.assertEquals(Integer.parseInt(list3.get(0).get("order_price").toString()),reducePrice); //改项预约，订单价格=砍价后金额

					System.out.println("单位用户极速改项预约--orderId:"+orderId);
					Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString()), orderId);
					Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), reducePrice-companyMoney);
					
					//tb_paymentrecord 单位用户写入
					String sql4 = "select method.name,record.amount,record.status from tb_paymentrecord record , tb_payment_method method where record.payment_method_id = method.id and record.order_id = ? ";
					List<Map<String,Object>> list4 = DBMapper.query(sql4, orderId);
					if(companyMoney == reducePrice){
						Assert.assertEquals(list4.size(),1);
						String payname = list4.get(0).get("name").toString();
						int amount = Integer.parseInt(list4.get(0).get("amount").toString());
						Assert.assertEquals(amount,companyMoney);
						Assert.assertEquals(payname,"卡支付");
					}else{
						Assert.assertEquals(list4.size(),2);
						for(Map<String,Object> m : list4){
							String payname = m.get("name").toString();
							int paystatus = Integer.parseInt(m.get("status").toString());
							int amount = Integer.parseInt(m.get("amount").toString());
							if(payOnline){
								Assert.assertTrue(payname.equals("卡支付")||payname.equals("线上支付"));
							}else {
								Assert.assertTrue(payname.equals("卡支付")||payname.equals("线下支付"));
							}
				
							if(payname.equals("卡支付")){
								Assert.assertEquals(amount,companyMoney);
								Assert.assertEquals(paystatus, 1);
							}	
							if(payname.equals("线下支付")){
								Assert.assertEquals(amount,reducePrice-companyMoney);
								Assert.assertEquals(paystatus, 1);
							}
							if(payname.equals("线上支付")){
								Assert.assertEquals(amount,reducePrice-companyMoney);
								Assert.assertEquals(paystatus, 0);
							}
								
						}
					}
					
					//tb_card  主管账户入口卡金额减少
					String sql5 = "select * from tb_card where account_id = ?";
					List<Map<String,Object>> list5 = DBMapper.query(sql5,defaccountId);
//					Assert.assertEquals(entryCardBalance - companyMoney ,Integer.parseInt(list5.get(0).get("balance").toString())); 
					Assert.assertEquals(entryCardBalance ,Integer.parseInt(list5.get(0).get("balance").toString())); //母卡金额不马上减少，等待订单任务触发

					//订单快照/调整金额
					Order order = OrderChecker.getOrderInfo(orderId);
					//快照1--tb_order表
					MealSnapshot mss = JSONObject.parseObject(order.getMealDetail(),MealSnapshot.class);
					int mOrderAdjustPrice = mss.getAdjustPrice();
					//快照2---tb_exam_order_meal_snapshot表
					OrderMealSnapshot snapshot = order.getOrderMealSnapshot();
					MealSnapshot mealSnapshot = snapshot.getMealSnapshot();
					int retAdjustPrice = mealSnapshot.getAdjustPrice();
					log.info("retAdjustPrice.."+retAdjustPrice);
					Assert.assertEquals(mOrderAdjustPrice,retAdjustPrice);
					Assert.assertEquals(retAdjustPrice,
							(itemsPrice - reducePrice)+ OrderChecker.calculateCrmOrderAdjustPrice(mealId,groupExamIds)); //调整金额=  砍价差额 + 套餐调整金额

					//单项列表
					Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderId)));
					if(checkmongo){
						String moSql1 = "{'orderAccount._id':"+healtherId+",'orderHospital._id':"+defhospital.getId()+",'id':"+orderId+",'examCompanyId':"+companyId+"}";
						List<Map<String,Object>> mogoList1 = MongoDBUtils.query(moSql1,MONGO_COLLECTION);
						Assert.assertEquals(mogoList1.size(),1);
						Map<String,Object> moMap = mogoList1.get(0);
						Assert.assertEquals(reducePrice,Integer.parseInt(mogoList1.get(0).get("orderPrice").toString()));
						//调整金额
						MealSnapshot mealSnapshot1 = JSON.parseObject(order.getMealDetail(), MealSnapshot.class);
						Assert.assertEquals(mealSnapshot1.getAdjustPrice().intValue(),retAdjustPrice);
					}


					//验证结算
					OrderChecker.check_Book_ExamOrderSettlement(order);
					//交易记录验证
					PayChecker.checkCrmFastBookTradeOrder(defhospital.getId(), hCompany, order, healtherId,onlinePay,defaccountId);

				}

				System.out.println("----------------------------单位用户极速改项预约End------------------------------");
	}

	@AfterMethod(alwaysRun=true)
	private  void revokeOrder() throws SqlException{
		List<Integer> idList = new ArrayList<Integer>();
		if(orderId!=0)
			idList.add(orderId);
		if(idList.size()>0)
			OrderChecker.Run_CrmOrderRevokeOrder(httpclient,idList,false,true,true);
	}
	
	public String getDBInitialMobile(FileAccountImportInfo customer){
		String dbInitialMobile = null;
		String sql = "SELECT ar.id_card,ar.initial_mobile,ar.mobile "
				+ "FROM tb_examiner ar "
				+ "LEFT JOIN tb_account a ON a.id = ar.customer_id "
				+ "WHERE ar.id_card = \""+customer.getIdCard()+"\" AND ar.manager_id = ? AND ar.new_company_id = ?;";
		List<Map<String, Object>> list = null;
		System.out.println("dbInitialMobile sql:"+sql);
		try {
			list = DBMapper.query(sql,customer.getManagerId(),customer.getNewCompanyId());
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null && list.size()>0)
			if(list.get(0).get("initial_mobile")!=null)
				dbInitialMobile = list.get(0).get("initial_mobile").toString();
		return dbInitialMobile;
	}
	
	
	public static  Map<String,Object> parseArgs(String ...args){
		Map<String,Object> maps = new HashMap<String,Object>();
		boolean changeDate = Boolean.parseBoolean(args[5]);
		boolean hidePrice = Boolean.parseBoolean(args[6]);
		boolean sendMsg = Boolean.parseBoolean(args[7]);
		boolean sitePay = Boolean.parseBoolean(args[8]);
//		boolean verifyFlag = Boolean.parseBoolean(args[9]);
	    payOnline = Boolean.parseBoolean(args[18]);
		String group = args[10];
//		int idType = Integer.parseInt(args[11]);
		int gender = Integer.parseInt(args[13]);
		String idCard = new IdCardGeneric().generateGender(gender);
		String name = args[14];
		HospitalCompany hospitalCompany = new HospitalCompany();
		if(args[15].equals("DEFAULT")||args[15].equals("1585")){
			hospitalCompany = defSKXCnewcompany;
			System.out.println("hospitalCompany.ID = " + hospitalCompany.getId());
		}
		else{
			hospitalCompany = CompanyChecker.getHCompanyByOldCompanyIdANDOrgId(Integer.valueOf(args[15]),defhospital.getId());
		}
		List<Integer> examItemList = new ArrayList<Integer>();
		String examStrs = "";
		boolean isChangeItem = false;
		if(!args[16].equals("")){//改项目
			String[] exam = args[16].split("#");
			for(String e : exam){
				examStrs += e + ",";
				examItemList.add(Integer.parseInt(e));
				}
			maps.put("examItemStrs",examStrs.substring(0, examStrs.length()-1) );
			isChangeItem = true;
		}
		int companyMoney = 0;
		if(!args[17].equals("")){
			companyMoney = Integer.parseInt(args[17]);
		}
		String addAccountType = args[19];
		String employeeId = args[20];
		String mobile = args[21];
		int reduceOrderPrice = 0;
		if(!args[22].equals("")){
			reduceOrderPrice = Integer.parseInt(args[22]);
		}

		
		//BookDto
		BookDto book = new BookDto();
		if (hospitalCompany.getId() == defSKXCnewcompany.getId()){
			book.setMealId(sankeCommonMeal.getId());
			book.setMealName(sankeCommonMeal.getName());
			book.setMealPrice(sankeCommonMeal.getPrice());
			book.setMealGender(sankeCommonMeal.getGender());
			book.setSitePay(true);
		}else {
			book.setMealId(defCompanyCommMeal.getId());
			book.setMealName(defCompanyCommMeal.getName());
			book.setMealPrice(defCompanyCommMeal.getPrice());
			book.setMealGender(defCompanyCommMeal.getGender());
			book.setSitePay(sitePay);
		}
		book.setPayOnline(payOnline);
		book.setChangeDate(changeDate);
		book.setHidePrice(hidePrice);
		book.setSendMsg(sendMsg);
		book.setVerifyFlag(false); //不验证是否已经有订单
		book.setHospitalId(defhospital.getId());
		book.setHospitalName(defhospital.getName());
		book.setCompanyId(hospitalCompany.getId());
		book.setExamItemIdList(examItemList);
		book.setCompanyMoney(companyMoney);
		book.setReducedPrice(book.getMealPrice() - reduceOrderPrice);
		book.setFastBook(true);  //极速预约关键信息，统一作为已预约状态
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		int bookMealId = book.getMealId().intValue();
		List<Integer> removeGroupItemList = new ArrayList<>();//等价组内需要删除的单项列表
		List<String> groups = ResourceChecker.getMealGroupByMealId(bookMealId);
		List<Integer> itemList = ResourceChecker.getMealExamItemIdList(bookMealId);
		List<Integer> groupExamIds = new ArrayList<>();//等价组最后选择的单项列表
		if(isChangeItem)
			itemList.addAll(examItemList);//改项（套餐内单项+增加的单项列表)
		for(String k : groups){
			MealMultiChooseParam chooseParam = new MealMultiChooseParam();
			List<MealExamitemGroup> examitemLists =  ResourceChecker.getMealExamitemGroupByMealId(bookMealId,k);//查询组内的所有单项ID,随机取一个
			int index = new Random().nextInt(examitemLists.size());
			int examId = examitemLists.get(index).getItemId();
			int defaultSelectItem = ResourceChecker.getExamitemGroupDefaultSelectId(bookMealId,k);
			if(isChangeItem){
				chooseParam.setSelectExamItemId(examId);//组内的单项ID
				groupExamIds.add(examId);//等价组最后选择的单项列表
				if(defaultSelectItem != examId){
					removeGroupItemList.add(defaultSelectItem);
					itemList.add(examId);//单项列表加上多选一组内已选单项
				}
			}
			else{
				chooseParam.setSelectExamItemId(defaultSelectItem);
				groupExamIds.add(defaultSelectItem);//等价组最后选择的单项列表
			}
			chooseParam.setMultiChooseId(k);
			chooseParam.setMultiChooseName(examitemLists.get(index).getGroupName());//组名称
			mealMultiChooseParams.add(chooseParam);
		}
		itemList.removeAll(removeGroupItemList);
		book.setMutiChooseParams(mealMultiChooseParams);//等价组
		Map<String,Object> hosMaps = HospitalChecker.getHospitalSetting(defhospital.getId(),HospitalParam.OPEN_PRINT_EXAM_GUIDE,HospitalParam.OPEN_QUEUE);
		int open_print_exam_guide = Integer.parseInt(hosMaps.get("open_print_exam_guide").toString());
		int open_queue = Integer.parseInt(hosMaps.get("open_queue").toString());
		if(open_print_exam_guide == 1 )
			book.setOpenPrintExamGuide(true);
		else
			book.setOpenPrintExamGuide(false);
		if(open_queue == 1 )
			book.setOpenQueue(true);
		else 
			book.setOpenQueue(false);
		//Customer
		FileAccountImportInfo customer = new FileAccountImportInfo();
		customer.setAddAccountType(addAccountType);
		customer.setCompanyId(hospitalCompany.getId());
		customer.setEmployeeId(employeeId);
		customer.setGender(gender+"");
		customer.setGroup(group);
		customer.setIdCard(idCard);
		customer.setName(name);
		customer.setNewCompanyId(hospitalCompany.getId());
		customer.setManagerId(defaccountId);
		int organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		customer.setOrganizationId(defhospital.getId());
		customer.setOrganizationType(organizationType);
		if (mobile!=null&&mobile!="") {
			customer.setInitialMobile(mobile);
		}
		
		//customer.setEmpty(false);
		
		FastBookVo fast = new FastBookVo();
		fast.setBookDto(book);
		fast.setCustomer(customer);
		
		maps.put("customer",customer);
		maps.put("bookDto", fast);
		maps.put("idCard",idCard);
		if (hospitalCompany.getId() == defSKXCnewcompany.getId()){
			maps.put("mealPrice",sankeCommonMeal.getPrice());
			maps.put("mealName", sankeCommonMeal.getName());	
		}else {
			maps.put("mealPrice",defCompanyCommMeal.getPrice());
			maps.put("mealName", defCompanyCommMeal.getName());
		}
		maps.put("companyId", hospitalCompany.getId());
		maps.put("oldCompanyId",hospitalCompany.getId());
		maps.put("companyMoney", companyMoney);
		maps.put("onlinePay", payOnline);
		maps.put("itemList",itemList);
		maps.put("groupExamIds",groupExamIds);
		return maps;
		
	}
	
	public void checkAccount(Map<String,Object> mas,Integer healtherId,String dbInitialMobile){
		FileAccountImportInfo customer = (FileAccountImportInfo)mas.get("customer");
		//tb_account 账户表中若无会添加
		String sql1 = "select * from tb_account where id = ? and idcard = ?";
		List<Map<String, Object>> list1 = null;
		try {
			list1 = DBMapper.query(sql1,healtherId,customer.getIdCard());
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(list1.size(),1);
		
		//tb_examiner  用户关系表增加
		String sql2 = "select * from tb_examiner where customer_id = ? and manager_id = ? and new_company_id = ? ";
		List<Map<String, Object>> list2 = null;
		try {
			list2 = DBMapper.query(sql2,healtherId,defaccountId,customer.getNewCompanyId());
		} catch (SqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(list2.size(),1);
		MobileValidator mobileValidator = new MobileValidator();
		if (customer.getInitialMobile()!=null) {
			if (mobileValidator.valid(customer)) {
				//手机未超过30位，返回true
				if (MobileValidator.isMobile(customer.getInitialMobile())) {
					Assert.assertEquals(list2.get(0).get("mobile"), customer.getInitialMobile());
				}else{
					Assert.assertNull(list2.get(0).get("mobile"));
				}

				Assert.assertEquals(list2.get(0).get("initial_mobile"), customer.getInitialMobile());
			}
		}else{
			if(dbInitialMobile==null){					
				Assert.assertNull(list2.get(0).get("mobile"),list2.get(0).get("customer_id").toString());
				Assert.assertNull(list2.get(0).get("initial_mobile"));
			}
			else{
				if(dbInitialMobile!=""){					
					Assert.assertNotNull(list2.get(0).get("mobile"));
					Assert.assertNotNull(list2.get(0).get("initial_mobile"));
				}
			}
		}
	}

	
	@DataProvider
	public Iterator<String[]> sanke_fastbook_1(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/fastbook/fastbook_1.csv",22);
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
	public Iterator<String[]> sanke_fastbook_2(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/fastbook/fastbook_2.csv",22);
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
	public Iterator<String[]> company_fastbook_1(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/fastbook/fastbook_3.csv",22);
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
	public Iterator<String[]> company_fastbook_2(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/fastbook/fastbook_4.csv",22);
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
