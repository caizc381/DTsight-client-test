package com.tijiantest.testcase.crm.settlement.sett;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.PayChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 获取结算的退款数量
 * 支持2种场景；
 * 场景1：执行结算，选择所有体检订单/收款订单，支持体检订单截至时间涮选,查看退款数量/特殊退款数量
 * 场景2：修改结算时，不修改任何订单，直接查看最后的退款数量/特殊退款数量
 * 场景3：修改结算时，增加所有订单，直接查看最后的退款数量/特殊退款数量
 * 场景4：修改结算时，提供体检订单截至时间，直接查看最后的退款数量/特殊退款数量
 * @author huifang
 *
 */
public class GetUnsettlementRefundNumTest extends SettleBase{

	@Test(description = "获取选择的结算订单的退款数量",groups = {"qa"},dataProvider = "listUnSettlementRefunds")
	public void test_01_listUnSettlementRefunds(String ...args) {
		String batchSnStr = args[1];
		String placeOrderEndTimeStr = args[2];
		String batchSn = null;
		String opt = args[3];//操作，是否有增加订单，删除订单
		String companyStr = args[4];
		String placeOrderEndTime = null;
		int companyid = Integer.parseInt(companyStr);
		List<String> orderNumList = new ArrayList<String>();
		List<String> payOrderNumList = new ArrayList<>();

		UnsettlementRefundNumDTO dto = new UnsettlementRefundNumDTO();
		if (companyid == 0) {
			log.error("没有可结算的单位,无法列出未结算的退款数量");
			return;
		}
		int hospitalId = defSettHospitalId;
		dto.setHospitalCompanyIds(Arrays.asList(companyid));
		dto.setHospitalId(hospitalId);

		if(!IsArgsNull(batchSnStr)){//修改批次进入  //场景2
			//随意取单位的1个待确认的批次号
			List<TradeSettlementBatch> batchList = SettleChecker.getTradeSettlementBatch(defSettHospitalId,companyid,null,null,SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,"1");
			if(batchList!=null && batchList.size()>0){
				batchSn = batchList.get(0).getSn();
				dto.setBatchSn(batchSn);
				//提取批次内的订单列表
				List<TradeSettlementOrder> settlementOrders = SettleChecker.getTradeSettleOrderByColumn("batch_sn",batchSn,"is_deleted","0","hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
				List<TradeSettlementPaymentOrder> settlementPaymentOrders = SettleChecker.getTradeSettlePaymentOrderByColumn("batch_sn",batchSn,"is_deleted","0","hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");

				if(!IsArgsNull(opt)){
					//场景3：增加所有未结算的订单
					if(opt.equals("ADDALL")){
						List<UnsettlementOrder> orderList = SettleChecker.getNotSettlementOrder(hospitalId, settle_time, companyid);
						List<UnsettlementPaymentOrder> paymentOrderList = SettleChecker.getNotSettlementPaymentOrder(hospitalId, settle_time, companyid);
						for (UnsettlementOrder o : orderList)
							orderNumList.add(o.getOrderNum());
						for(UnsettlementPaymentOrder paymentOrder : paymentOrderList)
							payOrderNumList.add(paymentOrder.getOrderNum());
					}
				}
				for (TradeSettlementOrder o : settlementOrders)
					orderNumList.add(o.getRefOrderNum());
				for(TradeSettlementPaymentOrder paymentOrder : settlementPaymentOrders)
					payOrderNumList.add(paymentOrder.getRefOrderNum());

			}else{
				log.error("没有待确认的批次号，无法修改批次并进入未结算退款页面");
				return;
			}
		}else{//执行批次进入（第一次执行结算) 场景1
			List<UnsettlementOrder> orderList = SettleChecker.getNotSettlementOrder(hospitalId, settle_time, companyid);
			List<UnsettlementPaymentOrder> paymentOrderList = SettleChecker.getNotSettlementPaymentOrder(hospitalId, settle_time, companyid);
			for (UnsettlementOrder o : orderList)
				orderNumList.add(o.getOrderNum());
			for(UnsettlementPaymentOrder paymentOrder : paymentOrderList)
				payOrderNumList.add(paymentOrder.getOrderNum());

		}

		if (orderNumList != null && orderNumList.size()>0)
			dto.setOrderNums(orderNumList);
		else
			dto.setOrderNums(new ArrayList<>());
		if(payOrderNumList!=null&& payOrderNumList.size()>0)
			dto.setPayOrderNums(payOrderNumList);
		else
			dto.setPayOrderNums(new ArrayList<>());
		if (!IsArgsNull(placeOrderEndTimeStr)) {
			placeOrderEndTime = placeOrderEndTimeStr;
			dto.setPlaceOrderEndTime(placeOrderEndTime);
		}
		HttpResult response = httpclient.post(GetUnsettlementRefundNum, JSON.toJSONString(dto));
		String body = response.getBody();
		System.out.println(body);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		int unsettlementRefundNum = JsonPath.read(body, "$.unsettlementRefundNum");
		int unsettlementRepayNum = JsonPath.read(body, "$.unsettlementRepayNum");

		if (checkdb) {
			int dbSum = 0;
			if(batchSn == null){//初次执行结算
				//1.未结算体检订单（勾选的体检订单中部分退款的订单个数 + 已结算体检订单未结算退款数，不受体检时间限制）
				//体检订单退款
				dbSum += SettleChecker.getNotSettlementRefundOrderNumsByOrderNumList(orderNumList,hospitalId,settle_time,companyid);
				//2.未结算收款订单（勾选的收款订单中部分退款的订单个数 + 已结算收款订单未结算退款数，不受体检时间限制）
				//收款订单退款
				dbSum += SettleChecker.getNotSettlementRefundPaymentOrderNumsByOrderNumList(payOrderNumList,hospitalId,settle_time,companyid);
				Assert.assertEquals(unsettlementRefundNum,dbSum);

				//3.特殊退款比较
				String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
				String sql = "select * from tb_trade_prepayments_record where is_deleted = 0 "
						+ " and company_id = "+companyid
						+ " and organization_id = "+hospitalId + " and gmt_created > '"+settle_time+"' and status in "+statusList  + " and settlement_view_type = 0 ";
				if(placeOrderEndTime!=null)
					sql += " and  payment_time <= '"+placeOrderEndTime+"'";
				List<TradePrepaymentRecord>  prepaymentRecords = SettleChecker.getTradePrepaymentRecordBySql(sql);
				Assert.assertEquals(unsettlementRepayNum,prepaymentRecords.size());
			}else{//修改结算
				if(!IsArgsNull(opt)){//增加所有订单
					//1.1 批次内的结算退款
					List<TradeSettlementRefund> batchOrderRefundLists = SettleChecker.getTradeSettleRefundByColumn("batch_sn",batchSn);
					//1.2 其他已确认批次的未结算体检订单退款数(约束条件；1.结算开始时间，2 订单已经结算&退款未结算，3.医院&单位)
					List<ExamOrderSettlementDO>  examOrderSetts =
							SettleChecker.getExamOrderSettleBySql("select * from tb_exam_order_settlement where  hospital_company_id = "+companyid + " and " +
									"hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+" and refund_settlement = "+ExamOrderRefundSettleEnum.NEED_REFUND.getCode()
									+" and order_num in (select ref_order_num from tb_trade_refund_record where gmt_modified >=  '"+settle_time+"')");

					//1.3 其他已确认批次的未结算收款退款数(约束条件；1.结算开始时间，2 订单已经结算&退款未结算，3.医院&单位)
					List<PaymentOrderSettlementDO>  paymentOrderSetts =
							SettleChecker.getPaymentOrderSettleBySql("select * from tb_payment_order_settlement where organization_id = "+defSettHospitalId + " and hospital_company_id = "+companyid + " 									and " +
									"hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+" and refund_settlement = "+ExamOrderRefundSettleEnum.NEED_REFUND.getCode()
									+" and order_num in (select ref_order_num from tb_trade_refund_record where gmt_modified >=  '"+settle_time+"')");
					//1.4 增加的未结算体检订单的需要结算的退款
					int notSettleOrderNums = 0;
					int notSettlePaymentOrderNums = 0;
					if(orderNumList!=null && orderNumList.size() > 0)
						notSettleOrderNums = SettleChecker.getNotSettlementRefundOrderNumsByOrderNumList(orderNumList,hospitalId,settle_time,companyid);
					//1.5 增加的未结算收款订单需要结算的退款
					if(payOrderNumList!=null && payOrderNumList.size()>0)
					  notSettlePaymentOrderNums = SettleChecker.getNotSettlementRefundPaymentOrderNumsByOrderNumList(payOrderNumList,hospitalId,settle_time,companyid);
					//结算退款数量需实时计算，既要包括已经有批次的退款，也要包括还需要结算的退款
					Assert.assertEquals(unsettlementRefundNum,batchOrderRefundLists.size()+examOrderSetts.size()+paymentOrderSetts.size() + notSettleOrderNums + notSettlePaymentOrderNums);
				}else{
					//1.退款
					//1.1 批次内的结算退款
					List<TradeSettlementRefund> batchOrderRefundLists = SettleChecker.getTradeSettleRefundByColumn("batch_sn",batchSn);
					//1.2 其他已确认批次的未结算体检订单退款数(约束条件；1.结算开始时间，2 订单已经结算&退款未结算，3.医院&单位)
					List<ExamOrderSettlementDO>  examOrderSetts =
							SettleChecker.getExamOrderSettleBySql("select * from tb_exam_order_settlement where  hospital_company_id = "+companyid + " and " +
									"hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+" and refund_settlement = "+ExamOrderRefundSettleEnum.NEED_REFUND.getCode()
									+" and order_num in (select ref_order_num from tb_trade_refund_record where gmt_modified >=  '"+settle_time+"')");

					//1.3 其他已确认批次的未结算收款退款数(约束条件；1.结算开始时间，2 订单已经结算&退款未结算，3.医院&单位)
					List<PaymentOrderSettlementDO>  paymentOrderSetts =
							SettleChecker.getPaymentOrderSettleBySql("select * from tb_payment_order_settlement where organization_id = "+defSettHospitalId + " and hospital_company_id = "+companyid + " 									and " +
									"hospital_settlement_status = "+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+" and refund_settlement = "+ExamOrderRefundSettleEnum.NEED_REFUND.getCode()
									+" and order_num in (select ref_order_num from tb_trade_refund_record where gmt_modified >=  '"+settle_time+"')");
					//结算退款数量需实时计算，既要包括已经有批次的退款，也要包括还需要结算的退款
					Assert.assertEquals(unsettlementRefundNum,batchOrderRefundLists.size()+examOrderSetts.size()+paymentOrderSetts.size());
				}

				//2.特殊退款比较
				//2.1 批次内特殊退款数量（时间筛选)
				String sql  = "select * from tb_trade_prepayments_record where batch_sn = '"+batchSn + "'";
				if(placeOrderEndTime!=null)
					sql += " and  payment_time <= '"+placeOrderEndTime+"'";
				List<TradePrepaymentRecord>  batchPrepaymentRecords = SettleChecker.getTradePrepaymentRecordBySql(sql);
				//2.2 批次外需要处理的特殊退款
				String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
				sql = "select * from tb_trade_prepayments_record where is_deleted = 0 "
						+ " and company_id = "+companyid
						+ " and organization_id = "+hospitalId + " and gmt_created > '"+settle_time+"'  and  status in "+statusList + " and settlement_view_type = 0";
				if(placeOrderEndTime!=null)
					sql += " and  payment_time <= '"+placeOrderEndTime+"'";
				List<TradePrepaymentRecord>  prepaymentRecords = SettleChecker.getTradePrepaymentRecordBySql(sql);
				//结算特殊退款实时计算，既要包括已经有批次内的特殊退款，也要包括还需要结算的特殊退款
				Assert.assertEquals(unsettlementRepayNum,prepaymentRecords.size() + batchPrepaymentRecords.size());
			}
		}
	}

	@DataProvider
	public Iterator<String[]> listUnSettlementRefunds(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/listUnSettlementRefunds.csv",18);
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
