package com.tijiantest.testcase.main.nologinbook;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.tijiantest.model.order.*;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealExamitemGroup;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.pattern.IntegerPatternConverter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 免登陆预约页面操作
 * 整个免登陆验证流程
 *  -> 1.校验是否登陆（接口ValidateLoginAddToken）
 *  -> 2.获取当前医院的经纬度（接口LoadSubmitOrderPageNoLogin）
 *  —> 3.免登陆下单（获取验证码）（接口NoLoginBook，接口MobileValidationCode）校验下单成功/身份证与套餐不一致/验证码错误情况
 *  -> 4.免登陆支付页面（接口NoLoginPayPageV2)
 *  -> 5.免登陆支付微信/支付宝/现场支付（接口NoLoginPay，接口NoLoginPayForOk，接口GoAliapyNoLogin，接口PayWithWxNoLogin)先使用微信/支付宝接口调用远程微信/支付宝端，再使用现场支付等支付验证
 *  -> 6.下重复订单(接口NoLoginBook）
 *  -> 7.撤销免登陆订单
 *  
 * 值得注意：这里免登陆预约之前会清除用户账户信息，免登陆预约提交后会验证用户注册信息
 * @author huifang
 *
 */
public class NoLoginBookTest extends MainBaseNoLogin{
	protected static int offmealId = 0;
	protected static List<Integer> newUserOrderList = new ArrayList<Integer>(); //新用户免登陆列表
	protected static List<Integer> noLoginBookList = new ArrayList<Integer>(); //免登陆订单列表
	protected static List<String> mobileList = new ArrayList<String>(); //账户手机号列表(主账户看手机号)
	protected static List<String> idCardList = new ArrayList<String>(); //账户身份证列表
	protected static List<String>passwordList = new ArrayList<String>(); //账户密码,与上列身份证对应
	protected static String mobile = null; //账户手机号码
	private String verifyCode = null;
	protected static int acceptOfflinePay = 0;
	protected static int accountPay = 0;
	protected static int aliPay = 0;
	protected static int weixinPay = 0;
	
	@Test(description = "免登陆预约一般套餐(无等价组)成功" , groups = {"qa","main_noLoginBookSuc"},dataProvider = "nologinbook_success")
	public void test_01_noLoginBook_success	(String ... args) throws SqlException{
		//STEP0：确保免登陆环境
		checkNoLoginEnv(hc1,defSite);
		//传参
		 String username  = args[1];
		 IdCardGeneric g = new IdCardGeneric();
		 String idcard = g.generateGender(1);
		 String mobile = args[3];
		 String examDate = args[4];
		 mobileList.add(mobile);
		 idCardList.add(idcard);
		 com.tijiantest.model.resource.meal.Meal offMeal = ResourceChecker.getOfficialMealListByMultiChooseOne(defHospitalId,MealGenderEnum.FEMALE.getCode(),false).get(0);
		 offmealId = offMeal.getId().intValue(); //获取第一个官方女性套餐
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();

		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site",defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p", "");
		 NameValuePair umobile = new BasicNameValuePair("mobile", mobile);
		 //step1:获取手机验证码
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);
		 params.add(umobile);
		 HttpResult result = hc1.post(Flag.MAIN,Account_MobileValidationCode,params);
		 Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		 Assert.assertTrue(result.getBody().equals("{}")||result.getBody().equals(""));
		 if(checkdb){
			 String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			 List<Map<String,Object>> smslist = DBMapper.query(sql);
			 String sms = smslist.get(0).get("content").toString();
			 verifyCode = sms.split("：")[1].split("，")[0];
			 log.info("verifyCode..."+verifyCode);
		 }

		 //step2:免登陆预约
		 //清理数据
		 if(checkdb){
			 DBMapper.update("delete from tb_user where username = \""+mobile+"\"");
		 }
		 int marriageInt = 0;
		 BookParams  bookParams = new BookParams();
		 bookParams.setName(username);
		 bookParams.setIdCard(idcard);
		 bookParams.setMobile(mobile);
		 bookParams.setValidationCode(verifyCode);
		 bookParams.setExamDate(examDate);
		 bookParams.setMealId(offmealId);
		 bookParams.setMarriageStatus(marriageInt);
		 bookParams.setItemIds(itemList);
		 bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		 bookParams.setNeedPaperReport(false);
		 bookParams.setInLocation(false);
		 bookParams.setScene(5);
		 String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		 waitto(1);
		 result = hc1.post(Flag.MAIN,NoLoginBook,params,JSON.toJSONString(bookParams));
		 log.info("result...."+result.getBody());
		 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		 int newNologinBookId = Integer.parseInt(JsonPath.read(result.getBody(), "$.orderId").toString());
		 newUserOrderList.add(newNologinBookId);
		 noLoginBookList.add(newNologinBookId);
		 if(checkdb){
			 //tb_user
			 String sql = "select * from tb_user where username = \'"+idcard +"\'";
			 List<Map<String,Object>> list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 int examinerAccountId = Integer.parseInt(list.get(0).get("account_id").toString());//体检人accountId

			 sql = "select * from tb_user where username = \'"+mobile +"\'";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 int selfAccountId = Integer.parseInt(list.get(0).get("account_id").toString());
			 Assert.assertNotEquals(examinerAccountId,selfAccountId);
			 //tb_account
			 sql = "select * from tb_account where id = "+examinerAccountId; //体检人账户
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 Map<String,Object> map = list.get(0);
			 Assert.assertEquals(map.get("name").toString(),username);
			 Assert.assertEquals(map.get("idcard").toString(),idcard);
			 Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
			 Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);

			 sql = "select * from tb_account where id = "+selfAccountId; //本人账户
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(map.get("mobile").toString(),mobile);
			 Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
			 Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);

			 //tb_account_role
			 sql = "select * from tb_account_role where account_id = "+examinerAccountId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 //tb_accounting
			 sql = "select * from tb_accounting where account_id = "+examinerAccountId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 int trade_account_id = Integer.parseInt(list.get(0).get("trade_account_id").toString());
			 map = list.get(0);
			 Assert.assertEquals(Integer.parseInt(map.get("balance").toString()),0);
			 //tb_examiner
			 sql = "select * from tb_examiner where customer_id = "+examinerAccountId + " order by update_time desc ";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(map.get("mobile").toString(),mobile);
			 Assert.assertEquals(Integer.parseInt(map.get("is_self").toString()),0);
			 Assert.assertEquals(Integer.parseInt(map.get("relation_id").toString()),selfAccountId);

			 //tb_nologin_account_info
			 sql = "select * from tb_nologin_account_info where account_id = "+selfAccountId+"  and gmt_created >= '"+beforeDate+"'";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);			 
			 map = list.get(0);
			 Assert.assertEquals(map.get("name").toString(),username);
			 Assert.assertEquals(map.get("mobile").toString(),mobile);
			 Assert.assertEquals(Integer.parseInt(map.get("marriagestatus").toString()),marriageInt);

			 //tb_order
			 sql = "select * from tb_order where account_id = "+examinerAccountId + " and status = ? order by id";
			 list = DBMapper.query(sql,OrderStatus.NOT_PAY.intValue());
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(Integer.parseInt(map.get("id").toString()),newNologinBookId);
			 Assert.assertEquals(Integer.parseInt(map.get("source").toString()),5); //免登陆订单
			 //itemList(订单单项id相同)
			 int orderid = Integer.parseInt(map.get("id").toString());
			 Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderid)));
			 //adjustPrice
			 MealSnapshot mealSnapshot = OrderChecker.getOrderInfo(orderid).getOrderMealSnapshot().getMealSnapshot();
			 Assert.assertEquals(mealSnapshot.getAdjustPrice().intValue(),OrderChecker.calculateClientOrderAdjustPrice(offmealId,null));
			 //tb_trade_account
			 sql = "select * from tb_trade_account where ref_id = "+examinerAccountId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 Assert.assertEquals(trade_account_id,Integer.parseInt(list.get(0).get("id").toString()));
			 
			 //免登陆获取短信信息
			 sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			 List<Map<String,Object>> smslist = DBMapper.query(sql);
			 String sms = smslist.get(0).get("content").toString();
			 //短信包括身份证号码，格式：张三先生（女士）您好，欢迎您使用体检预约平台服务，系统已经为您生成账号：14444444444，初始密码：837504，请尽快关注微信公众号并修改密码（预约成功后会出现公众号二维码，直接识别可关注，已关注则忽略此提示）
			 Assert.assertFalse(sms.contains(idcard));  
			 String password = sms.split("初始密码：")[1].split("，")[0];
			 passwordList.add(password);
			 
			 if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> mlist = MongoDBUtils.query("{'id':"+newNologinBookId+"}", MONGO_COLLECTION);
					Assert.assertNotNull(mlist);
					Assert.assertEquals(1, mlist.size());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.NOT_PAY.intValue());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("source").toString()),5); //免登陆订单
				}
			 Order order = OrderChecker.getOrderInfo(newNologinBookId);
			 //验证结算
			 OrderChecker.check_Book_ExamOrderSettlement(order);
			 
		 }
	}



	@Test(description = "免登陆预约一般套餐(有等价组)成功" , groups = {"qa","main_noLoginBookSuc"},dataProvider = "nologinbook_success")
	public void test_02_noLoginBook_success_multiChoose(String ... args) throws SqlException, ParseException {
		//STEP0：确保免登陆环境
		checkNoLoginEnv(hc1,defSite);
		//传参
		String username  = args[1];
		IdCardGeneric g = new IdCardGeneric();
		String idcard = g.generateGender(1);
		String mobile = args[3];
		String examDate = args[4];
		mobileList.add(mobile);
		idCardList.add(idcard);
		com.tijiantest.model.resource.meal.Meal offMeal = ResourceChecker.getOfficialMealListByMultiChooseOne(defHospitalId,MealGenderEnum.FEMALE.getCode(),true).get(0);
		offmealId = offMeal.getId().intValue(); //获取第一个官方女性套餐
		List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
		int hexamTimeIntervalId = defhospitalPeriod.getId();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair _site = new BasicNameValuePair("_site",defSite);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		NameValuePair _p = new BasicNameValuePair("_p", "");
		NameValuePair umobile = new BasicNameValuePair("mobile", mobile);
		//step1:获取手机验证码
		params.add(_site);
		params.add(_siteType);
		params.add(_p);
		params.add(umobile);
		HttpResult result = hc1.post(Flag.MAIN,Account_MobileValidationCode,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(result.getBody().equals("{}")||result.getBody().equals(""));
		if(checkdb){
			String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			List<Map<String,Object>> smslist = DBMapper.query(sql);
			String sms = smslist.get(0).get("content").toString();
			verifyCode = sms.split("：")[1].split("，")[0];
			log.info("verifyCode..."+verifyCode);
		}

		//step2:免登陆预约
		//清理数据
		if(checkdb){
			DBMapper.update("delete from tb_user where username = \""+mobile+"\"");
		}
		int marriageInt = 0;
		BookParams  bookParams = new BookParams();
		bookParams.setName(username);
		bookParams.setIdCard(idcard);
		bookParams.setMobile(mobile);
		bookParams.setValidationCode(verifyCode);
		bookParams.setExamDate(examDate);
		bookParams.setMealId(offmealId);
		bookParams.setMarriageStatus(marriageInt);
		bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		bookParams.setNeedPaperReport(false);
		bookParams.setInLocation(false);
		List<MealMultiChooseParam> mealMultiChooseParams = new ArrayList<>();
		List<Integer> removeGroupItemList = new ArrayList<>();//等价组内需要删除的单项列表
		List<String> groups  = ResourceChecker.getMealGroupByMealId(offmealId);//官方套餐的等价组列表
		List<Integer> groupExamIds = new ArrayList<>();//等价组最后选择的单项列表
		for(String k : groups){
			MealMultiChooseParam chooseParam = new MealMultiChooseParam();
			chooseParam.setMultiChooseId(k);
			List<MealExamitemGroup> examitemLists =  ResourceChecker.getMealExamitemGroupByMealId(offmealId,k);//查询组内的所有单项ID,随机取一个
			int index = new Random().nextInt(examitemLists.size());
			int examId = examitemLists.get(index).getItemId();
			chooseParam.setSelectExamItemId(examId);//组内的单项ID
			chooseParam.setMultiChooseName(examitemLists.get(index).getGroupName());//组名称
			mealMultiChooseParams.add(chooseParam);
			int defaultSelectItem = ResourceChecker.getExamitemGroupDefaultSelectId(offmealId,k);
			if(defaultSelectItem != examId){
				removeGroupItemList.add(defaultSelectItem);
				itemList.add(examId);//单项列表加上多选一组内已选单项
			}
			groupExamIds.add(examId);//等价组最后选择的单项列表
		}
		itemList.removeAll(removeGroupItemList);
		bookParams.setMealMultiChooseParams(mealMultiChooseParams);
		bookParams.setItemIds(itemList);
		bookParams.setScene(5);
		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		result = hc1.post(Flag.MAIN,NoLoginBook, params,JSON.toJSONString(bookParams));
		log.info("result...."+result.getBody());
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		int newNologinBookId = Integer.parseInt(JsonPath.read(result.getBody(), "$.orderId").toString());
		newUserOrderList.add(newNologinBookId);
		noLoginBookList.add(newNologinBookId);
		int selfAccountId = 0;
		if(checkdb){
			//tb_user
			String sql = "select * from tb_user where username = \'"+idcard +"\'";
			List<Map<String,Object>> list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			int examinerAccountId = Integer.parseInt(list.get(0).get("account_id").toString());//体检人accountId

			sql = "select * from tb_user where username = \'"+mobile +"\'";
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			selfAccountId = Integer.parseInt(list.get(0).get("account_id").toString());
			Assert.assertNotEquals(examinerAccountId,selfAccountId);
			//tb_account
			sql = "select * from tb_account where id = "+examinerAccountId; //体检人账户
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			Map<String,Object> map = list.get(0);
			Assert.assertEquals(map.get("name").toString(),username);
			Assert.assertEquals(map.get("idcard").toString(),idcard);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
			Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);

			sql = "select * from tb_account where id = "+selfAccountId; //本人账户
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			map = list.get(0);
			Assert.assertEquals(map.get("mobile").toString(),mobile);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
			Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);

			//tb_account_role
			sql = "select * from tb_account_role where account_id = "+examinerAccountId;
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			//tb_accounting
			sql = "select * from tb_accounting where account_id = "+examinerAccountId;
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			int trade_account_id = Integer.parseInt(list.get(0).get("trade_account_id").toString());
			map = list.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("balance").toString()),0);
			//tb_examiner
			sql = "select * from tb_examiner where customer_id = "+examinerAccountId + " order by update_time desc ";
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			map = list.get(0);
			Assert.assertEquals(map.get("mobile").toString(),mobile);
			Assert.assertEquals(Integer.parseInt(map.get("is_self").toString()),0);
			Assert.assertEquals(Integer.parseInt(map.get("relation_id").toString()),selfAccountId);

			//tb_nologin_account_info
			sql = "select * from tb_nologin_account_info where account_id = "+selfAccountId+"  and gmt_created >= '"+beforeDate+"'";
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			map = list.get(0);
			Assert.assertEquals(map.get("name").toString(),username);
			Assert.assertEquals(map.get("mobile").toString(),mobile);
			Assert.assertEquals(Integer.parseInt(map.get("marriagestatus").toString()),marriageInt);

			//tb_order
			sql = "select * from tb_order where account_id = "+examinerAccountId + " and status = ? order by id";
			list = DBMapper.query(sql,OrderStatus.NOT_PAY.intValue());
			Assert.assertEquals(list.size(),1);
			map = list.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("id").toString()),newNologinBookId);
			Assert.assertEquals(Integer.parseInt(map.get("source").toString()),5); //免登陆订单
			//itemList(订单单项id相同)
			int orderid = Integer.parseInt(map.get("id").toString());
			Assert.assertTrue(ListUtil.equalList(itemList,OrderChecker.getOrderItemList(orderid)));
			//adjustPrice
			MealSnapshot mealSnapshot = OrderChecker.getOrderInfo(orderid).getOrderMealSnapshot().getMealSnapshot();
			Assert.assertEquals(mealSnapshot.getAdjustPrice().intValue(),OrderChecker.calculateClientOrderAdjustPrice(offmealId,groupExamIds));
			//tb_trade_account
			sql = "select * from tb_trade_account where ref_id = "+examinerAccountId;
			list = DBMapper.query(sql);
			Assert.assertEquals(list.size(),1);
			Assert.assertEquals(trade_account_id,Integer.parseInt(list.get(0).get("id").toString()));

			//免登陆获取短信信息
			sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			List<Map<String,Object>> smslist = DBMapper.query(sql);
			String sms = smslist.get(0).get("content").toString();
			//短信包括身份证号码，格式：张三先生（女士）您好，欢迎您使用体检预约平台服务，系统已经为您生成账号：14444444444，初始密码：837504，请尽快关注微信公众号并修改密码（预约成功后会出现公众号二维码，直接识别可关注，已关注则忽略此提示）
			Assert.assertFalse(sms.contains(idcard));
			String password = sms.split("初始密码：")[1].split("，")[0];
			passwordList.add(password);

			if(checkmongo){
				waitto(mongoWaitTime);
				List<Map<String,Object>> mlist = MongoDBUtils.query("{'id':"+newNologinBookId+"}", MONGO_COLLECTION);
				Assert.assertNotNull(mlist);
				Assert.assertEquals(1, mlist.size());
				Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.NOT_PAY.intValue());
				Assert.assertEquals(Integer.parseInt(mlist.get(0).get("source").toString()),5); //免登陆订单
			}
			Order order = OrderChecker.getOrderInfo(newNologinBookId);
			//验证结算
			OrderChecker.check_Book_ExamOrderSettlement(order);
			//校验C端订单详情
			OrderChecker.main_checkOrderDetails(hc1,newNologinBookId,defSite,selfAccountId);

		}
	}

	@Test(description = "验证码错误，接口返回错误信息" , groups = {"qa"},dataProvider = "nologinbook_success",dependsOnMethods = "test_01_noLoginBook_success")
	public void test_03_noLoginBook_vericode_fail (String ...args) throws SqlException{
		 String idcard = idCardList.get(0);
		 int code =  Integer.parseInt(verifyCode)+10;  //错误验证码
		 String username  = args[1];
		 String mobile = args[3];
		 String examDate = args[4];
		 int mealId = ResourceChecker.getOfficialMealList(defHospitalId,MealGenderEnum.FEMALE.getCode()).get(0).getId().intValue(); //获取第一个官方女性套餐
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(mealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site", defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p", "");
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);

		 BookParams  bookParams = new BookParams();
		 bookParams.setName(username);
		 bookParams.setIdCard(idcard);
		 bookParams.setMobile(mobile);
		 bookParams.setValidationCode(code+"");
		 bookParams.setExamDate(examDate);
		 bookParams.setMealId(mealId);
		 bookParams.setMarriageStatus(0);
		 bookParams.setItemIds(itemList);
		 bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		 bookParams.setNeedPaperReport(false);
		 bookParams.setInLocation(false);
		 bookParams.setScene(5);

		 HttpResult result = hc1.post(Flag.MAIN,NoLoginBook,params, JSON.toJSONString(bookParams));
		 log.info("result...."+result.getBody());
		 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		 Assert.assertTrue(result.getBody().contains("验证码不正确"));
	}
	

	@Test(description = "套餐与身份证性别不符合,接口返回错误信息" , groups = {"qa"},dataProvider = "nologinbook_success",dependsOnMethods = "test_01_noLoginBook_success")
	public void test_04_noLoginBook_idcard_fail (String ...args) throws SqlException{
		 String idcard = idCardList.get(0);
		 String username  = args[1];
		 String mobile = args[3];
		 String examDate  = sdf.format(DateUtils.offDate(10));
		 int mealId = ResourceChecker.getOfficialMealList(defHospitalId,MealGenderEnum.MALE.getCode()).get(0).getId().intValue(); //获取第一个官方男性套餐
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(mealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site", defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p", "");
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);

		int marriageInt = 0;
		BookParams  bookParams = new BookParams();
		bookParams.setName(username);
		bookParams.setIdCard(idcard);
		bookParams.setMobile(mobile);
		bookParams.setValidationCode(verifyCode);
		bookParams.setExamDate(examDate);
		bookParams.setMealId(mealId);
		bookParams.setMarriageStatus(0);
		bookParams.setItemIds(itemList);
		bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		bookParams.setNeedPaperReport(false);
		bookParams.setInLocation(false);
		bookParams.setScene(5);
			
		 HttpResult result = hc1.post(Flag.MAIN,NoLoginBook, params,JSON.toJSONString(bookParams));
		 log.info("性别不符合result...."+result.getBody());
		 Assert.assertTrue(result.getBody().contains("餐与性别不符，请重新选择套餐"));
	}
	
	

	
	/**
	 * 上一个订单支付完成后再下一个单，提示重复
	 * @throws SqlException 
	 */
	@Test(description = "重复日期下单报错" , groups = {"qa"},dataProvider = "nologinbook_success",dependsOnMethods = "test_01_noLoginBook_success")
	public void test_05_noLoginBook_dateDuplidate_fail (String ...args) throws SqlException{
		 boolean needRebackSettings = false;
		 int same_day_order_maximum = 0;
		 if(checkdb){
			 //如果订单状态不为2或者11或者1，改为2
			 String sql = "select * from tb_order where id = ?";
			 List<Map<String,Object>> list = DBMapper.query(sql, newUserOrderList.get(0));
			 int status = Integer.parseInt(list.get(0).get("status").toString());
			 log.info("现在订单状态...."+status);
			 if(status !=2 && status !=11 && status != 1){
				 DBMapper.update("update tb_order set status = 2 where id = "+newUserOrderList.get(0));
				 MongoDBUtils.updateMongo("{'id':"+newUserOrderList.get(0)+"}","{$set:{'status':2}}", MONGO_COLLECTION);
			 }
			 Map<String,Object> hospitalMap = HospitalChecker.getHospitalSetting(defHospitalId, HospitalParam.SAME_DAY_ORDER_MAXIMUM);
			 same_day_order_maximum = Integer.parseInt(hospitalMap.get(HospitalParam.SAME_DAY_ORDER_MAXIMUM).toString());
			 if(same_day_order_maximum > 1){//大于1次，手动修改为1次
				 needRebackSettings = true;
				 log.info("开始重置了医院的同1天预约次数上限为1!!");
				 DBMapper.update("update tb_hospital_settings set same_day_order_maximum = 1 where hospital_id = "+defHospitalId);
				 log.info("结束重置了医院的同1天预约次数上限为1!!");
			 }
		 }
		 String idcard = idCardList.get(0);
		 String username  = args[1];
		 String mobile = args[3];
		 String examDate = args[4];
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site", defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p", "");
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);

		int marriageInt = 0;
		BookParams  bookParams = new BookParams();
		bookParams.setName(username);
		bookParams.setIdCard(idcard);
		bookParams.setMobile(mobile);
		bookParams.setValidationCode(verifyCode);
		bookParams.setExamDate(examDate);
		bookParams.setMealId(offmealId);
		bookParams.setMarriageStatus(0);
		bookParams.setItemIds(itemList);
		bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		bookParams.setNeedPaperReport(false);
		bookParams.setInLocation(false);
		bookParams.setScene(5);
			
		 HttpResult result = hc1.post(Flag.MAIN,NoLoginBook, params,JSON.toJSONString(bookParams));
		 if(needRebackSettings){
			 DBMapper.update("update tb_hospital_settings set same_day_order_maximum = "+same_day_order_maximum+" where hospital_id = "+defHospitalId);
		 }
		 log.info("重复日期result...."+result.getBody());
		 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		 Assert.assertTrue(result.getBody().contains("下单次数已经超过单日上限，无法下单"));
		
	}
	
	
	
	@Test(description = "已存在的账号免登陆预约" , groups = {"qa"},dataProvider = "nologinbook_success_again",dependsOnMethods = "test_05_noLoginBook_dateDuplidate_fail")
	public void test_06_noLoginBook_success_again(String ... args) throws SqlException{
		 //传参
		 String username  = args[1];
		 String examDate = args[4];
		 String againVerifyCode = null;
		 offmealId = ResourceChecker.getOfficialMealList(defHospitalId,MealGenderEnum.FEMALE.getCode()).get(0).getId().intValue(); //获取第一个官方女性套餐
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();
		 String idcard = idCardList.get(0);
		 String mobile = mobileList.get(0);
		 
		 //step1 :获取验证码
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site",defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p","");
		 NameValuePair pmonbile = new BasicNameValuePair("mobile",mobile);
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);
		 params.add(pmonbile);
		 HttpResult result = hc1.post(Flag.MAIN,Account_MobileValidationCode,params);
		 Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(result.getBody().equals("{}")||result.getBody().equals(""));
		 if(checkdb){
			 String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			 List<Map<String,Object>> smslist = DBMapper.query(sql);
			 String sms = smslist.get(0).get("content").toString();
			 againVerifyCode = sms.split("：")[1].split("，")[0];
			 log.info("再一次againVerifyCode..."+againVerifyCode);
			 
		 }
		int marriageInt = 0;
		BookParams  bookParams = new BookParams();
		bookParams.setName(username);
		bookParams.setIdCard(idcard);
		bookParams.setMobile(mobile);
		bookParams.setValidationCode(againVerifyCode);
		bookParams.setExamDate(examDate);
		bookParams.setMealId(offmealId);
		bookParams.setMarriageStatus(marriageInt);
		bookParams.setItemIds(itemList);
		bookParams.setExamTimeIntervalId(hexamTimeIntervalId);
		bookParams.setNeedPaperReport(false);
		bookParams.setInLocation(false);
		bookParams.setScene(5);

		String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);

		 result = hc1.post(Flag.MAIN,NoLoginBook, params,JSON.toJSONString(bookParams));
		 log.info("test_05_noLoginBook_success_again result...."+result.getBody());
		 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		 int newOrderId = Integer.parseInt(JsonPath.read(result.getBody(), "$.orderId").toString());
		 noLoginBookList.add(newOrderId);
		 if(checkdb){
			 //tb_user
			 String sql = "select * from tb_user where username = \'"+idcard +"\'";
			 List<Map<String,Object>> list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 int examinerCustomerId = Integer.parseInt(list.get(0).get("account_id").toString());
			 sql = "select * from tb_user where username = \'"+mobile+"\'";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 int selfAccountId = Integer.parseInt(list.get(0).get("account_id").toString());
			 //tb_account
			 sql = "select * from tb_account where id = "+examinerCustomerId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 Map<String,Object> map = list.get(0);
			 Assert.assertNotEquals(map.get("name").toString(),username);
			 Assert.assertEquals(map.get("idcard").toString(),idcard);
			 Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
			 Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);
			 //tb_account_role
			 sql = "select * from tb_account_role where account_id = "+examinerCustomerId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 //tb_accounting
			 sql = "select * from tb_accounting where account_id = "+examinerCustomerId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(Integer.parseInt(map.get("balance").toString()),0);
			 //tb_examiner
			 sql = "select * from tb_examiner where customer_id = "+examinerCustomerId + " and update_time > '"+beforeDate+"' order by update_time desc ";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(map.get("mobile").toString(),mobile);
			 Assert.assertEquals(Integer.parseInt(map.get("is_self").toString()),0);
			 Assert.assertEquals(Integer.parseInt(map.get("relation_id").toString()),selfAccountId);
			 //tb_nologin_account_info
			 sql = "select * from tb_nologin_account_info where account_id = "+selfAccountId +" order by id desc limit 1";
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(map.get("name").toString(),username);
			 Assert.assertEquals(map.get("mobile").toString(),mobile);
			 Assert.assertEquals(Integer.parseInt(map.get("marriagestatus").toString()),marriageInt);

			 //tb_order
			 sql = "select * from tb_order where account_id = "+examinerCustomerId + " and status = ? order by id";
			 list = DBMapper.query(sql,OrderStatus.NOT_PAY.intValue());
			 Assert.assertEquals(list.size(),1);
			 map = list.get(0);
			 Assert.assertEquals(Integer.parseInt(map.get("id").toString()),newOrderId);
			 Assert.assertEquals(Integer.parseInt(map.get("source").toString()),5); //免登陆订单
			 
			 if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> mlist = MongoDBUtils.query("{'id':"+newOrderId+"}", MONGO_COLLECTION);
					Assert.assertNotNull(mlist);
					Assert.assertEquals(1, mlist.size());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.NOT_PAY.intValue());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("source").toString()),5); //免登陆订单
				}
			 
			 Order order = OrderChecker.getOrderInfo(newOrderId);
			 //验证结算
			 OrderChecker.check_Book_ExamOrderSettlement(order);
		 }

	}
	

	
	
	@AfterClass(description = "撤销免登陆预约的订单",alwaysRun = true)
	public static void  test_revorkeOrder() throws SqlException{
		for(int i=0;i<idCardList.size();i++){
				onceLoginInSystem(hc1, Flag.MAIN, idCardList.get(i),"111111");
			    int id = noLoginBookList.get(i);
			    OrderChecker.Run_MainOrderRevokeOrder(hc1, id, false, true, true);
				HttpResult result = hc1.post(Flag.MAIN,MainDeleteOrder, id);
				if(result.getCode()==HttpStatus.SC_OK){
				//验证订单操作日志
				List<ExamOrderOperateLogDO>logs = OrderChecker.getOrderOperatrLog(OrderChecker.getOrderInfo(id).getOrderNum());
				Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.DELETE_ORDER.getCode());
				Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.DELETED.intValue());
				Assert.assertEquals(logs.get(0).getSystem().intValue(), OperateAppEnum.CLIENT.getCode());	
				System.out.println(id+"删除成功！");
				}
				else
					System.err.println(id+"删除失败！");
				onceLogOutSystem(hc1,Flag.MAIN);		
		}		
	}

	@DataProvider
	public Iterator<String[]> nologinbook_success() {
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/nologin/nologinbook_success.csv", 8);
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
	public Iterator<String[]> nologinbook_success_again() {
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/nologin/nologinbook_success_again.csv", 8);
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
