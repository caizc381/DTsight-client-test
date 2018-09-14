package com.tijiantest.testcase.crm.account.query;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.counter.DateUnit;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.order.MealMultiChooseParam;
import com.tijiantest.model.resource.meal.MealExamitemGroup;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.ListUtil;
import org.apache.http.HttpStatus;
import org.jsoup.helper.DataUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.order.ProxyBookDTO;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

import javax.annotation.Resource;

/**
 * @author huifang
 * 用户速查->卡代预约
 *
 */
public class ProxyFastbookTest extends CrmMediaBase{
    
	private int orderId = 0;
	private int mealPrice=0;
	private int companyMoney=0;
	private String nowday = sdf.format(new Date());
	private List<Integer> orderIdList = new ArrayList<Integer>();
	private static List<Integer>accountList = new ArrayList<Integer>();
	@Test(description = "代预约当天体检--不够使用线下付款",dataProvider="proxyFastbook" ,groups = {"qa","online"})
	public void test_01_proxyFastbook_success(String ... args) throws Exception{
		accountList.add(defCompanyAccountId);
		int mealId = defCompanyMaleMeal.getId();
		int mealGender = defCompanyMaleMeal.getGender();
		String mealName = defCompanyMaleMeal.getName();
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		int cardId = defCompanyCard.getId();
		boolean payOnline = false;
		ProxyBookDTO bdt = new ProxyBookDTO();
		bdt.setAccountIdList(accountList);
		bdt.setCardId(cardId);
		bdt.setMealId(mealId);
		bdt.setMealGender(mealGender);
		bdt.setMealName(mealName);
		bdt.setCompanyId(defnewcompany.getId());
		bdt.setCompanyMoney(defCompanyCard.getBalance().intValue());
		bdt.setHospitalId(defhospital.getId());
		bdt.setHospitalName(defhospital.getName());
		bdt.setExamDate(sdf.format(new Date()));
		bdt.setExamTimeIntervalId(defhospitalPeriod.getId());
		bdt.setExamTimeIntervalName(defhospitalPeriod.getName());
		bdt.setChangeDate(true);
		bdt.setMutiChooseParams(mealMultiChooseParams);

		
		String mealSql= "select * from tb_meal where id=?";
		List<Map<String, Object>> mealList =DBMapper.query(mealSql, mealId);
		/*
		* 构建多选一项目信息*/



		System.out.println("套餐名称："+mealList.get(0).get("name")+"   状态："+mealList.get(0).get("disable"));
	 
		HttpResult result = httpclient.post(AccountQuery_ProxyFastbook, JSON.toJSONString(bdt));
		System.out.println(result.getBody());
		//判断下单是否异常，如果异常，先处理
		if(result.getCode()==HttpStatus.SC_BAD_REQUEST){
			String exceptType = OrderChecker.orderExceptionAction(result,httpclient);
			if(exceptType!=null)
				result = httpclient.post(Order_BatchOrder, JSON.toJSONString(bdt));
		}
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"fail reson:"+result.getBody());
		Assert.assertNotNull(result.getBody());
		
