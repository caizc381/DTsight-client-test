package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.settlement.FinancePaymentDto;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * OPS->付款账单
 * 
 * 付款按钮
 * @author huifang
 *
 */
public class AddSettlementPayRecordTest extends OpsBase{

	private static String platBillSn = null;
	@Test(description = "财务付款" , groups = {"qa"},dataProvider = "ops_addSettlementPayRecord",dependsOnGroups="crm_auditHospitalPlatformBill",ignoreMissingDependencies = true)
	public void test_01_addSettlementPayRecord(String ...args) throws ParseException{
		String hospitalStr = args[1];
		String imageUrl = args[2];
		String remarkStr = args[3];
		int hospitalId = -1;
		String remark = null;
		String imageCertifac = null;
		
		FinancePaymentDto dto = new FinancePaymentDto();
		if(!IsArgsNull(hospitalStr)){
			hospitalId = defSettHospitalId;
			dto.setOrganizationId(hospitalId);
		}

		List<TradeHospitalPlatformBill> hp = SettleChecker.getTradeHospitalPlatformBillByColumn(defSettHospitalId,HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode());
		if(hp.size()==0){
			log.error("医院id="+defSettHospitalId+"没有已经审核完毕的平台账单");
			return;
		}else
			platBillSn = hp.get(0).getSn();
		dto.setSnList(Arrays.asList(platBillSn));
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remarkStr);
			}
		if(!IsArgsNull(imageUrl)){
			imageCertifac = imageUrl;
			dto.setImageUrl(imageCertifac);
			}
	
	
		HttpResult response = httpclient.post(Flag.OPS,OPS_AddSettlementPayRecord, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(),"");
		
		if(checkdb){
			log.info("hospitalId"+hospitalId + "sn"+platBillSn);
			//check 平台账单状态变为打款完毕
			List<TradeHospitalPlatformBill> platBillList = SettleChecker.getTradeHospitalPlatformBillByColumn("sn","'"+platBillSn+"'");
			TradeHospitalPlatformBill platBill = platBillList.get(0);
			Assert.assertEquals(platBill.getStatus(),HospitalPlatformBillStatusEnum.PLATFORM_COMPLETE_PAYMENT.getCode());
			String paymentRecordSn = platBill.getPaymentRecordSn();
			//check 付款记录
			String sql = "select * from tb_trade_settlement_payment_record where is_deleted = 0 and organization_id = "+hospitalId
				+ " and is_deleted = 0 and sn='"+paymentRecordSn+"'";
			log.info("sql.."+sql);
			List<TradeSettlementPayRecord> dbList = SettleChecker.getTradeSettlePaymentRecordBySql(sql);
			Assert.assertEquals(dbList.size(),1);
			TradeSettlementPayRecord payRecord = dbList.get(0);
				
				Assert.assertNull(payRecord.getCompanyId());
				Assert.assertEquals(payRecord.getCompanyName(),"平台");
				Assert.assertEquals(payRecord.getOperatorId(), platBill.getOperatorId());
				Assert.assertEquals(payRecord.getOperatorName(),AccountChecker.getOpsAccount(payRecord.getOperatorId()).getName());
				Assert.assertEquals(payRecord.getOrganizationId(), platBill.getHospitalId());
				Assert.assertEquals(payRecord.getOrganizationName(), HospitalChecker.getHospitalById(payRecord.getOrganizationId()).getName());
				Assert.assertEquals(payRecord.getPayableAmount(), platBill.getPlatformChargedAmount());//应收金额
				Assert.assertEquals(payRecord.getRealAmount(), platBill.getPlatformActurallyPayAmount()); //实收金额
			    if(payRecord.getTotalConsumeQuotaAmount().intValue()==0)
			    	Assert.assertTrue(platBill.getConsumeQuotaAmount() == null ||platBill.getConsumeQuotaAmount().intValue() == 0);
			    else
					Assert.assertEquals(payRecord.getTotalConsumeQuotaAmount(),platBill.getConsumeQuotaAmount());//消费额度
			    if(payRecord.getTotalDiscountAmount().intValue() == 0)
			    	Assert.assertTrue(platBill.getDiscountAmount() == null || platBill.getDiscountAmount().intValue() ==0);
			    else
					Assert.assertEquals(payRecord.getTotalDiscountAmount(),platBill.getDiscountAmount());//实际折扣
				Assert.assertEquals(sdf.format(payRecord.getPaymentTime()), sdf.format(new Date())); //打款时间
				Assert.assertEquals(payRecord.getSn(), platBill.getPaymentRecordSn()); //流水账单号
				Assert.assertEquals(sdf.format(payRecord.getGmtCreated()), sdf.format(new Date())); 
				Assert.assertEquals(sdf.format(payRecord.getGmtModified()),sdf.format(new Date())); 

				Assert.assertEquals(payRecord.getType().intValue(),1); //平台收款
				if(imageCertifac!=null )
					Assert.assertEquals(payRecord.getCertificate(), imageCertifac);//凭证
				if(remark !=null )
				Assert.assertEquals(payRecord.getRemark(), remark);//付款记录备注
				//check流转日志
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platBillSn, LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),"desc");//获取结算平台账单审核
				Account account = AccountChecker.getOpsAccount(defusername);
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
				Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台付款");
				Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),platBillSn);
				Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),1);
				Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));
		}
				
	}
	
	  @DataProvider
	  public Iterator<String[]> ops_addSettlementPayRecord(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_addSettlementPayRecord.csv",18);
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