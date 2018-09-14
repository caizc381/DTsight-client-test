package com.tijiantest.testcase.main.nologinbook;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.model.payment.trade.*;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.hospital.HospitalSettings;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 免登陆支付页面
 * @author huifang
 *
 */
public class NoLoginPayPageTest extends MainBaseNoLogin {

	protected static int acceptOfflinePay = 0;
	protected static int accountPay = 0;
	protected static int aliPay = 0;
	protected static int weixinPay = 0;
	protected static int orderId;

	
	@SuppressWarnings("deprecation")
	@Test(description = "免登陆订单支付页面",groups = {"qa","main_nologinPayPage"},dataProvider = "getNoLoginPage")
	public void test_01_getNoLoginPayPage(String ...args) throws SqlException{

		//STEP0：确保免登陆环境
		checkNoLoginEnv(hc2,defSite);
		//STEP1:免登陆下单
		orderId = OrderChecker.main_createNoLoginOrder(hc2,defHospitalId,defSite,args[1],args[3],args[4],defhospitalPeriod.getId());

		//STEP3:免登陆支付
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair oid = new BasicNameValuePair("orderId", orderId+"");
		NameValuePair _site = new BasicNameValuePair("_site", defSite);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(oid);
		params.add(_site);
		params.add(_siteType);
		
		HttpResult result = hc2.get(Flag.MAIN,NoLoginPayPageV2, params);
		String body = result.getBody();
		log.info(body);
		HospitalSettings hs = JSONObject.parseObject(JsonPath.read(body, "$.hospitalSetting").toString(), HospitalSettings.class);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK, "卡支付页面报错:" + body);
		int dbOrderId = Integer.parseInt(JsonPath.read(body, "$.orderId").toString());
		int dbOrderPrice = Integer.parseInt(JsonPath.read(body, "$.orderPrice").toString());
		int examProject = Integer.parseInt(JsonPath.read(body, "$.体检项目").toString());
		int balance = Integer.parseInt(JsonPath.read(body, "$.accounting.balance").toString());
		List<Integer> selectedCardIds = JsonPath.read(body, "$.selectedCardIds.[*]");
		String retHospitalName = JsonPath.read(body,"$.hospitalName").toString();
		int retHospitalId = Integer.parseInt(JsonPath.read(body, "$.hospitalId").toString());
		int retPaidMoney = Integer.parseInt(JsonPath.read(body, "$.paidMoney").toString());
		JSONObject retInvoice = JSONObject.parseObject(JsonPath.read(body, "$.invoice").toString());
		JSONObject retOrderAccount = JSONObject.parseObject(JsonPath.read(body, "$.orderAccount").toString());