		mealPrice=defCompanyMaleMeal.getPrice();
		companyMoney=new Long(defCompanyCard.getBalance()).intValue();
		if(mealPrice-companyMoney>0){
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), mealPrice-companyMoney);
		}
		else {
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), 0);
		}

		//check database
		if(checkdb){
			String sql = "select * from tb_order where account_id = ? and hospital_id = ? and status = 2 and exam_date like '"+nowday+"%'  and insert_time like '"+nowday+"%'";
			List<Map<String,Object>> list1 = DBMapper.query(sql,defCompanyAccountId,defhospital.getId());
			Assert.assertEquals(list1.size(),1);	
			Assert.assertEquals(Integer.parseInt(list1.get(0).get("order_price").toString()),mealPrice); //直接预约，订单价格=套餐价格
			orderId = Integer.parseInt(list1.get(0).get("id").toString());
			Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString()), orderId);
			
			for(Order order : OrderChecker.checkOrder(accountList)){
				orderIdList.add(order.getId());
				Assert.assertEquals(order.getStatus(), OrderStatus.ALREADY_BOOKED.intValue());
				//验证结算
				OrderChecker.check_Book_ExamOrderSettlement(order);
				//验证交易
				PayChecker.checkCrmProxyFastBookTodayTradeOrder(defhospital.getId(), order, defCompanyAccountId,payOnline,cardId);
				if(checkmongo){
					List<Map<String,Object>> list2 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
					Assert.assertNotNull(list2);
					Assert.assertEquals(1, list2.size());
				}
			}
			
		}
	}

	@Test(description = "代预约非当天体检--不够使用线下付款",dataProvider="proxyFastbook" ,groups = {"qa","online"})
	public void test_02_proxyFastbook_success(String ... args) throws Exception{
		accountList.clear();
		accountList.add(defCompanyAccountId);
		int mealId = defCompanyMaleMeal.getId();
		int mealGender = defCompanyMaleMeal.getGender();
		String mealName = defCompanyMaleMeal.getName();
		int cardId = defCompanyCard.getId();
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		boolean payOnline = false;
		ProxyBookDTO bdt = new ProxyBookDTO();
		bdt.setAccountIdList(accountList);
		bdt.setCardId(cardId);
		bdt.setMealId(mealId);
		bdt.setMealGender(mealGender);
		bdt.setMealName(mealName);
		bdt.setCompanyId(defnewcompany.getId());
		int companyNowMoney = CardChecker.getCardInfo(cardId).getBalance().intValue();
		bdt.setCompanyMoney(companyNowMoney);
		bdt.setHospitalId(defhospital.getId());
		bdt.setHospitalName(defhospital.getName());
		bdt.setMutiChooseParams(mealMultiChooseParams);
		//预约其他天
		Map<String,Object> maps = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.PREVIOUS_BOOK_DAYS,HospitalParam.NEED_LOCAL_PAY);
		int previous_book_days = (int)maps.get(HospitalParam.PREVIOUS_BOOK_DAYS);
		boolean needLocalPay = ((int)maps.get(HospitalParam.NEED_LOCAL_PAY) == 1)? true:false;
		Date newDate = DateUtils.offsetDestDay(new Date(),previous_book_days);
		String newDateStr = sdf.format(newDate);
		bdt.setExamDate(sdf.format(newDate));
		bdt.setExamTimeIntervalId(defhospitalPeriod.getId());
		bdt.setExamTimeIntervalName(defhospitalPeriod.getName());
		bdt.setSitePay(false);//非当天付款
		bdt.setVerifyFlag(true);
		bdt.setChangeDate(true);

		String mealSql= "select * from tb_meal where id=?";
		List<Map<String, Object>> mealList =DBMapper.query(mealSql, mealId);
		System.out.println("套餐名称："+mealList.get(0).get("name")+"   状态："+mealList.get(0).get("disable"));

		HttpResult result = httpclient.post(AccountQuery_ProxyFastbook, JSON.toJSONString(bdt));
		System.out.println(result.getBody());
		//判断下单是否异常，如果异常，先处理
		if(result.getCode()==HttpStatus.SC_BAD_REQUEST){
			String exceptType = OrderChecker.orderExceptionAction(result,httpclient);
			if(exceptType!=null)
				result = httpclient.post(Order_BatchOrder, JSON.toJSONString(bdt));
		}
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"fail reson:"+result.getBody());
		Assert.assertNotNull(result.getBody());

		//check database
		if(checkdb){
			mealPrice=defCompanyMaleMeal.getPrice();
			int newOrderId = Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString());
			orderIdList.add(newOrderId);
			Order order = OrderChecker.getOrderInfo(newOrderId);
			Assert.assertEquals(order.getHospitalCompany().getId(),defCompanyCard.getNewCompanyId());
			if(mealPrice-companyNowMoney>0){
				Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), mealPrice-companyNowMoney);
				if(needLocalPay){
					Assert.assertEquals(order.getStatus(),OrderStatus.SITE_PAY.intValue());
					if(checkmongo){
						List<Map<String,Object>> list2 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
						Assert.assertNotNull(list2);
						Assert.assertEquals(1, list2.size());
						Assert.assertEquals(Integer.parseInt(list2.get(0).get("status").toString()),OrderStatus.SITE_PAY.intValue());
					}
				}
				else{
					Assert.assertEquals(order.getStatus(),OrderStatus.ALREADY_BOOKED.intValue());
					if(checkmongo){
						List<Map<String,Object>> list2 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
						Assert.assertNotNull(list2);
						Assert.assertEquals(1, list2.size());
						Assert.assertEquals(Integer.parseInt(list2.get(0).get("status").toString()),OrderStatus.ALREADY_BOOKED.intValue());
					}
				}
			}
			else {
				Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), 0);
			}

			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			//验证交易
			PayChecker.checkCrmProxyFastBookTradeOrder(defhospital.getId(), order, defCompanyAccountId,payOnline,cardId,needLocalPay);
		}
	}


	@Test(description = "卡代預約非当天改项预约",groups = "qa",dataProvider = "proxyfastbook_1")
	public void test_03_proxyfastbook_success(String... args) throws Exception {
		System.out.println("----卡代预约非当天改项预约-----");
		//传参数
		accountList.clear();
		accountList.add(defCompanyAccountId);
		int mealId = defCompanyMaleMeal.getId();
		int mealGender = defCompanyMaleMeal.getGender();
		String mealName = defCompanyMaleMeal.getName();
		int cardId = defCompanyCard.getId();
		String examItemStrs = args[6];
		boolean payOnline = false;
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		List<String> groups = ResourceChecker.getMealGroupByMealId(mealId);
		List<Integer> itemlist = ResourceChecker.getMealExamItemIdList(mealId);
		String[] addExamItemArray = examItemStrs.split("#");
		List<Integer> addExamItemList = ListUtil.StringArraysToIntegerList(addExamItemArray);
		itemlist.addAll(addExamItemList);//增加的单项
		List<Integer> groupExamIds = new ArrayList<>();

		ProxyBookDTO bdt = new ProxyBookDTO();
		bdt.setAccountIdList(accountList);
		bdt.setCardId(cardId);
		bdt.setMealId(mealId);
		bdt.setMealGender(mealGender);
		bdt.setMealName(mealName);
		bdt.setCompanyId(defnewcompany.getId());
		int companyNowMoney = CardChecker.getCardInfo(cardId).getBalance().intValue();
		bdt.setCompanyMoney(companyNowMoney);
		bdt.setHospitalId(defhospital.getId());
		bdt.setHospitalName(defhospital.getName());

		//预约其他天
		Map<String,Object> maps = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.PREVIOUS_BOOK_DAYS,HospitalParam.NEED_LOCAL_PAY);
		int previous_book_days = (int)maps.get(HospitalParam.PREVIOUS_BOOK_DAYS);
		boolean needLocalPay = ((int)maps.get(HospitalParam.NEED_LOCAL_PAY) == 1)? true:false;
		Date newDate = DateUtils.offsetDestDay(new Date(),previous_book_days);
		String newDateStr = sdf.format(newDate);
		bdt.setExamDate(sdf.format(newDate));
		bdt.setExamTimeIntervalId(defhospitalPeriod.getId());
		bdt.setExamTimeIntervalName(defhospitalPeriod.getName());
		bdt.setSitePay(false);//非当天付款
		bdt.setVerifyFlag(true);
		bdt.setChangeDate(true);
		List<Integer> removeGroupItemList = new ArrayList<>();//等价组内需要删除的单项列表
		for(String K:groups){
			MealMultiChooseParam chooseParam = new MealMultiChooseParam();
			chooseParam.setMultiChooseId(K);
			List<MealExamitemGroup> examitemlists = ResourceChecker.getMealExamitemGroupByMealId(mealId);
			int index = new Random().nextInt(examitemlists.size());
			int examId = examitemlists.get(index).getItemId();
			chooseParam.setSelectExamItemId(examId);
			chooseParam.setMultiChooseName(examitemlists.get(index).getGroupName());
			mealMultiChooseParams.add(chooseParam);
			int defaultSelectItem = ResourceChecker.getExamitemGroupDefaultSelectId(mealId,K);
			if(defaultSelectItem != examId){
				removeGroupItemList.add(defaultSelectItem);
				itemlist.add(examId);
			}
			groupExamIds.add(examId);
		}
		itemlist.removeAll(removeGroupItemList);
		bdt.setMutiChooseParams(mealMultiChooseParams);
		bdt.setExamItemIdList(itemlist);
		HttpResult result = httpclient.post(AccountQuery_ProxyFastbook, JSON.toJSONString(bdt));
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK,"fail reson:"+result.getBody());
		Assert.assertNotNull(result.getBody());


		if(checkdb){
			mealPrice=defCompanyMaleMeal.getPrice();
			int newOrderId = Integer.parseInt(JsonPath.read(result.getBody(),"$.orderId").toString());
			orderIdList.add(newOrderId);
			Order order = OrderChecker.getOrderInfo(newOrderId);
			Assert.assertEquals(order.getHospitalCompany().getId(),defCompanyCard.getNewCompanyId());
			double orderPrice = mealPrice;
			List<ExamItem> addExamList = ResourceChecker.getItemInfoByIds(addExamItemList);
			for(ExamItem items : addExamList)
				orderPrice += HospitalChecker.calculator_data(defhospital.getId(),(int)(defCompanyMaleMeal.getDiscount()* items.getPrice()));
			if(orderPrice-companyNowMoney>0){
				Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), (int)orderPrice-companyNowMoney);
				if(needLocalPay){
					Assert.assertEquals(order.getStatus(),OrderStatus.SITE_PAY.intValue());
					if(checkmongo){
						List<Map<String,Object>> list2 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
						Assert.assertNotNull(list2);
						Assert.assertEquals(1, list2.size());
						Assert.assertEquals(Integer.parseInt(list2.get(0).get("status").toString()),OrderStatus.SITE_PAY.intValue());
					}
				}
				else{
					Assert.assertEquals(order.getStatus(),OrderStatus.ALREADY_BOOKED.intValue());
					if(checkmongo){
						List<Map<String,Object>> list2 = MongoDBUtils.query("{'id':"+order.getId()+"}", MONGO_COLLECTION);
						Assert.assertNotNull(list2);
						Assert.assertEquals(1, list2.size());
						Assert.assertEquals(Integer.parseInt(list2.get(0).get("status").toString()),OrderStatus.ALREADY_BOOKED.intValue());
					}
				}
			}
			else {
				Assert.assertEquals(Integer.parseInt(JsonPath.read(result.getBody(),"$.fee").toString()), 0);
			}

			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			//验证交易
			PayChecker.checkCrmProxyFastBookTradeOrder(defhospital.getId(), order, defCompanyAccountId,payOnline,cardId,needLocalPay);
		}
		waitto(1);
	}





	@AfterClass(description="代撤销订单",groups = {"qa"})
	public void afterTest() throws SqlException{
		/*****撤销订单******/
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient,orderIdList,false,true,true);
	}
	@DataProvider
	public Iterator<String[]> proxyFastbook(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/proxyFastbook.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public Iterator<String[]> proxyfastbook_1(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/proxyFastbook_1.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

