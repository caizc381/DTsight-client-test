package com.tijiantest.testcase.main.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.coupon.TradeCouponAccount;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.payment.trade.*;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.order.ExamOrderOperateLogDO;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderOperateTypeEnum;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.paylog.PayConsts;
import com.tijiantest.model.payment.Accounting;
import com.tijiantest.model.payment.PaymentTypeEnum;
import com.tijiantest.model.payment.trade.PayConstants.TradeType;
import com.tijiantest.testcase.main.order.OrderBaseTest;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;

/**
 * 位置：C端支付页面
 * 使用余额（+医院优惠券）支付订单
 *
 * 医院优惠券只能在医院使用
 */
public class PayTest extends OrderBaseTest{
	public static Order order = null;
//  @Test(description = "账户余额支付(订单的来源是哪里，订单支付配置取自哪里)", groups = {"qa","main_payWithBalance"},dependsOnGroups={"main_mainBook"})
	  @Test(description = "账户余额支付(订单的来源是哪里，订单支付配置取自哪里)", groups = {"qa","main_payWithBalance"})
	public void test_PayWithBalance() throws Exception {
	  System.out.println("-----------------------C端用账户余额支付Start----------------------------");
	  //获取最初用户/每天健康余额账户
	  Accounting acc = PayChecker.getAccouting(defaccountId);
	  int before_acc = acc.getBalance();
	  log.info("用户余额...."+before_acc);
	  //获取订单金额小于用户余额的未支付的订单
	  List<Order> orders = OrderChecker.getDesignatedOrderList(defaccountId,OrderStatus.NOT_PAY.intValue(),before_acc);
	  if(orders == null || orders.size() == 0){
		  orders = new ArrayList<Order>();
		  //create notpay order
		  HospitalPeriodSetting perid = HospitalChecker.getHospitalPeriodSettings(defHospitalId).get(0);
		  int retOrderId = OrderChecker.main_createOrder(httpclient,sdf.format(DateUtils.offsetDay(5)),perid.getId() , defaccountId, defHospitalId);
		  log.info("新创建的订单id.."+retOrderId);
		  orders.add(OrderChecker.getOrderInfo(retOrderId));
	  }
	  Order orderT = orders.get(0);
	  int testOrderId = orderT.getId();
	  if(orders!=null){
		  JSONObject jo = new JSONObject();
		  int organizationId = orderT.getFromSite();
		  DBMapper.update("update tb_hospital_settings set account_pay = 1 where hospital_id = "+organizationId);//确保医院是支持余额支付
		  String siteName = "/"+HospitalChecker.getSiteByHospitalId(organizationId).getUrl();
		  jo.put("orderId", testOrderId);
		  jo.put("site", "/"+siteName);
		  jo.put("subSite", "/"+siteName);
		  jo.put("payType", "");
		  jo.put("client", "wap");
		  jo.put("p", "");
		  jo.put("useBalance", "true");
		  //先查询下本地方是否有可用的优惠券
		  int couponId = 0;//优惠券ID
		  int couponPrice = 0;//优惠券面值
		  int couponOwnerId = 0;//发券人
		  int couponTradeAccountId = 0;//发券交易账户ID
		  int couponTradeSubAccountId = 0;//发券信用账户的子账号ID
		  int beforeCreditFreezeLimit = 0;//支付前的发券信用账户的冻结金额
		  int beforeBalance = 0;//支付前的发券信用账户的余额
		  int beforeCreditLimit = 0;//支付前的发券信用账户的额度
		  int hospitalCouponTradeAccountId = 0;//医院优惠券交易账户ID
		  int hospitalCouponSubAccountId = 0;//医院优惠券子账号
		  int beforeHospitalCouponBalance = 0;//医院优惠券账户余额
		  long beforeSuccPayAmount = 0;//订单支付之前已成功支付的价格
		  if(checkdb){
		  	List<UserCouponReceive> userCouponReceiveList = PayChecker.getOrderAvaliableCouponList(orderT);
		  	if(userCouponReceiveList!=null && userCouponReceiveList.size()>0){
		  		couponId = userCouponReceiveList.get(0).getId();
		  		couponPrice = userCouponReceiveList.get(0).getCouponTemplate().getPrice();
				//获取修改前客户经理的授信账户
				int ownerId = userCouponReceiveList.get(0).getCouponTemplate().getOwnerId();
				couponTradeAccountId = PayChecker.getTradeAccountIdByAccountId(ownerId);
				TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(couponTradeAccountId);
				couponTradeSubAccountId = creditAccount.getId();
				beforeCreditFreezeLimit = creditAccount.getFreezeCreditLimit();
				beforeBalance = creditAccount.getBalance();
				beforeCreditLimit = creditAccount.getCreditLimit();
				hospitalCouponTradeAccountId = PayChecker.getTradeAccountIdByRefIdAndType(orderT.getFromSite(),2);
				TradeCouponAccount couponAccount = PayChecker.getTradeCouponAccount(hospitalCouponTradeAccountId);
				hospitalCouponSubAccountId = couponAccount.getId();
				beforeHospitalCouponBalance = couponAccount.getBalance();
				jo.put("couponId",couponId+"");

				PayAmount beforePayAmount = PayChecker.getPayAmountByOrderNum(orderT.getOrderNum(), PayConstants.OrderType.MytijianOrder);
				beforeSuccPayAmount = beforePayAmount.getTotalSuccPayAmount();
			}
		  }

		  //获取接收方的医院Id
		  int fromSiteId = orderT.getFromSite();
		  int dbReceiveTradeAccountId =  PayChecker.getSuitableReceiveMethodId(fromSiteId, PayConstants.PayMethodBit.BalanceBit);
		  Accounting mtjk = PayChecker.getSubAccountting(dbReceiveTradeAccountId);
		  int mtjk_acc = mtjk.getBalance();
		  log.info("用户余额...."+before_acc+"...订单id..."+testOrderId);
		  HttpResult result = httpclient.post(Flag.MAIN,Main_OrderPay,jo.toJSONString());
		  log.info("返回..."+result.getBody());
		  Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		  PayResponse payRes = JSON.parseObject(result.getBody(), PayResponse.class);
		  Assert.assertTrue(payRes.isDone());
		  Assert.assertFalse(payRes.isNeedNextAction());
		  Assert.assertTrue(payRes.isSuccess());
		  
		  if(checkdb){
				//校验订单
			  	order = OrderChecker.getOrderInfo(testOrderId);
			    System.out.println("预约订单状态:"+ order.getId()+"状态..."+order.getStatus());
				Assert.assertEquals(order.getStatus(),OrderStatus.ALREADY_BOOKED.intValue()); 
				Assert.assertEquals(payRes.getRefOrderNum(),order.getOrderNum());
				Assert.assertEquals(payRes.getRequireAmount()+"",(order.getOrderPrice() - beforeSuccPayAmount)+"");
			    //验证订单操作日志
			    System.out.println("--------验证操作日志开始---------");
	    	    List<ExamOrderOperateLogDO> logs = OrderChecker.getOrderOperatrLog(order.getOrderNum());
	    	    Assert.assertEquals(logs.get(0).getType().intValue(), OrderOperateTypeEnum.FINISH_PAY.getCode());
	    	    Assert.assertEquals(logs.get(0).getOrderStatus().intValue(), OrderStatus.ALREADY_BOOKED.intValue());
	    	    System.out.println("--------验证操作日志结束---------");
	    	    //支付后的支付情况
			    PayAmount afterPayAmount = PayChecker.getPayAmountByOrderNum(orderT.getOrderNum(), PayConstants.OrderType.MytijianOrder);
				//校验tb_paymentrecord
				String paymentSql = "select p.* from tb_paymentrecord p ,tb_payment_method m  where p.order_id = "+testOrderId + " and p.payment_method_id = m.id and m.type = "+PaymentTypeEnum.Balance.getCode() + " order by p.id desc ";
				List<Map<String,Object>> paymentList = DBMapper.query(paymentSql);
				Assert.assertEquals(paymentList.size(),1);
				Map<String,Object> paymentMap = paymentList.get(0);
				Assert.assertEquals(Integer.parseInt(paymentMap.get("status").toString()),1); //支付成功
				Assert.assertEquals(Integer.parseInt(paymentMap.get("trade_type").toString()),1); //支付类型
				//校验tb_paylog
				String paylogSql = "select * from tb_paylog where order_id = "+testOrderId + " order by id desc limit 1";
				List<Map<String,Object>> paylogList = DBMapper.query(paylogSql);
				Map<String,Object> paylogMap = paylogList.get(0);
				Assert.assertEquals(Integer.parseInt(paylogMap.get("trade_body_type").toString()),PayConsts.TradeBodyTypes.Balance);
				Assert.assertEquals(Integer.parseInt(paylogMap.get("trade_body").toString()),OrderChecker.getOrderAccountId(testOrderId));
				Assert.assertEquals(paylogMap.get("amount").toString(),""+-(afterPayAmount.getTotalSuccPayAmount() - afterPayAmount.getHospitalCouponAmount()));
				Assert.assertEquals(Integer.parseInt(paylogMap.get("status").toString()),PayConsts.TradeStatus.Successful);
				//校验tb_trade_order【交易订单表】
				List<TradeOrder> tradeOrderList =  PayChecker.getTradeOrderByOrderNum(order.getOrderNum(),TradeType.pay);
				Assert.assertTrue(tradeOrderList.size()>=1); //下单 OR 改项目
				TradeOrder to = tradeOrderList.get(0);
				Assert.assertEquals(payRes.getRefOrderNumVersion(),to.getRefOrderNumVersion());
				Assert.assertEquals(to.getRefOrderType().intValue(),1);
				Assert.assertEquals(payRes.getPayingAmount(),0l);
				Assert.assertEquals(payRes.getRequireAmount(),to.getAmount().longValue());
				Assert.assertEquals(payRes.getSuccessAmount(),to.getSuccAmount().longValue());
				Assert.assertEquals(to.getTradeStatus().intValue(),PayConstants.TradeStatus.Successful);
				if(couponId != 0)//优惠券+余额
					Assert.assertEquals(to.getPayMethodType().intValue(),PayConstants.PayMethodBit.BalanceBit + PayConstants.PayMethodBit.CouponBit);
				else//余额
			  	Assert.assertEquals(to.getPayMethodType().intValue(),PayConstants.PayMethodBit.BalanceBit);
				Assert.assertEquals(to.getTradeType().intValue(),PayConstants.TradeType.pay);
				Assert.assertTrue(to.getExtraCommonParam().contains(siteName));
				//校验tb_trade_pay_record【交易支付表】
			    if(couponId != 0){//使用优惠券+余额混合支付
					List<TradePayRecord> tradePayRecList =  PayChecker.getTradePayRecordByOrderNum(order.getOrderNum(), to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
					Assert.assertEquals(tradePayRecList.size(),2);
					TradePayRecord tpr = tradePayRecList.get(0);//优惠券支付记录
					Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(), PayChecker.getSuitablePayMethodId(fromSiteId, PayConstants.PayMethodBit.CouponBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.Coupon);
					Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayAmount().longValue(),couponPrice);//优惠券面值
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), couponTradeAccountId);
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), couponTradeSubAccountId);
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_COUPON_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),hospitalCouponTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(), hospitalCouponSubAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_CREDIT_ACCOUNT);

					tpr = tradePayRecList.get(1);//余额支付记录
					Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(), PayChecker.getSuitablePayMethodId(fromSiteId, PayConstants.PayMethodBit.BalanceBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.Balance);
					Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Successful);
					Assert.assertEquals(tpr.getPayAmount().longValue(),payRes.getRequireAmount()-couponPrice);//订单总额-优惠券支付金额=余额支付金额
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), PayChecker.getTradeAccountByOrderId(testOrderId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), PayChecker.getSubAccounttingId( PayChecker.getTradeAccountByOrderId(testOrderId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(), PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);


				}else{//只用余额支付
					List<TradePayRecord> tradePayRecList =  PayChecker.getTradePayRecordByOrderNum(order.getOrderNum(), to.getTradeOrderNum(),PayConstants.OrderType.MytijianOrder);
					Assert.assertEquals(tradePayRecList.size(),1);
					TradePayRecord tpr = tradePayRecList.get(0);
					Assert.assertEquals(tpr.getRefOrderType().intValue(),1);
					Assert.assertEquals(tpr.getTradeMethodConfigId().intValue(), PayChecker.getSuitablePayMethodId(fromSiteId, PayConstants.PayMethodBit.BalanceBit));
					Assert.assertEquals(tpr.getTradeMethodType().intValue(),PayConstants.PayMethod.Balance);
					Assert.assertEquals(tpr.getPayStatus().intValue(),PayConstants.TradeStatus.Successful);
					Assert.assertEquals(payRes.getRequireAmount(),tpr.getPayAmount().longValue());
					Assert.assertEquals(tpr.getPayTradeAccountId().intValue(), PayChecker.getTradeAccountByOrderId(testOrderId));
					Assert.assertEquals(tpr.getPayTradeSubaccountId().intValue(), PayChecker.getSubAccounttingId( PayChecker.getTradeAccountByOrderId(testOrderId)));
					Assert.assertEquals(tpr.getPayTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tpr.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
					Assert.assertEquals(tpr.getReceiveTradeSubaccountId().intValue(), PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tpr.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);

					//交易tb_trade_account_detail,支付中不往这个表插入数据【账户明细表】
					List<TradeAccountDetail> tradeAccountList =  PayChecker.getTradeAccountDetail(to.getTradeOrderNum(),0);
					Assert.assertEquals(tradeAccountList.size(),1);
					TradeAccountDetail tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(),tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(),dbReceiveTradeAccountId);
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(), PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
					Assert.assertEquals(tad.getTradeOrderNum(),to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(),TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(),order.getOrderPrice().intValue());
					Assert.assertEquals(tad.getPreAmount().intValue(),mtjk_acc);
					Assert.assertEquals(tad.getAftAmount().intValue(),mtjk_acc);
					//出账
					tradeAccountList =  PayChecker.getTradeAccountDetail(to.getTradeOrderNum(),1);
					Assert.assertEquals(tradeAccountList.size(),1);
					tad = tradeAccountList.get(0);
					Assert.assertEquals(tad.getRefBizSn(),tpr.getSn());
					Assert.assertEquals(tad.getTradeAccountId().intValue(), PayChecker.getTradeAccountByOrderId(testOrderId));
					Assert.assertEquals(tad.getTradeSubAccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
					Assert.assertEquals(tad.getTradeSubAccountId().intValue(), PayChecker.getSubAccounttingId( PayChecker.getTradeAccountByOrderId(testOrderId)));
					Assert.assertEquals(tad.getTradeOrderNum(),to.getTradeOrderNum());
					Assert.assertEquals(tad.getBizType().intValue(),TradeAccountDetailBizType.PAYMENT);
					Assert.assertEquals(tad.getChangeAmount().intValue(),order.getOrderPrice().intValue());
					Assert.assertEquals(tad.getPreAmount().intValue(),before_acc);
					Assert.assertEquals(tad.getAftAmount().intValue(),before_acc - order.getOrderPrice());
				}

				




		}
	  }
	  System.out.println("-----------------------C端用账户余额支付End----------------------------");
  }
}
