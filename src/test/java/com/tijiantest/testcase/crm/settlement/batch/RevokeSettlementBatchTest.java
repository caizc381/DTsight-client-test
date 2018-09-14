package com.tijiantest.testcase.crm.settlement.batch;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.payment.trade.PayConstants;
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
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * 医院撤销对账单
 * 位置：CRM结算管理->结算批次->撤销对账单
 * @author huifang
 *
 */
public class RevokeSettlementBatchTest extends SettleBase{

	@Test(description = "CRM撤销对账单",groups = {"qa"},dataProvider = "crm_create_sett_revoke",dependsOnGroups = "crm_create_set",ignoreMissingDependencies = true)
	public void test_01_revokeSett(String ...args) throws SqlException, ParseException{
		//提取待确认的结算批次列表(每天健康/http测试单位/散客现场)
		List<String> snList = new ArrayList<>();
		String companyStr = args[1];
		List<TradeSettlementBatch> needConfirmLists = SettleChecker.getTradeSettlementBatch(defSettHospitalId,Integer.parseInt(companyStr),null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,null);
		if(needConfirmLists.size()>0){
			for(TradeSettlementBatch b : needConfirmLists)
				snList.add(b.getSn());
		}else{
			log.error("没有待确认的批次，无需撤销批次.....");
			return;
			//sn....
		}
		for(String sn : snList){
			//调用接口前,查询卡的冻结金额
			List<Card> settCardList = new ArrayList<Card>();
			if(checkdb){
				List<TradeSettlementCard> setCards1 = SettleChecker.getTradeSettleCardByColumn("batch_sn","'"+sn+"'" );
				for(TradeSettlementCard cardt : setCards1){
					Assert.assertEquals(cardt.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode());
					//6.卡
					int cardId = cardt.getRefCardId();
					Card card1 = CardChecker.getCardInfo(cardId);
					settCardList.add(card1);
				}

			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("sn", sn));
			HttpResult response = httpclient.get(RevokeSettlementBatch, params);
			log.info(response.getBody());
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			boolean retFlag = Boolean.parseBoolean(JsonPath.read(response.getBody(), "$.result").toString());
			Assert.assertTrue(retFlag);

			if(checkdb){
				//1.结算批次
				List<TradeSettlementBatch> batch = SettleChecker.getTradeSettlementBatchByColumn("sn", "'"+sn+"'");
				Assert.assertEquals(batch.size(),1);
				Assert.assertEquals(batch.get(0).getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				TradeSettlementBatch batcht = batch.get(0);
				int newCompanyId = batcht.getCompanyId();
				long platform_pay_amount = batcht.getPlatformPayAmount();
				long platform_refund_amount = batcht.getPlatformCouponRefundAmount();
				long platPrepaymentAmount = batcht.getPlatformPrepaymentAmount();
				long company_pay_amount = batcht.getCompanyPayAmount();
				long company_refund_amount = batcht.getCompanyRefundAmount();
				long hospital_coupon_amount = batcht.getHospitalCouponAmount();
				long hospital_coupon_refund_amount = batcht.getHospitalCouponRefundAmount();
				long offline_pay_amount = batcht.getOfflinePayAmount();
				long hospital_online_pay_amount = batcht.getHospitalOnlinePayAmount();
				long hospital_online_refund_amount = batcht.getHospitalOnlineRefundAmount();
				log.info("该批次的账单信息如下：平台支付"+platform_pay_amount+"平台退款 "+platform_refund_amount+";平台特殊退款 "+platPrepaymentAmount+";单位支付 "+company_pay_amount+";单位退款 "+company_refund_amount
						+";医院优惠券支付 "+hospital_coupon_amount+";医院优惠券退款 "+hospital_coupon_refund_amount+";线下付款 "+offline_pay_amount+";医院线上支付 "+hospital_online_pay_amount+";医院线上退款 		"+hospital_online_refund_amount);

				//2.医院平台账单
				List<TradeHospitalPlatformBill> platBill = SettleChecker.getTradeHospitalPlatformBillByColumn("batch_sn","'"+sn+"'" );
				if(platform_pay_amount ==0 && platform_refund_amount == 0 && platPrepaymentAmount == 0)
					Assert.assertEquals(platBill.size(),0);
				else{
					Assert.assertEquals(platBill.size(),1);
					Assert.assertEquals(platBill.get(0).getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				}

				//3.医院单位账单 type=0
				List<TradeHospitalCompanyBill> comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","0" );
				if(company_pay_amount ==0 && company_refund_amount == 0)
					Assert.assertEquals(comBill.size(),0);
				else {
					Assert.assertEquals(comBill.size(), 1);
					Assert.assertEquals(comBill.get(0).getStatus(), SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				}
				//4.医院优惠券账单 type=2
				comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'" ,"type","2");
				if(hospital_coupon_amount ==0 && hospital_coupon_refund_amount == 0)
					Assert.assertEquals(comBill.size(),0);
				else {
					Assert.assertEquals(comBill.size(), 1);
					Assert.assertEquals(comBill.get(0).getStatus(), SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				}
				//5.医院线上账单 type=3
				comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","3" );
				if(hospital_online_pay_amount ==0 && hospital_online_refund_amount == 0)
					Assert.assertEquals(comBill.size(),0);
				else {
					Assert.assertEquals(comBill.size(), 1);
					Assert.assertEquals(comBill.get(0).getStatus(), SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				}
				//6.医院线下单位账单 type=4
				comBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null,false,"batch_sn","'"+sn+"'","type","4" );
				if(offline_pay_amount ==0)
					Assert.assertEquals(comBill.size(),0);
				else {
					Assert.assertEquals(comBill.size(), 1);
					Assert.assertEquals(comBill.get(0).getStatus(), SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				}

				//7.批次内结算体检订单
				List<TradeSettlementOrder> setOrders = SettleChecker.getTradeSettleOrderByColumn("batch_sn","'"+sn+"'" );
				for(TradeSettlementOrder so : setOrders)
					Assert.assertEquals(so.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());

				//8.批次内结算收款订单
				List<TradeSettlementPaymentOrder> setPaymentOrders = SettleChecker.getTradeSettlePaymentOrderByColumn("batch_sn","'"+sn+"'" );
				for(TradeSettlementPaymentOrder so : setPaymentOrders)
					Assert.assertEquals(so.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());

				//9.批次内结算卡
				List<TradeSettlementCard> setCards = SettleChecker.getTradeSettleCardByColumn("batch_sn","'"+sn+"'" );
				for(TradeSettlementCard card : setCards){
					Assert.assertEquals(card.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
					//9.卡
					int cardId = card.getRefCardId();
					Card card1 = CardChecker.getCardInfo(cardId);
					Assert.assertEquals(card1.getFreezeBalance().longValue(),0l);//冻结金额为0
					for(Card srcCard : settCardList){
						if(srcCard.getId() == cardId)//金额还原验证tb_card.balance
							Assert.assertEquals(card1.getBalance().intValue(),srcCard.getBalance().intValue()+srcCard.getFreezeBalance().intValue());
					}
				}

				//10.批次内结算退款
				List<TradeSettlementRefund> setRefunds = SettleChecker.getTradeSettleRefundByColumn("batch_sn","'"+sn+"'" );
				for(TradeSettlementRefund re : setRefunds){
					Assert.assertEquals(re.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
					String refOrderNum = re.getRefOrderNum();
					int ref_order_type = re.getRefOrderType();
					//订单结算关系表更新退款为需要退款
					if(ref_order_type == PayConstants.OrderType.MytijianOrder){//体检订单
						List<ExamOrderSettlementDO> setDto = SettleChecker.getExamOrderSettleByColumn("order_num","'"+refOrderNum+"'" );
						Assert.assertEquals(setDto.size(),1);
						ExamOrderSettlementDO setD = setDto.get(0);
						Assert.assertEquals(setD.getRefundSettlement(),ExamOrderRefundSettleEnum.NEED_REFUND.getCode());//撤销结算，退款变成需要退款
					}
					if(ref_order_type == PayConstants.OrderType.PaymentOrder){//收款订单
						List<PaymentOrderSettlementDO> setDto = SettleChecker.getPaymentOrderSettleByColumn("order_num","'"+refOrderNum+"'" );
						Assert.assertEquals(setDto.size(),1);
						PaymentOrderSettlementDO setD = setDto.get(0);
						Assert.assertEquals(setD.getRefundSettlement(),ExamOrderRefundSettleEnum.NEED_REFUND.getCode());//撤销结算，退款变成需要退款
					}
				}
				//11.批次内结算特殊退款
				List<TradePrepaymentRecord> setPayRecords = SettleChecker.getTradePrementRecordByColumn("batch_sn","'"+sn+"'" );
				for(TradePrepaymentRecord set : setPayRecords)
					Assert.assertEquals(set.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
				//12.订单结算关系表
				List<ExamOrderSettlementDO> examOrders = SettleChecker.getExamOrderSettleByColumn("settlement_batch_sn","'"+sn+"'" );
				for(ExamOrderSettlementDO S : examOrders){
					Assert.assertEquals(S.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
					//10.mongo settleSign=4
					if(checkmongo){
						List<Map<String,Object>> list = MongoDBUtils.query("{'orderNum':'"+S.getOrderNum()+"'}", MONGO_COLLECTION);
						Assert.assertEquals(1, list.size());
						Assert.assertTrue(Integer.parseInt(list.get(0).get("settleSign").toString()) == SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
					}
				}
				//13.收款订单结算关系表（部分退款的付款订单只能在一个批次内结算支付和退款）
				List<PaymentOrderSettlementDO> paymentOrders = SettleChecker.getPaymentOrderSettleByColumn("settlement_batch_sn","'"+sn+"'" );
				for(PaymentOrderSettlementDO s: paymentOrders){
					Assert.assertEquals(s.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode());
					Assert.assertTrue(s.getRefundSettlement() != ExamOrderRefundSettleEnum.REFUND_OK.getCode()); //付款订单如果有退款，不会再是已结算退款
				}


			}
		}

			
	}

	@DataProvider
	public Iterator<String[]> crm_create_sett_revoke(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/crm_create_sett_revoke.csv",18);
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
