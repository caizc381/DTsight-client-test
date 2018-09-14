package com.tijiantest.testcase.crm.paymentOrder;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.payment.trade.*;
import com.tijiantest.model.paymentOrder.PaymentOrderStatusEnum;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 医院撤销对账单
 * 位置：CRM->订单&用户->收款订单
 * 撤销操作（未结算&已支付订单）
 * @author huifang
 *
 */
public class RevokePaymentOrderTest extends CrmBase{

	@Test(description = "CRM撤销未结算已支付付款订单",groups = {"qa"})
	public void test_01_revokePaymentOrder() throws Exception {
		int hosptialId = defhospital.getId();
		int managerId = defaccountId;
		//提取可撤销的订单编号
		String orderNum = null;
		//医院内部未结算且已支付的收款订单列表
		String requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 2  and " +
				"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+ " order by o.id desc";
		List<PaymentOrder> notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
		if(notConfirmPaylist.size()>0){
			orderNum = notConfirmPaylist.get(0).getOrderNum();
		}else{
			log.info("通过修改数据来获取已支付的收款订单");
			requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 0  and " +
					"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() + " order by o.id desc";
			notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
			if(notConfirmPaylist.size()>0){
				PaymentOrder paymentOrder = notConfirmPaylist.get(0);
				orderNum = paymentOrder.getOrderNum();
				PayChecker.updatePaymentOrderPaySuccess(orderNum);
			}else{
				log.error("没有已支付的收款订单，请手动创建.....");
				return;
			}
		}
		String beforeTime = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderNum", orderNum));
		HttpResult response = httpclient.get(Payment_RevokePaymentOrder, params);
		log.info(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		boolean retFlag = Boolean.parseBoolean(JsonPath.read(response.getBody(), "$.success").toString());
		Assert.assertTrue(retFlag);
		
		if(checkdb) {
			//1.付款订单
			PaymentOrder paymentOrder = OrderChecker.getPaymentOrderInfo(orderNum);
			Assert.assertEquals(paymentOrder.getStatus().intValue(), PaymentOrderStatusEnum.REVOKE.getCode());
			//2.付款结算关系表
			List<PaymentOrderSettlementDO> paymentOrderSettlementDOS = SettleChecker.getPaymentOrderSettleByColumn("order_num","'"+orderNum+"'");
			Assert.assertEquals(paymentOrderSettlementDOS.size(),1);
			PaymentOrderSettlementDO paymentOrderSettlementDO = paymentOrderSettlementDOS.get(0);
			Assert.assertEquals(paymentOrderSettlementDO.getRefundSettlement(),ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode());
			Assert.assertEquals(paymentOrderSettlementDO.getHospitalSettlementStatus(),SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode());
			//3.退款申请表无数据
			String sql = "select * from tb_order_refund_apply where order_num = "+orderNum ;
			List<Map<String,Object>> refundList = DBMapper.query(sql);
			Assert.assertEquals(refundList.size(),0);
			//4.交易记录表
			List<TradeOrder> tradeOrders =  PayChecker.getTradeOrderByOrderNum(orderNum, PayConstants.TradeType.refund);
			Assert.assertEquals(tradeOrders.size(),1);
			TradeOrder tradeOrder = tradeOrders.get(0);
			Assert.assertEquals(tradeOrder.getRefOrderType().intValue(),PayConstants.OrderType.PaymentOrder);
			Assert.assertEquals(tradeOrder.getAmount(),paymentOrder.getAmount());
			Assert.assertEquals(tradeOrder.getSuccAmount(),paymentOrder.getAmount());
			Assert.assertEquals(tradeOrder.getTradeStatus().intValue(),PayConstants.TradeStatus.Successful);
			//5.交易退款记录表
			List<TradeRefundRecord> tradeRefundRecords = PayChecker.getTradeRefundRecordByOrderNum(orderNum,tradeOrder.getTradeOrderNum(),PayConstants.OrderType.PaymentOrder);
			Assert.assertEquals(tradeRefundRecords.size(),1);
			TradeRefundRecord tradeRefundRecord = tradeRefundRecords.get(0);
			Assert.assertTrue(tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Alipay)
					||tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Wxpay));//trade_method_type 退款方式（支付宝/微信）
			Assert.assertEquals(tradeRefundRecord.getRefundStatus(), RefundConstants.RefundStatus.REFUNDING);//refund_status=2
			Assert.assertEquals(tradeRefundRecord.getRefundAmount(),paymentOrder.getAmount());
			Assert.assertNull(tradeRefundRecord.getReceiveTradeSubAccountType()); //三方付款账号
			if(tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Alipay)){
				int dbRefundTradeAccountId = PayChecker.getSuitableReceiveMethodId(hosptialId,
						PayConstants.PayMethodBit.AlipayBit);
				Assert.assertEquals(tradeRefundRecord.getRefundTradeAccountId().intValue(),dbRefundTradeAccountId);
				Assert.assertEquals(tradeRefundRecord.getRefundTradeSubAccountId().intValue(),
						PayChecker.getSubAccounttingId(dbRefundTradeAccountId));
			}else{
				int dbRefundTradeAccountId = PayChecker.getSuitableReceiveMethodId(hosptialId,
						PayConstants.PayMethodBit.WxpayBit);
				Assert.assertEquals(tradeRefundRecord.getRefundTradeAccountId().intValue(),dbRefundTradeAccountId);
				Assert.assertEquals(tradeRefundRecord.getRefundTradeSubAccountId().intValue(),
						PayChecker.getSubAccounttingId(dbRefundTradeAccountId));
			}
			Assert.assertEquals(tradeRefundRecord.getRefundTradeSubAccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			Assert.assertNull(tradeRefundRecord.getReceiveTradeSubAccountType());
			Assert.assertNull(tradeRefundRecord.getReceiveTradeAccountId());
			Assert.assertNull(tradeRefundRecord.getReceiveTradeSubAccountId());
			Assert.assertNull(tradeRefundRecord.getReceiveTradeAccountSnap());
			//6.交易流水表
			List<TradeAccountDetail> tradeAccountList = PayChecker.getTradeAccountDetail(tradeOrder.getTradeOrderNum(), 0);
			Assert.assertEquals(tradeAccountList.size(), 0);
			tradeAccountList = PayChecker.getTradeAccountDetail(tradeOrder.getTradeOrderNum(), 1);
			TradeAccountDetail detail = tradeAccountList.get(0);
			Assert.assertEquals(tradeAccountList.size(), 1);
			Assert.assertEquals(detail.getBizType().intValue(),TradeAccountDetailBizType.REFUND);
			Assert.assertEquals(detail.getChangeAmount(),paymentOrder.getAmount());
			Assert.assertEquals(detail.getTradeSubAccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
			if(tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Alipay)){
				int dbRefundTradeAccountId = PayChecker.getSuitableReceiveMethodId(hosptialId,
						PayConstants.PayMethodBit.AlipayBit);
				Assert.assertEquals(detail.getTradeAccountId().intValue(),dbRefundTradeAccountId);
				Assert.assertEquals(detail.getTradeSubAccountId().intValue(),
						PayChecker.getSubAccounttingId(dbRefundTradeAccountId));
		}else{
				int dbRefundTradeAccountId = PayChecker.getSuitableReceiveMethodId(hosptialId,
						PayConstants.PayMethodBit.WxpayBit);
				Assert.assertEquals(detail.getTradeAccountId().intValue(),dbRefundTradeAccountId);
				Assert.assertEquals(detail.getTradeSubAccountId().intValue(),
						PayChecker.getSubAccounttingId(dbRefundTradeAccountId));
			}
			//7.流转日志表
			List<TradeCommonLogResultDTO>  tradeCommonLogResultDTOS = SettleChecker.getTradeCommonLogList(paymentOrder.getId()+"", LogTypeEnum.LOG_TYPE_REFUND.getValue(),null);
			TradeCommonLogResultDTO common = tradeCommonLogResultDTOS.get(0);
			Assert.assertEquals(common.getOperatorType().intValue(),1);//CRM操作员
			Assert.assertEquals(common.getOperatorId().intValue(),managerId);
			Assert.assertEquals(common.getOperation(),"撤销");
			Assert.assertEquals(common.getOperatorName(), AccountChecker.getAccountById(managerId).getName());
			Assert.assertEquals(common.getGmtModified().compareTo(simplehms.parse(beforeTime)),1);
		}
	}
	
}
