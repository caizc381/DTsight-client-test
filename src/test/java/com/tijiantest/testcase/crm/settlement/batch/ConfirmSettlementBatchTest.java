package com.tijiantest.testcase.crm.settlement.batch;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.coupon.CouponTemplate;
import com.tijiantest.model.coupon.TradeCouponAccount;
import com.tijiantest.model.coupon.TradeCreditAccount;
import com.tijiantest.model.coupon.UserCouponReceive;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.payment.trade.TradeOrder;
import com.tijiantest.model.settlement.*;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.company.CompanySettleModeEnum;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 医院确认对账单
 * 位置：CRM结算管理->结算批次->确认对账单
 *
 * @author huifang
 *
 */
public class ConfirmSettlementBatchTest extends SettleBase{

	@Test(description = "CRM确认对账单",groups = {"qa","crm_confirmBatch"},dataProvider = "crm_confirmBatch",dependsOnGroups = "crm_create_set",ignoreMissingDependencies = true)
	public void test_01_confirmSett(String ...args) throws SqlException, ParseException{
		String newCompanyStr = args[1];
		int newCompanyId = Integer.parseInt(newCompanyStr);
//		int	newCompanyId = 4400008;
		//提取待确认的结算批次列表
		String sn = null;
		List<TradeSettlementBatch> needConfirmLists = SettleChecker.getTradeSettlementBatch(defSettHospitalId,newCompanyId,null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,null);
		if(needConfirmLists.size()>0){
			sn = needConfirmLists.get(0).getSn();
		}else{
			log.error(newCompanyId+"单位没有待确认的批次,无法验证CRM确认对账单.....");
			return;
			//sn....
		}
		long parentCardAmount = 0l;
		Map<Integer,TradeCreditAccount> beforeCreditAccountMap = new HashMap<Integer,TradeCreditAccount>();//ownerId->授信账户
		Map<Integer,TradeCreditAccount> beforeMtjkCreditAccountMap = new HashMap<>();//每天健康 ->授信账户
		TradeCouponAccount beforeCouponAccount = null;//医院优惠券账户
		TradeCouponAccount beforeMtjkCouponAccount = null;//每天健康优惠券账户
		int hospitalCouponTradeAccountId = 0; //医院优惠券交易账户ID
		int platformCouponTradeAccountId = 0;//平台优惠券交易账户ID
		TradeSettlementBatch batcht = null;//批次
		long platform_pay_amount = 0l;
		long platform_refund_amount = 0l;
		long platPrepaymentAmount = 0l;
		long company_pay_amount = 0l;
		long company_refund_amount =  0l;
		long hospital_coupon_amount = 0l;
		long hospital_coupon_refund_amount =  0l;
		long offline_pay_amount =  0l;
		long hospital_online_pay_amount =  0l;
		long hospital_online_refund_amount =  0l;
		long platform_coupon_amount = 0l;
		long platform_coupon_refund_amount = 0l;
		if(checkdb){
			//提取母卡金额
			Card parentCard = CardChecker.getParentCardByManagerId(defSettAccountId);
			parentCardAmount = parentCard.getBalance().longValue();

			//结算批次各项数据
			List<TradeSettlementBatch> batch  = SettleChecker.getTradeSettlementBatchByColumn("sn", "'"+sn+"'");
			Assert.assertEquals(batch.size(),1);
			batcht = batch.get(0);
			Assert.assertEquals(newCompanyId,batcht.getCompanyId().intValue());
			platform_pay_amount = batcht.getPlatformPayAmount();
			platform_refund_amount = batcht.getPlatformCouponRefundAmount();
			platPrepaymentAmount = batcht.getPlatformPrepaymentAmount();
			company_pay_amount = batcht.getCompanyPayAmount();
			company_refund_amount = batcht.getCompanyRefundAmount();
			hospital_coupon_amount = batcht.getHospitalCouponAmount();
			hospital_coupon_refund_amount = batcht.getHospitalCouponRefundAmount();
			platform_coupon_amount = batcht.getPlatformCouponAmount();
			platform_coupon_refund_amount = batcht.getPlatformCouponRefundAmount();
			offline_pay_amount = batcht.getOfflinePayAmount();
			hospital_online_pay_amount = batcht.getHospitalOnlinePayAmount();
			hospital_online_refund_amount = batcht.getHospitalOnlineRefundAmount();
			log.info("该批次的账单信息如下：平台支付"+platform_pay_amount+"平台退款 "+platform_refund_amount+";平台特殊退款 "+platPrepaymentAmount+";单位支付 "+company_pay_amount+";单位退款 "+company_refund_amount
					+";医院优惠券支付 "+hospital_coupon_amount+";医院优惠券退款 "+hospital_coupon_refund_amount+";线下付款 "+offline_pay_amount+";医院线上支付 "+hospital_online_pay_amount+";医院线上退款 		"+hospital_online_refund_amount+";平台优惠券支付"+platform_coupon_amount+";平台优惠券退款"+platform_coupon_refund_amount);

			//提取发行优惠券的客户经理授信账户金额信息
			if(hospital_coupon_amount != 0){
				List<TradeSettlementOrder> setOrders2 = SettleChecker.getTradeSettleOrderByColumn("batch_sn","'"+sn+"'" );//找到批次内的订单的优惠券支付部分
				for(TradeSettlementOrder so : setOrders2) {
					List<TradeOrder> tradeOrders = PayChecker.getTradeOrderByOrderNum(so.getRefOrderNum(), PayConstants.TradeType.pay);
					for (TradeOrder to : tradeOrders) {
						//提取拥有医院优惠券的交易订单，并且将这些优惠券的ownerId找到（券所属人)
						if (to.getHospitalCouponId() != null) {
							List<UserCouponReceive> userCouponReceiveList = PayChecker.getUserCouponList("id", to.getHospitalCouponId().intValue() + "");
							String tempBatch = userCouponReceiveList.get(0).getTemplateBatchNum();
							List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+tempBatch+"'");
							int ownerId = couponTemplateList.get(0).getOwnerId();
							int hospitalCoupAmount = couponTemplateList.get(0).getPrice();
							int ownerTradeAccountId = PayChecker.getTradeAccountIdByAccountId(ownerId);
							TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(ownerTradeAccountId);
							//将ownerId->creditAccount装在到Map容器中
							beforeCreditAccountMap.put(ownerId, creditAccount);
							log.info("医院客户经理授信账户"+ownerId+"授信账户额度"+creditAccount.getCreditLimit()+";授信账户余额:"+creditAccount.getBalance()+";冻结金额"+creditAccount.getFreezeCreditLimit());

						}
					}
				}
			}
			//提取机构的优惠券信息
			hospitalCouponTradeAccountId = PayChecker.getTradeAccountIdByRefIdAndType(defSettHospitalId,2);
			beforeCouponAccount = PayChecker.getTradeCouponAccount(hospitalCouponTradeAccountId);
			log.info("医院优惠券账户额度"+hospitalCouponTradeAccountId+";账户余额:"+beforeCouponAccount.getBalance());


			//提取发行优惠券的每天健康客户经理授信账户金额信息
			if(platform_coupon_amount != 0){
				List<TradeSettlementOrder> setOrders2 = SettleChecker.getTradeSettleOrderByColumn("batch_sn","'"+sn+"'" );//找到批次内的订单的优惠券支付部分
				for(TradeSettlementOrder so : setOrders2) {
					List<TradeOrder> tradeOrders = PayChecker.getTradeOrderByOrderNum(so.getRefOrderNum(), PayConstants.TradeType.pay);
					for (TradeOrder to : tradeOrders) {
						//提取拥有平台优惠券的交易订单，并且将这些优惠券的ownerId找到（券所属人)
						if (to.getPlatformCouponId() != null) {
							List<UserCouponReceive> userCouponReceiveList = PayChecker.getUserCouponList("id", to.getPlatformCouponId().intValue() + "");
							String tempBatch = userCouponReceiveList.get(0).getTemplateBatchNum();
							List<CouponTemplate> couponTemplateList = PayChecker.getCouponTemplateList("batch_num","'"+tempBatch+"'");
							int ownerId = couponTemplateList.get(0).getOwnerId();
							int platformCoupAmount = couponTemplateList.get(0).getPrice();
							int ownerTradeAccountId = PayChecker.getTradeAccountIdByAccountId(ownerId);
							TradeCreditAccount creditAccount = PayChecker.getTradeCreditAccount(ownerTradeAccountId);
							//将ownerId->creditAccount装在到Map容器中
							beforeMtjkCreditAccountMap.put(ownerId, creditAccount);
							log.info("每天健康客户经理授信账户"+ownerId+"授信账户额度"+creditAccount.getCreditLimit()+";授信账户余额:"+creditAccount.getBalance()+";冻结金额"+creditAccount.getFreezeCreditLimit());

						}
					}
				}
			}

			//提取每天健康的优惠券信息
			platformCouponTradeAccountId = PayChecker.getTradeAccountIdByRefIdAndType(196,2);
			beforeMtjkCouponAccount = PayChecker.getTradeCouponAccount(platformCouponTradeAccountId);
			log.info("每天健康优惠券账户额度"+platformCouponTradeAccountId+";账户余额:"+beforeMtjkCouponAccount.getBalance());

		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sn", sn));
		HttpResult response = httpclient.get(ConfirmSettlementBatch, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		log.info("body.."+response.getBody());
		boolean retFlag = Boolean.parseBoolean(JsonPath.read(response.getBody(), "$.result").toString());
		Assert.assertTrue(retFlag);
		
		if(checkdb){
			//1.结算批次(获取最新的批次数据)
			List<TradeSettlementBatch> afterBatch  = SettleChecker.getTradeSettlementBatchByColumn("sn", "'"+sn+"'");
			Assert.assertEquals(afterBatch.size(),1);
			batcht = afterBatch.get(0);
			Assert.assertEquals(batcht.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
			//2.医院平台账单
			List<TradeHospitalPlatformBill> platBill = SettleChecker.getTradeHospitalPlatformBillByColumn("batch_sn","'"+sn+"'" );
			if(platform_pay_amount == 0 && platform_refund_amount == 0 && platPrepaymentAmount == 0){
				Assert.assertEquals(platBill.size(),0);
			}else{
				Assert.assertEquals(platBill.size(),1);
				TradeHospitalPlatformBill plat = platBill.get(0);
				Assert.assertEquals(plat.getStatus(),HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode());
				Assert.assertEquals(plat.getIsDeleted().longValue(),0);//未删除
			}

			//3.医院单位账单 type = 0
			List<TradeHospitalCompanyBill> comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","0" );
			if(company_pay_amount == 0 && company_refund_amount == 0)
				Assert.assertEquals(comBill.size(),0);
			else{
				Assert.assertEquals(comBill.size(),1);
				TradeHospitalCompanyBill com = comBill.get(0);
				Assert.assertEquals(com.getStatus(),HospitalCompanyBillStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());
				Assert.assertEquals(com.getIsDeleted().longValue(),0);//未删除

			}


			//4.医院优惠券账单 type=2
			comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","2" );
			if(hospital_coupon_amount == 0 && hospital_coupon_refund_amount == 0)
						Assert.assertEquals(comBill.size(),0);
			else{
						Assert.assertEquals(comBill.size(),1);
						TradeHospitalCompanyBill com = comBill.get(0);
						Assert.assertEquals(com.getStatus(),HospitalCompanyBillStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());
						Assert.assertEquals(com.getIsDeleted().longValue(),0);//未删除
			}

			//5.医院线上支付账单 type=3
			comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","3" );
			if(hospital_online_pay_amount==0 && hospital_online_refund_amount==0)
				Assert.assertEquals(comBill.size(),0);
			else{
				Assert.assertEquals(comBill.size(),1);
				TradeHospitalCompanyBill com = comBill.get(0);
				Assert.assertEquals(com.getStatus(),HospitalCompanyBillStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());
				Assert.assertEquals(com.getIsDeleted().longValue(),0);//未删除
			}

			//6.医院线下账单 type=4
			comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","4" );
			if(offline_pay_amount == 0)
				Assert.assertEquals(comBill.size(),0);
			else{
				Assert.assertEquals(comBill.size(),1);
				TradeHospitalCompanyBill com = comBill.get(0);
				Assert.assertEquals(com.getStatus(),HospitalCompanyBillStatusEnum.HOSPITAL_HAS_CONFIRMED.getCode());
				Assert.assertEquals(com.getIsDeleted().longValue(),0);//未删除
			}


			//7.批次内结算体检订单
			List<TradeSettlementOrder> setOrders = SettleChecker.getTradeSettleOrderByColumn("batch_sn","'"+sn+"'" );
			for(TradeSettlementOrder so : setOrders)
				Assert.assertEquals(so.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());

			//8.批次内结算收款订单
			List<TradeSettlementPaymentOrder> setPaymentOrders = SettleChecker.getTradeSettlePaymentOrderByColumn("batch_sn","'"+sn+"'" );
			for(TradeSettlementPaymentOrder so : setPaymentOrders)
				Assert.assertEquals(so.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());

			//9.批次内结算卡
			int settle_mode = CompanyChecker.getHospitalCompanyById(newCompanyId).getSettlementMode();
			List<TradeSettlementCard> setCards = SettleChecker.getTradeSettleCardByColumn("batch_sn","'"+sn+"'" );
			for(TradeSettlementCard card : setCards){
				Assert.assertEquals(card.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
				int cardId = card.getRefCardId();
				Card c = CardChecker.getCardInfo(cardId);
				Card cprarent = CardChecker.getCardById(c.getParentCardId());
				//按项目冻结金额进入母卡中
				if(settle_mode == CompanySettleModeEnum.USE_PROJECT.getCode()){
					Assert.assertEquals(c.getFreezeBalance().longValue(), 0l);//冻结金额为0
					//母卡金额可以随着定时任务而变化，因此不做判断
//					Assert.assertEquals(cprarent.getBalance().longValue(), c.getFreezeBalance().longValue() + parentCardAmount );
				}else{//按人数冻结金额不变&母卡金额不变
					Assert.assertEquals(c.getFreezeBalance().longValue(),card.getRecycleAmount().longValue());//冻结金额为结算回收金额
					//母卡金额可以随着定时任务而变化，因此不做判断
//					Assert.assertEquals(cprarent.getBalance().longValue(),parentCardAmount);
				}
			}
			//10.批次内结算退款
			List<TradeSettlementRefund> setRefunds = SettleChecker.getTradeSettleRefundByColumn("batch_sn","'"+sn+"'" );
			for(TradeSettlementRefund re : setRefunds){
				Assert.assertEquals(re.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
				String orderNum = re.getRefOrderNum();
				int ref_order_type = re.getRefOrderType();
				if(ref_order_type == PayConstants.OrderType.MytijianOrder){//体检订单
					List<ExamOrderSettlementDO> examOrdert = SettleChecker.getExamOrderSettleByColumn("order_num","'"+orderNum+"'");
					for(ExamOrderSettlementDO S : examOrdert)
						Assert.assertEquals(S.getRefundSettlement().intValue(),ExamOrderRefundSettleEnum.REFUND_OK.getCode().intValue()); //已结算已结算订单退款
				}
				if(ref_order_type == PayConstants.OrderType.PaymentOrder){//收款订单
					List<PaymentOrderSettlementDO> examOrdert = SettleChecker.getPaymentOrderSettleByColumn("order_num","'"+orderNum+"'");
					for(PaymentOrderSettlementDO S : examOrdert)
						Assert.assertEquals(S.getRefundSettlement().intValue(),ExamOrderRefundSettleEnum.REFUND_OK.getCode().intValue()); //已结算已结算订单退款
				}
			}
			//11.批次内结算特殊退款
			List<TradePrepaymentRecord> setPayRecords = SettleChecker.getTradePrementRecordByColumn("batch_sn","'"+sn+"'" );
			for(TradePrepaymentRecord set : setPayRecords)
				Assert.assertEquals(set.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
			//12.订单结算关系表
			List<ExamOrderSettlementDO> examOrders = SettleChecker.getExamOrderSettleByColumn("settlement_batch_sn","'"+sn+"'" );
			for(ExamOrderSettlementDO S : examOrders){
				Assert.assertEquals(S.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
				//9.mongo settleSign=4
				if(checkmongo){
					List<Map<String,Object>> list = MongoDBUtils.query("{'orderNum':'"+S.getOrderNum()+"'}", MONGO_COLLECTION);
					Assert.assertEquals(1, list.size());
					Assert.assertTrue(Integer.parseInt(list.get(0).get("settleSign").toString()) == SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());
				}
			}
			//13.收款订单结算关系表
			List<PaymentOrderSettlementDO> paymentOrders = SettleChecker.getPaymentOrderSettleByColumn("settlement_batch_sn","'"+sn+"'" );
			for(PaymentOrderSettlementDO s: paymentOrders)
				Assert.assertEquals(s.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode());

			//14.(当有优惠券账单的时候才会进行此步骤)
			//医院优惠券支付部分结算->医院优惠券账户余额-，授信账户余额+，授信账户冻结金额-
			//医院优惠券退款部分结算在订单回单撤销的时候修改授信账户和医院优惠券账户，不再在确认结算时变更
			if(hospital_coupon_amount != 0 ){
					TradeCouponAccount afterCoupAccount = PayChecker.getTradeCouponAccount(hospitalCouponTradeAccountId);
					Assert.assertEquals(afterCoupAccount.getBalance(),beforeCouponAccount.getBalance() - hospital_coupon_amount);//医院机构优惠券余额减少
					log.info("医院优惠券账户"+afterCoupAccount.getBalance());
				Set<Integer> ownerSet = beforeCreditAccountMap.keySet();
				    int beforeCreditBalanceTotal = 0;;
				    int beforeCreditFreezeLimitTotal = 0;
				    int beforeCreditLimitTotal = 0;
				    int afterCreditBlanceTotal = 0;
				    int afterCreditFreezeLimitTotal = 0;
				    int afterCreditLimitTotal = 0;
				    for(Integer owneridS : ownerSet) {
				    	TradeCreditAccount beforeCreditAccount = beforeCreditAccountMap.get(owneridS);
				    	TradeCreditAccount afterCreditAccount = PayChecker.getTradeCreditAccount(PayChecker.getTradeAccountIdByAccountId(owneridS));
				    	beforeCreditBalanceTotal += beforeCreditAccount.getBalance();
				    	beforeCreditFreezeLimitTotal += beforeCreditAccount.getFreezeCreditLimit();
				    	beforeCreditLimitTotal += beforeCreditAccount.getCreditLimit();
				    	afterCreditBlanceTotal += afterCreditAccount.getBalance();
				    	afterCreditFreezeLimitTotal += afterCreditAccount.getFreezeCreditLimit();
				    	afterCreditLimitTotal += afterCreditAccount.getCreditLimit();
					}
					Assert.assertEquals(afterCreditBlanceTotal,beforeCreditBalanceTotal + hospital_coupon_amount); //授信账户余额+
				    Assert.assertEquals(afterCreditFreezeLimitTotal,beforeCreditFreezeLimitTotal - hospital_coupon_amount);//授信账户冻结金额-
				    Assert.assertEquals(afterCreditLimitTotal,beforeCreditLimitTotal);//授信账户总额度不变
				 log.info("医院客户经理授信账户授信账户额度"+afterCreditBlanceTotal+";授信账户余额:"+afterCreditLimitTotal+";冻结金额"+afterCreditFreezeLimitTotal);
				}

				//15.(当有平台红包账单的时候才会进行此步骤)
			//平台红包支付部分结算-> 每天健康站点优惠券账户余额-，授信账户余额+，授信账户冻结金额-
			//平台红包退款部分结算在订单回单撤销的时候修改授信账户和每天健康优惠券账户，不再在确认结算时变更
			if(platform_coupon_amount != 0 ){
				TradeCouponAccount afterCoupAccount = PayChecker.getTradeCouponAccount(platformCouponTradeAccountId);
				Assert.assertEquals(afterCoupAccount.getBalance(),beforeMtjkCouponAccount.getBalance() - platform_coupon_amount);//医院机构优惠券余额减少
				log.info("每天健康优惠券账户"+afterCoupAccount.getBalance());
				Set<Integer> ownerSet = beforeMtjkCreditAccountMap.keySet();
				int beforeCreditBalanceTotal = 0;;
				int beforeCreditFreezeLimitTotal = 0;
				int beforeCreditLimitTotal = 0;
				int afterCreditBlanceTotal = 0;
				int afterCreditFreezeLimitTotal = 0;
				int afterCreditLimitTotal = 0;
				for(Integer owneridS : ownerSet) {
					TradeCreditAccount beforeCreditAccount = beforeMtjkCreditAccountMap.get(owneridS);
					TradeCreditAccount afterCreditAccount = PayChecker.getTradeCreditAccount(PayChecker.getTradeAccountIdByAccountId(owneridS));
					beforeCreditBalanceTotal += beforeCreditAccount.getBalance();
					beforeCreditFreezeLimitTotal += beforeCreditAccount.getFreezeCreditLimit();
					beforeCreditLimitTotal += beforeCreditAccount.getCreditLimit();
					afterCreditBlanceTotal += afterCreditAccount.getBalance();
					afterCreditFreezeLimitTotal += afterCreditAccount.getFreezeCreditLimit();
					afterCreditLimitTotal += afterCreditAccount.getCreditLimit();
				}
				Assert.assertEquals(afterCreditBlanceTotal,beforeCreditBalanceTotal + platform_coupon_amount); //授信账户余额+
				Assert.assertEquals(afterCreditFreezeLimitTotal,beforeCreditFreezeLimitTotal - platform_coupon_amount);//授信账户冻结金额-
				Assert.assertEquals(afterCreditLimitTotal,beforeCreditLimitTotal);//授信账户总额度不变
				log.info("每天健康客户经理授信账户授信账户额度"+afterCreditBlanceTotal+";授信账户余额:"+afterCreditLimitTotal+";冻结金额"+afterCreditFreezeLimitTotal);

			}

		}
	}

	@DataProvider
	public Iterator<String[]> crm_confirmBatch(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/crm_prepareSettData.csv",18);
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
