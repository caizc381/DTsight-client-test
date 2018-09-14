package com.tijiantest.testcase.crm.settlement.bill;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.HospitalReceiptDTO;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradeHospitalCompanyBill;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * 医院CRM收款（单位账单/医院优惠券账单/医院线上账单/医院线下账单)
 * 位置:维护后台->结算管理->收款账单
 * 确认医院账单
 * @author huifang
 *
 */
public class HospitalReceiptTest extends SettleBase{

	@Test(description = "医院收单位账单/医院线上账单/线下账单/医院优惠券账单",groups = {"qa"},dataProvider = "hospitalReceipt")
	public void test_01_hospitalReceipt(String ...args){
		String totalRevenueAmountStr = args[1];
		String remark = args[2];
		String typeStr = args[3];
		int type = Integer.parseInt(typeStr);
		//params
		HospitalReceiptDTO dto = new HospitalReceiptDTO();
		//param1:organizationId
		int organizationId = defSettHospitalId;
		List<Integer> companylist = SettleChecker.getCompanyListWithCompanyBill(organizationId,type);
		if(companylist == null || companylist.size() ==0){
			log.error("没有type="+typeStr+"的单位账单,需要手动生成账单");
			return;
		}
		//param2:companyId
		int companyId = companylist.get(0);
		List<TradeHospitalCompanyBill> companyBills =  SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false,"company_id",companyId+"","status",SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+"","type",typeStr);
		List<String> snList = new ArrayList<String>();
		//param3:snList
		for(TradeHospitalCompanyBill c :companyBills)
			snList.add(c.getSn());
		//param4:remark
		//param5:totalRevenueAmount
		long totalRevenueAmount = Long.parseLong(totalRevenueAmountStr);
		dto.setCompanyId(companyId);
		dto.setOrganizationId(organizationId);
		dto.setRemark(remark);
		dto.setSnList(snList);
		dto.setTotalRevenueAmount(totalRevenueAmount);
		dto.setType(type);
		
		HttpResult response = httpclient.post(HospitalReceipt, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Assert.assertEquals(body,"{}");
		
		if(checkdb){
			String payment_record_sn = null;
			long dbPayableAmount = 0;
			for(String sn : snList){
				 List<TradeHospitalCompanyBill> dbBill = SettleChecker.getTradeHospitalCompanyBillByColumn(null,false,null, false,"sn",sn,"type",typeStr);
				 Assert.assertEquals(dbBill.size(),1);
				 TradeHospitalCompanyBill cBill = dbBill.get(0);
				 //单位应付累加计算
				 dbPayableAmount += cBill.getCompanyChargedAmount();
				//医院完成收款,状态改变
				 Assert.assertEquals(cBill.getStatus(),SettlementHospitalConfirmEnum.SETTLEMENT_COMPLETE_PAYMENT.getCode());
				 if(payment_record_sn == null)
					 payment_record_sn = cBill.getPaymentRecordSn();
				 Assert.assertEquals(cBill.getPaymentRecordSn(),payment_record_sn);
				
			}
			 //收款记录表
			 List<TradeSettlementPayRecord> paymentRecords = SettleChecker.getTradeSettlePaymentRecordByColumn("sn",payment_record_sn,"type",typeStr);
			 Assert.assertEquals(paymentRecords.size(),1);
			 TradeSettlementPayRecord payRecord = paymentRecords.get(0);
			 Assert.assertEquals(payRecord.getSn(),payment_record_sn);
			 Assert.assertEquals(payRecord.getOrganizationId().intValue(),organizationId);
			 Assert.assertEquals(payRecord.getCompanyId().intValue(),companyId);
			 Assert.assertEquals(payRecord.getOperatorId().intValue(),defSettAccountId);
			 Assert.assertEquals(payRecord.getPayableAmount().longValue(),dbPayableAmount);//应付金额
			 Assert.assertEquals(payRecord.getRealAmount().longValue(),totalRevenueAmount);//实付金额
			 Assert.assertEquals(sdf.format(payRecord.getPaymentTime()),sdf.format(new Date()));
			 Assert.assertEquals(payRecord.getRemark(),remark);
			 Assert.assertEquals(payRecord.getType().intValue(),type);//单位收款
			 Assert.assertEquals(payRecord.getIsDeleted().intValue(),0); //未删除
		}
	}
	
	
	 @DataProvider
	  public Iterator<String[]> hospitalReceipt(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/hospitalReceipt.csv",18);
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
