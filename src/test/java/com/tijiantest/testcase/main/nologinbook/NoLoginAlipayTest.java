package com.tijiantest.testcase.main.nologinbook;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.order.*;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.paylog.TradeTypeEnum;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.payment.trade.PayConstants.NextActions;
import com.tijiantest.model.payment.trade.PayConstants.PayMethod;
import com.tijiantest.model.payment.trade.PayConstants.TradeType;
import com.tijiantest.model.payment.trade.PayResponse;
import com.tijiantest.model.payment.trade.TradeAccountDetail;
import com.tijiantest.model.payment.trade.TradeOrder;
import com.tijiantest.model.payment.trade.TradePayRecord;
import com.tijiantest.model.payment.trade.TradeSubAccountType;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
/**
 * 支付宝免登陆订单
 * @author huifang
 *
 */
public class NoLoginAlipayTest extends MainBaseNoLogin{	
	protected static int offmealId = 0;
	protected static String aliIdcard = null; //新用户免登陆列表
	protected static int alipayOrder = 0; //免登陆订单列表
	protected static String aliPassword = null; //账户密码,与上列身份证对应
	protected static String aliMobile = null; //账户手机号码
	protected static String aliOrderNum = null;//订单编号
	protected static List<TradeOrder> tradePayOrderList  = null;
	protected static List<TradePayRecord> tradePayRecList = null;
	@Test(description = "验证是否登陆",groups = {"qa","main_validateNologinAlipay"})
	public void test_01_validateLoginAddToken(){
        checkNoLoginEnv(hc3,defSite);
    }
	
