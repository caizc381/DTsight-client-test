package com.tijiantest.testcase.crm.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.payment.trade.*;
import com.tijiantest.model.paymentOrder.PaymentOrderStatusEnum;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.NumberUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * 医院CRM->订单&用户->收款订单
 * 操作:手动退款
 * @author huifang
 *
 */
public class ManuallyRefundPaymentOrderTest extends CrmBase{

	@Test(description = "付款订单手动退款=0",groups = {"qa"})
	public void test_01_paymentOrder_manualRefund_refundO() throws SqlException {
		String remarks = "部分退款=0";
		int refundPrice = 0;
		int hosptialId = defhospital.getId();
		int managerId = defaccountId;
		//提取可撤销的订单编号
		String orderNum = null;
		//医院内部未结算且已支付的付款订单列表
		String requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 2  and " +
				"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+ " order by o.id desc";
		log.info(requestSql);
		List<PaymentOrder> notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
		if(notConfirmPaylist.size()>0){
			orderNum = notConfirmPaylist.get(0).getOrderNum();
		}else{
			log.info("通过修改数据来获取已支付的付款订单");
			requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 0  and " +
					"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() + " order by o.id desc";
			notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
			if(notConfirmPaylist.size()>0){
				PaymentOrder paymentOrder = notConfirmPaylist.get(0);
				orderNum = paymentOrder.getOrderNum();
				PayChecker.updatePaymentOrderPaySuccess(orderNum);
			}else{
				log.error("没有已支付的付款订单，请手动创建.....");
				return;
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("refOrderNum" ,orderNum);
		jo.put("refOrderType", PayConstants.OrderType.PaymentOrder);
		jo.put("tradeRemark",remarks);
		jo.put("amount",refundPrice);
		HttpResult response = httpclient.post(Payment_ManaualRefund, JSON.toJSONString(jo));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		boolean result = JsonPath.read(body,"$.result");
		Assert.assertTrue(result);
		if(checkdb){
			//1.付款订单
			PaymentOrder paymentOrder = OrderChecker.getPaymentOrderInfo(orderNum);
			Assert.assertEquals(paymentOrder.getStatus().intValue(), PaymentOrderStatusEnum.PAYSUCCESS.getCode());//还是已支付状态
			//2.退款申请表无数据
			String sql = "select * from tb_order_refund_apply where order_num = "+orderNum ;
			List<Map<String,Object>> refundList = DBMapper.query(sql);
			Assert.assertEquals(refundList.size(),0);
			//3.交易记录表
			List<TradeOrder> tradeOrders =  PayChecker.getTradeOrderByOrderNum(orderNum, PayConstants.TradeType.refund);
			Assert.assertEquals(tradeOrders.size(),0);
			//4.流转日志表
			List<TradeCommonLogResultDTO>  tradeCommonLogResultDTOS = SettleChecker.getTradeCommonLogList(paymentOrder.getId()+"", LogTypeEnum.LOG_TYPE_REFUND.getValue(),null);
			Assert.assertEquals(tradeCommonLogResultDTOS.size(),0);
		}
	}

	@Test(description = "付款订单手动退款>0(支持退全部款/部分退款2种情况)",groups = {"qa"})
	public void test_02_paymentOrder_manualRefund_refundPlus() throws Exception {
		boolean isPartRefund = true;
		String remarks = "部分退款>0";
		int hosptialId = defhospital.getId();
		int managerId = defaccountId;
		int orderPrice = 0;
		int partRefundPrice = 1;
		//提取可撤销的订单编号
		String orderNum = null;
		//医院内部未结算且已支付的付款订单列表
		String requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 2  and " +
				"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode();
		List<PaymentOrder> notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
		if(notConfirmPaylist.size()>0){
			PaymentOrder paymentOrder = notConfirmPaylist.get(0);
			orderNum = paymentOrder.getOrderNum();
			orderPrice = paymentOrder.getAmount().intValue();
		}else{
			log.info("通过修改数据来获取已支付的付款订单");
			requestSql = "select o.* from tb_payment_order o ,tb_payment_order_settlement s where o.order_num = s.order_num  and o.status = 0  and " +
					"o.organization_id = "+hosptialId+" and s.hospital_settlement_status = "+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode();
			notConfirmPaylist =  OrderChecker.getPaymentOrderListBySql(requestSql);
			if(notConfirmPaylist.size()>0){
				PaymentOrder paymentOrder = notConfirmPaylist.get(0);
				orderNum = paymentOrder.getOrderNum();
				orderPrice = paymentOrder.getAmount().intValue();
                PayChecker.updatePaymentOrderPaySuccess(orderNum);
			}else{
				log.error("没有未结算且已支付的付款订单，请手动创建.....");
				return;
			}
		}
		JSONObject jo = new JSONObject();
		jo.put("refOrderNum" ,orderNum);
		jo.put("refOrderType", PayConstants.OrderType.PaymentOrder);
		if(orderPrice>1){
			jo.put("amount",partRefundPrice);
			remarks += "退部分";
		}
		else{
			jo.put("amount",orderPrice);
			remarks += "退全部";
			isPartRefund = false;
		}
		jo.put("tradeRemark",remarks);

		String beforeTime = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
		waitto(1);
		HttpResult response = httpclient.post(Payment_ManaualRefund, JSON.toJSONString(jo));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		System.out.println(body);
		boolean result = JsonPath.read(body,"$.result");
		Assert.assertTrue(result);
		if(checkdb){
			//1.付款订单
			PaymentOrder paymentOrder = OrderChecker.getPaymentOrderInfo(orderNum);
			if(!isPartRefund)
				Assert.assertEquals(paymentOrder.getStatus().intValue(), PaymentOrderStatusEnum.REVOKE.getCode());
			else
				Assert.assertEquals(paymentOrder.getStatus().intValue(), PaymentOrderStatusEnum.PARTREFUND.getCode());
			//2.付款结算关系表
			List<PaymentOrderSettlementDO> paymentOrderSettlementDOS = SettleChecker.getPaymentOrderSettleByColumn("order_num","'"+orderNum+"'");
			Assert.assertEquals(paymentOrderSettlementDOS.size(),1);
			PaymentOrderSettlementDO paymentOrderSettlementDO = paymentOrderSettlementDOS.get(0);
			if(!isPartRefund)
				Assert.assertEquals(paymentOrderSettlementDO.getRefundSettlement(),ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode());
			else
				Assert.assertEquals(paymentOrderSettlementDO.getRefundSettlement(),ExamOrderRefundSettleEnum.NEED_REFUND.getCode());
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
			if(!isPartRefund)
				Assert.assertEquals(tradeOrder.getSuccAmount(),paymentOrder.getAmount());
			else
				Assert.assertEquals(tradeOrder.getSuccAmount().intValue(),partRefundPrice);
			Assert.assertEquals(tradeOrder.getTradeStatus().intValue(),PayConstants.TradeStatus.Successful);
			//5.交易退款记录表
			List<TradeRefundRecord> tradeRefundRecords = PayChecker.getTradeRefundRecordByOrderNum(orderNum,tradeOrder.getTradeOrderNum(),PayConstants.OrderType.PaymentOrder);
			Assert.assertEquals(tradeRefundRecords.size(),1);
			TradeRefundRecord tradeRefundRecord = tradeRefundRecords.get(0);
			Assert.assertTrue(tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Alipay)
					||tradeRefundRecord.getTradeMethodType().equals(PayConstants.PayMethod.Wxpay));//trade_method_type 退款方式（支付宝/微信）
			Assert.assertEquals(tradeRefundRecord.getRefundStatus(), RefundConstants.RefundStatus.REFUNDING);//refund_status=2
			if(!isPartRefund)
				Assert.assertEquals(tradeRefundRecord.getRefundAmount(),paymentOrder.getAmount());
			else
				Assert.assertEquals(tradeRefundRecord.getRefundAmount().intValue(),partRefundPrice);
			Assert.assertNull(tradeRefundRecord.getReceiveTradeSubAccountType());//三方付款账号
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
			if(!isPartRefund)
				Assert.assertEquals(detail.getChangeAmount(),paymentOrder.getAmount());
			else
				Assert.assertEquals(detail.getChangeAmount().intValue(),partRefundPrice);
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
			List<TradeCommonLogResultDTO>  tradeCommonLogResultDTOS = SettleChecker.getTradeCommonLogList(paymentOrder.getId()+"",LogTypeEnum.LOG_TYPE_REFUND.getValue(),null);
			TradeCommonLogResultDTO common = tradeCommonLogResultDTOS.get(0);
			Assert.assertEquals(common.getOperatorType().intValue(),1);//CRM操作员
			Assert.assertEquals(common.getOperatorId().intValue(),managerId);
			if(!isPartRefund)
				Assert.assertEquals(common.getOperation(),"撤销"+" 备注："+remarks);
			else
				Assert.assertEquals(common.getOperation(),"退款:"+ NumberUtil.IntegerDivision(partRefundPrice,100) +" 备注："+remarks);

			Assert.assertEquals(common.getOperatorName(), AccountChecker.getAccountById(managerId).getName());
			Assert.assertEquals(common.getGmtModified().compareTo(simplehms.parse(beforeTime)),1);
		}
	}

}