		if (checkdb) {
			//查询医院的支付方式
			Map<String,Object> payMethods = HospitalChecker.getHospitalSetting(defHospitalId,HospitalParam.ACCEPT_OFFLINE_PAY,HospitalParam.ALI_PAY,HospitalParam.WEIXIN_PAY,HospitalParam.ACCOUNT_PAY);
			acceptOfflinePay = Integer.parseInt(payMethods.get(HospitalParam.ACCEPT_OFFLINE_PAY).toString());
			accountPay = Integer.parseInt(payMethods.get(HospitalParam.ACCOUNT_PAY).toString());
			aliPay = Integer.parseInt(payMethods.get(HospitalParam.ALI_PAY).toString());
			weixinPay = Integer.parseInt(payMethods.get(HospitalParam.WEIXIN_PAY).toString());
			Assert.assertEquals(hs.getAcceptOfflinePay().booleanValue(),acceptOfflinePay==1?true:false);
			Assert.assertEquals(hs.getAccountPay().booleanValue(),accountPay==1?true:false);
			Assert.assertEquals(hs.getAliPay().booleanValue(),aliPay==1?true:false);
			Assert.assertEquals(hs.getWeiXinPay().booleanValue(),weixinPay==1?true:false);
			
			// 订单
			Order order = OrderChecker.getOrderInfo(orderId);
			int dbAccountId = order.getAccount().getId();
			int operationId = order.getOperatorId();
			Assert.assertEquals(order.getId(), dbOrderId);
			Assert.assertEquals(order.getOrderPrice().intValue(), dbOrderPrice);
			Assert.assertEquals(examProject,order.getOrderPrice().intValue());
			//医院信息
			Assert.assertEquals(retHospitalId,defHospitalId);
			Assert.assertEquals(retHospitalName,HospitalChecker.getHospitalById(defHospitalId).getName());
			//体检人信息,账号Id,免登陆支付页面体检人姓名从免登陆关系表中提取
			Assert.assertEquals(Integer.parseInt(retOrderAccount.get("id").toString()),dbAccountId);
			String sql = "select * from tb_nologin_account_info where account_id = ? order by id desc limit 1";
			List<Map<String,Object>>newlist2 = DBMapper.query(sql, operationId);
			Assert.assertEquals(retOrderAccount.get("name"),newlist2.get(0).get("name"));
			//发票
			Assert.assertNull(retInvoice.get("name"));
			// 账户余额
			sql = "select * from tb_accounting where account_id = ?";
			List<Map<String,Object>>newlist = DBMapper.query(sql, dbAccountId);
			Assert.assertEquals(newlist.get(0).get("balance"), balance);
			// 卡信息
			Assert.assertEquals(selectedCardIds.size(), 0); // 没有选择卡列表
			//已支付金额
			PayAmount payAmount = PayChecker.getPayAmountByOrderNum(order.getOrderNum(), PayConstants.OrderType.MytijianOrder);
			Assert.assertEquals(retPaidMoney,payAmount.getTotalSuccPayAmount());
			if (checkmongo) {
				waitto(mongoWaitTime);
				List<Map<String, Object>> monlist = MongoDBUtils.query("{'id':" + orderId + "}", MONGO_COLLECTION);
				Assert.assertEquals(monlist.size(), 1);
				Assert.assertEquals(monlist.get(0).get("orderPrice"), dbOrderPrice);
			}
		}
	}
	/**
	 * 实际去支付
	 * @throws Exception
	 */
	@Test(description = "免登陆线下付款支付",groups = {"qa"}, dependsOnMethods = "test_01_getNoLoginPayPage")
	public void test_02_noLoginOfflinePay() throws Exception{
		Order order = OrderChecker.getOrderInfo(orderId);
		if(order.getStatus() == OrderStatus.NOT_PAY.intValue()){
			JSONObject jo = new JSONObject();
			jo.put("orderId", orderId);
			jo.put("_site", defSite);
			jo.put("site",defSite);
			jo.put("subSite", "/"+defSite);
			jo.put("payType", PayConstants.PayMethod.OfflinePay);
			jo.put("client", "wx");
			jo.put("p", "");

			DBMapper.update("update tb_hospital_settings set accept_offline_pay = 1 where hospital_id = "+defHospitalId);
			Map<String,Object> payMethods = HospitalChecker.getHospitalSetting(defHospitalId,HospitalParam.ACCEPT_OFFLINE_PAY,HospitalParam.ALI_PAY,HospitalParam.NEED_LOCAL_PAY);
			int acceptOfflinePay = Integer.parseInt(payMethods.get(HospitalParam.ACCEPT_OFFLINE_PAY).toString());
			int need_local_pay = Integer.parseInt(payMethods.get(HospitalParam.NEED_LOCAL_PAY).toString());
			if(acceptOfflinePay == 1 ){
				HttpResult result = hc2.post(Flag.MAIN, Main_OrderPay,jo.toJSONString());
				log.info("线下付款..."+result.getBody());
				Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
				PayResponse payRes = JSON.parseObject(result.getBody(), PayResponse.class);
				Assert.assertTrue(payRes.isDone());
				Assert.assertFalse(payRes.isNeedNextAction());
				Assert.assertTrue(payRes.isSuccess());

				if(checkdb){
					if(need_local_pay == 1){
						//校验订单
						String sql = "select * from tb_order where id = ? ";
						List<Map<String,Object>> list = DBMapper.query(sql, orderId);
						Map<String,Object> orderMap = list.get(0);
						System.out.println("线下付款订单状态:"+list.get(0).get("id")+"状态..."+list.get(0).get("status"));
						int status = Integer.parseInt(orderMap.get("status").toString());
						int orderPrice = Integer.parseInt(orderMap.get("order_price").toString());
						String orderNum = orderMap.get("order_num").toString();
						Assert.assertEquals(status,OrderStatus.SITE_PAY.intValue()); //支付中
						Assert.assertEquals(payRes.getRefOrderNum(),orderNum);
						Assert.assertEquals(payRes.getRequireAmount()+"",orderPrice+"");
						//校验tb_paymentrecord
						String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "+orderId + " and p.payment_method_id = m.id and m.type = "+ PaymentTypeEnum.Offline.getCode();
						List<Map<String,Object>> paymentList = DBMapper.query(paymentSql);
						Assert.assertEquals(paymentList.size(),1);
						Map<String,Object> paymentMap = paymentList.get(0);
						Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()),0); //支付中状态
						Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()),1); //支付类型
						//校验tb_paylog 无数据
						String paylogSql = "select * from tb_paylog where order_id = "+orderId + " order by id desc limit 1";
						List<Map<String,Object>> paylogList = DBMapper.query(paylogSql);
						Assert.assertEquals(paylogList.size(),0);
						//校验tb_trade_order【交易订单表】
						List<TradeOrder> tradeOrderList = PayChecker.getTradeOrderByOrderNum(orderNum, PayConstants.TradeType.pay);
						Assert.assertEquals(tradeOrderList.size(),1); //下单
						TradeOrder to = tradeOrderList.get(0);
						Assert.assertEquals(payRes.getRefOrderNumVersion(),to.getRefOrderNumVersion());
						Assert.assertEquals(to.getRefOrderType().intValue(),1);
						Assert.assertEquals(payRes.getPayingAmount(),to.getAmount().longValue());
						Assert.assertEquals(payRes.getRequireAmount(),to.getAmount().longValue());
						Assert.assertEquals(payRes.getSuccessAmount(),to.getSuccAmount().longValue());
						Assert.assertEquals(to.getTradeStatus().intValue(),PayConstants.TradeStatus.Paying);
						Assert.assertEquals(to.getPayMethodType().intValue(),PayConstants.PayMethodBit.OfflinePayBit);
						Assert.assertEquals(to.getTradeType().intValue(),PayConstants.TradeType.pay);
						Assert.assertTrue(to.getExtraCommonParam().contains(defSite));
						//校验tb_trade_pay_record【交易支付表】
						List<TradePayRecord> tradePayRecList = PayChecker.getTradePayRecordByOrderNum(orderNum, to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
						Assert.assertEquals(tradePayRecList.size(),1);
						TradePayRecord tpr = tradePayRecList.get(0);
						Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
						Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),PayChecker.getSuitablePayMethodId(defHospitalId, PayConstants.PayMethodBit.OfflinePayBit));
						Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.OfflinePay);
						Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Paying);
						Assert.assertEquals(payRes.getRequireAmount(),tpr.getPayAmount().longValue());
						Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),PayChecker.getTradeAccountByOrderId(orderId));
						Assert.assertNull(tpr.getPayTradeSubaccountId());
						Assert.assertNull(tpr.getPayTradeSubaccountType());
						int dbReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(defHospitalId, PayConstants.PayMethodBit.OfflinePayBit);
						Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
						Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
						Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
						//交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】 第二期上线打开
						List<TradeAccountDetail> tradeAccountList = PayChecker.getTradeAccountDetail(to.getTradeOrderNum());
						Assert.assertEquals(tradeAccountList.size(),0);
						if(checkmongo){
							waitto(mongoWaitTime);
							List<Map<String,Object>> mongolist = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
							Assert.assertNotNull(mongolist);
							Assert.assertEquals(1, mongolist.size());
							Assert.assertEquals(Integer.parseInt(mongolist.get(0).get("status").toString()),OrderStatus.SITE_PAY.intValue());
						}
					}
					else {/*******************已预约订单***************/
						//校验订单
						String sql = "select * from tb_order where id = ? ";
						List<Map<String,Object>> list = DBMapper.query(sql, orderId);
						Map<String,Object> orderMap = list.get(0);
						System.out.println("线下付款订单状态:"+list.get(0).get("id")+"状态..."+list.get(0).get("status"));
						int status = Integer.parseInt(orderMap.get("status").toString());
						int orderPrice = Integer.parseInt(orderMap.get("order_price").toString());
						String orderNum = orderMap.get("order_num").toString();
						Assert.assertEquals(status,OrderStatus.ALREADY_BOOKED.intValue()); //支付中
						Assert.assertEquals(payRes.getRefOrderNum(),orderNum);
						Assert.assertEquals(payRes.getRequireAmount()+"",orderPrice+"");
						//校验tb_paymentrecord
						String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "+orderId + " and p.payment_method_id = m.id and m.type = "+PaymentTypeEnum.Offline.getCode();
						List<Map<String,Object>> paymentList = DBMapper.query(paymentSql);
						Assert.assertEquals(paymentList.size(),1);
						Map<String,Object> paymentMap = paymentList.get(0);
						Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()),1); //支付完毕
						Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()),1); //支付类型
						//校验tb_paylog 无数据
						String paylogSql = "select * from tb_paylog where order_id = "+orderId + " order by id desc limit 1";
						List<Map<String,Object>> paylogList = DBMapper.query(paylogSql);
						Assert.assertEquals(paylogList.size(),0);
						//校验tb_trade_order【交易订单表】
						List<TradeOrder> tradeOrderList = PayChecker.getTradeOrderByOrderNum(orderNum, PayConstants.TradeType.pay);
						Assert.assertEquals(tradeOrderList.size(),1); //下单
						TradeOrder to = tradeOrderList.get(0);
						Assert.assertEquals(payRes.getRefOrderNumVersion(),to.getRefOrderNumVersion());
						Assert.assertEquals(to.getRefOrderType().intValue(),1);
						Assert.assertEquals(payRes.getPayingAmount(),0);
						Assert.assertEquals(payRes.getRequireAmount(),to.getAmount().longValue());
						Assert.assertEquals(payRes.getSuccessAmount(),to.getSuccAmount().longValue());
						Assert.assertEquals(to.getTradeStatus().intValue(),PayConstants.TradeStatus.Successful);
						Assert.assertEquals(to.getPayMethodType().intValue(),PayConstants.PayMethodBit.OfflinePayBit);
						Assert.assertEquals(to.getTradeType().intValue(),PayConstants.TradeType.pay);
						Assert.assertTrue(to.getExtraCommonParam().contains(defSite+"&nologin=true"));
						//校验tb_trade_pay_record【交易支付表】
						List<TradePayRecord> tradePayRecList = PayChecker.getTradePayRecordByOrderNum(orderNum, to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
						Assert.assertEquals(tradePayRecList.size(),1);
						TradePayRecord tpr = tradePayRecList.get(0);
						Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
						Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(),PayChecker.getSuitablePayMethodId(defHospitalId, PayConstants.PayMethodBit.OfflinePayBit));
						Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.OfflinePay);
						Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Successful);
						Assert.assertEquals(payRes.getRequireAmount(),tpr.getPayAmount().longValue());
						Assert.assertEquals(tpr.getPayTradeAccountId().intValue(),PayChecker.getTradeAccountByOrderId(orderId));
						Assert.assertNull(tpr.getPayTradeSubaccountId());
						Assert.assertNull(tpr.getPayTradeSubaccountType());
						int dbReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(defHospitalId, PayConstants.PayMethodBit.BalanceBit);
						Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
						Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(),PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
						Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
						//交易tb_trade_account_detail【账户明细表】
						List<TradeAccountDetail> tradeAccountList = PayChecker.getTradeAccountDetail(to.getTradeOrderNum());
						Assert.assertEquals(tradeAccountList.size(),1);
						TradeAccountDetail ta = tradeAccountList.get(0);
						Assert.assertEquals(ta.getFlag().intValue(),1);
						Assert.assertEquals(ta.getTradeAccountId().intValue(),PayChecker.getTradeAccountByOrderId(orderId));
						Assert.assertNull(ta.getTradeSubAccountId());
						Assert.assertNull(ta.getTradeSubAccountType().intValue());
						Assert.assertEquals(ta.getBizType().intValue(),TradeAccountDetailBizType.PAYMENT);

						if(checkmongo){
							waitto(mongoWaitTime);
							List<Map<String,Object>> mongolist = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
							Assert.assertNotNull(list);
							Assert.assertEquals(1, mongolist.size());
							Assert.assertEquals(Integer.parseInt(mongolist.get(0).get("status").toString()),OrderStatus.ALREADY_BOOKED.intValue());
						}
					}
				}
			}
		}else
			log.error("该订单不是未支付状态，无法进行支付!");

	}

	@Test(description = "预约成功页面" ,groups= {"qa"}, dependsOnMethods = "test_02_noLoginOfflinePay")
	public void test_03_noLoginPayForOk() throws SqlException{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair organizationId = new BasicNameValuePair("organizationId",defHospitalId+"");
		NameValuePair _site = new BasicNameValuePair("_site", defSite);
		NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		params.add(organizationId);
		params.add(_site);
		params.add(_siteType);
		HttpResult result = hc2.post(Flag.MAIN,NoLoginPayForOk,params);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		String body = result.getBody();
		if(checkdb){
			String sql = "select * from tb_site_resource where hospital_id = ? and type = 3";
			List<Map<String,Object>> dblist = DBMapper.query(sql, defHospitalId);
			Assert.assertEquals(body,"\""+dblist.get(0).get("value").toString()+"\"");  //验证返回的二维码
		}
	}
	@DataProvider
	public Iterator<String[]> getNoLoginPage() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/nologin/getNoLoginPage.csv", 8);
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