	@Test(description = "新账号免密码登陆" , groups = {"qa"},dataProvider = "nologinbook_Alipaysuccess",dependsOnMethods = "test_01_validateLoginAddToken")
	public void test_02_noLoginBook_alipay(String ... args) throws SqlException{
		 //传参
		 String username  = args[1];
		 IdCardGeneric g = new IdCardGeneric();
		 String idcard = g.generateGender(1);
		 aliIdcard = idcard;
		 String mobile = args[3];
		 String examDate = args[4];
		 aliMobile = mobile;
		 offmealId = ResourceChecker.getOfficialMealList(defHospitalId,MealGenderEnum.FEMALE.getCode()).get(0).getId().intValue(); //获取第一个官方女性套餐
		 List<Integer> itemList = ResourceChecker.getMealExamItemIdList(offmealId);
		 int hexamTimeIntervalId = defhospitalPeriod.getId();
		 String verifyCode = null;
		 NameValuePair _site = new BasicNameValuePair("_site",defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair _p = new BasicNameValuePair("_p", "");

		 NameValuePair umobile = new BasicNameValuePair("mobile", mobile);
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 //step1:获取手机验证码
		 params.add(_site);
		 params.add(_siteType);
		 params.add(_p);
		 params.add(umobile);
		 HttpResult result = hc3.post(Flag.MAIN,Account_MobileValidationCode,params);
		 Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		 Assert.assertEquals(result.getBody(), "{}");
		 if(checkdb){
			 String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
			 List<Map<String,Object>> smslist = DBMapper.query(sql);
			 String sms = smslist.get(0).get("content").toString();
			 verifyCode = sms.split("：")[1].split("，")[0];
			 log.info("verifyCode..."+verifyCode);
		 }
		 NameValuePair uvalidationCode = new BasicNameValuePair("validationCode",verifyCode);

		 
		 
		 //step2:免登陆预约
		 //清理数据
		 if(checkdb){
			 DBMapper.update("delete from tb_user where username = \""+mobile+"\"");
			 DBMapper.update("update tb_account set mobile=null WHERE mobile= \""+mobile+"\"");
		 }

		int marriageInt = 0;
		BookParams bookParams = new BookParams();
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
		 
		 result = hc3.post(Flag.MAIN,NoLoginBook, params,JSON.toJSONString(bookParams));
		 log.info("result...."+result.getBody());
		 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		 Assert.assertTrue(result.getBody().contains("orderId"),"失败原因:body返回"+result.getBody());
		 alipayOrder = Integer.parseInt(JsonPath.read(result.getBody(), "$.orderId").toString());
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
			 sql = "select * from tb_account where id = "+examinerAccountId;
			 list = DBMapper.query(sql);
			 Assert.assertEquals(list.size(),1);
			 Map<String,Object> map = list.get(0);
			 Assert.assertEquals(map.get("name").toString(),username);
			 Assert.assertEquals(map.get("idcard").toString(),idcard);
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
			 sql = "select * from tb_nologin_account_info where account_id = "+selfAccountId +" order by id desc limit 1";
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
			 Assert.assertEquals(Integer.parseInt(map.get("id").toString()),alipayOrder);
			 Assert.assertEquals(Integer.parseInt(map.get("source").toString()),5); //免登陆订单
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
			 Assert.assertTrue(sms.contains(mobile));  
			 aliPassword = sms.split("初始密码：")[1].split("，")[0];
			 
			 if(checkmongo){
					waitto(mongoWaitTime);
					List<Map<String,Object>> mlist = MongoDBUtils.query("{'id':"+alipayOrder+"}", MONGO_COLLECTION);
					Assert.assertNotNull(mlist);
					Assert.assertEquals(1, mlist.size());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.NOT_PAY.intValue());
					Assert.assertEquals(Integer.parseInt(mlist.get(0).get("source").toString()),5); //免登陆订单
				}
		 }

	}
	

	@Test(description = "免登陆支付宝支付，调用统一的支付方法" ,groups= {"qa"}, dependsOnMethods = "test_02_noLoginBook_alipay")
	public void test_03_goAlipayNoLogin() throws Exception{
		DBMapper.update("update tb_hospital_settings set ali_pay = 1 where hospital_id = "+defHospitalId);
		Map<String,Object> payMethods = HospitalChecker.getHospitalSetting(defHospitalId,HospitalParam.ALI_PAY);
		int aliPay = Integer.parseInt(payMethods.get(HospitalParam.ALI_PAY).toString());
		 if(aliPay == 1){ 
			 JSONObject jo = new JSONObject();
			 jo.put("orderId", alipayOrder);
			 jo.put("_site", defSite);
			 jo.put("site",defSite);
			 jo.put("subSite", "/"+defSite);
			 jo.put("payType", PayMethod.Alipay);
			 jo.put("client", "wap");
			 jo.put("p", "");

			 waitto(6);
			 HttpResult result = hc3.post(Flag.MAIN,Main_OrderPay,jo.toJSONString());
			 Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
			 String body = result.getBody();
			 log.info("支付宝页面返回...."+body);
			 Assert.assertTrue(body.contains("ALIPAY_SUBMIT_FROM"),"错误原因：支付宝返回"+body);
			 PayResponse payRes = JSON.parseObject(result.getBody(), PayResponse.class);
			 Assert.assertFalse(payRes.isDone());
			 Assert.assertTrue(payRes.isNeedNextAction());
			 Assert.assertTrue(payRes.isSuccess());
			 Assert.assertEquals(payRes.getNextActionName(),NextActions.AlipaySubmitFrom);
			//为实际支付做准备
			if(checkdb){
					//校验订单
					String sql = "select * from tb_order where id = ? ";
					List<Map<String,Object>> list = DBMapper.query(sql, alipayOrder);
					Map<String,Object> orderMap = list.get(0);
					System.out.println("支付宝订单状态:"+list.get(0).get("id")+"状态..."+list.get(0).get("status"));
					int status = Integer.parseInt(orderMap.get("status").toString());
					int orderPrice = Integer.parseInt(orderMap.get("order_price").toString());
					aliOrderNum = orderMap.get("order_num").toString();
					Assert.assertEquals(status,OrderStatus.PAYING.intValue()); //支付中
					Assert.assertEquals(payRes.getRefOrderNum(),aliOrderNum);
					Assert.assertEquals(payRes.getRequireAmount()+"",orderPrice+"");
					//校验tb_paymentrecord
					String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "+alipayOrder + " and p.trade_type = "+TradeTypeEnum.OrderPayment.getCode()	+" and p.payment_method_id = m.id and m.type = "+PaymentTypeEnum.Alipay.getCode();
					List<Map<String,Object>> paymentList = DBMapper.query(paymentSql);
					Assert.assertEquals(paymentList.size(),1);
					Map<String,Object> paymentMap = paymentList.get(0);
					Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()),0); //支付中状态
					Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()),1); //支付类型
					//校验tb_paylog
					String paylogSql = "select * from tb_paylog where order_id = "+alipayOrder + " order by id desc limit 1";
					List<Map<String,Object>> paylogList = DBMapper.query(paylogSql);
					Map<String,Object> paylogMap = paylogList.get(0);
					Assert.assertEquals(Integer.parseInt(paylogMap.get("trade_body_type").toString()),PayConsts.TradeBodyTypes.Balance);
					Assert.assertEquals(Integer.parseInt(paylogMap.get("trade_body").toString()),OrderChecker.getOrderInfo(alipayOrder).getOperatorId().intValue());
					Assert.assertEquals(paylogMap.get("amount").toString(),""+-orderPrice);
					Assert.assertEquals(Integer.parseInt(paylogMap.get("status").toString()),PayConsts.TradeStatus.Created);
					//校验tb_trade_order【交易订单表】
					tradePayOrderList = PayChecker.getTradeOrderByOrderNum(aliOrderNum,TradeType.pay);
					Assert.assertEquals(tradePayOrderList.size(),1); //下单
					TradeOrder to = tradePayOrderList.get(0);
					Assert.assertEquals(payRes.getRefOrderNumVersion(),to.getRefOrderNumVersion());
					Assert.assertEquals(to.getRefOrderType().intValue(),1);
					Assert.assertEquals(payRes.getPayingAmount(),to.getAmount().longValue());
					Assert.assertEquals(payRes.getRequireAmount(),to.getAmount().longValue());
					Assert.assertEquals(payRes.getSuccessAmount(),to.getSuccAmount().longValue());
					Assert.assertEquals(to.getTradeStatus().intValue(),PayConstants.TradeStatus.Paying);
					Assert.assertEquals(to.getPayMethodType().intValue(),PayConstants.PayMethodBit.AlipayBit);
					Assert.assertEquals(to.getTradeType().intValue(),PayConstants.TradeType.pay);
					Assert.assertTrue(to.getExtraCommonParam().contains(defSite));
					//校验tb_trade_pay_record【交易支付表】
					tradePayRecList = PayChecker.getTradePayRecordByOrderNum(aliOrderNum, to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
					Assert.assertEquals(tradePayRecList.size(),1); 
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),PayChecker.getSuitablePayMethodId(defHospitalId, PayConstants.PayMethodBit.AlipayBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.Alipay);
					Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Paying);
					Assert.assertEquals(payRes.getRequireAmount(),tpr.getPayAmount().longValue());
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),PayChecker.getTradeAccountByOrderId(alipayOrder));
					Assert.assertNull(tpr.getPayTradeSubaccountId());
					Assert.assertNull(tpr.getPayTradeSubaccountType());
					int dbReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(defHospitalId, PayConstants.PayMethodBit.AlipayBit);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					
					//交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
					List<TradeAccountDetail> tradeAccountList = PayChecker.getTradeAccountDetail(to.getTradeOrderNum());
					Assert.assertEquals(tradeAccountList.size(),0); 
			}
		 }
		
	}
	
	
	
	@AfterClass(description = "撤销免登陆支付宝预约的订单",alwaysRun = true)
	public  void  test_revorkeOrder() throws SqlException{
			onceLoginInSystem(hc3, Flag.MAIN, aliMobile, aliPassword);
			int id =  alipayOrder;
			waitto(1);//等待1s再撤销订单
			//撤销订单
			OrderChecker.Run_MainOrderRevokeOrder(hc3, id, false, true, true);
		    //校验tb_paymentrecord 无数据插入
			String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "+alipayOrder + " and p.trade_type = "+TradeTypeEnum.unifyRefund.getCode() + " and  p.payment_method_id = m.id and m.type = "+PaymentTypeEnum.Alipay.getCode();
			List<Map<String,Object>> paymentList = DBMapper.query(paymentSql);
			Assert.assertEquals(paymentList.size(),0);
			//校验tb_paylog 不增加数据，支付宝支付未成功，撤销不插入新数据
			String paylogSql = "select * from tb_paylog where order_id = "+alipayOrder + " and trade_type = 1011 order by id ";
			List<Map<String,Object>> paylogList = DBMapper.query(paylogSql);
			Assert.assertEquals(paylogList.size(),0);
			//校验tb_trade_order【交易订单表】
			List<TradeOrder> tradeRefundList = PayChecker.getTradeOrderByOrderNum(aliOrderNum,TradeType.refund);
			Assert.assertEquals(tradeRefundList.size(),0); //退款无记录
			HttpResult result = hc3.post(Flag.MAIN,MainDeleteOrder, id);
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
			onceLogOutSystem(hc3, Flag.MAIN);
	}
	
	@DataProvider
	public Iterator<String[]> nologinbook_Alipaysuccess() {
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/nologin/nologinbook_alipay.csv", 8);
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
