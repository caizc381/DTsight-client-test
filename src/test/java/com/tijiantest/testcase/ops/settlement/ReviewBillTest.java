package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.settlement.ConsumeQuotaDetailSceneEnum;
import com.tijiantest.model.settlement.ConsumeQuotaDetailStatusEnum;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.TradeReviewBillDTO;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * OPS->付款账单
 * 
 * 运营经理审核账单
 * 
 * @author huifang
 * 
 *
 */
public class ReviewBillTest extends OpsBase{

	public static String platBillSn = null;
	@Test(description = "运营审核平台账单",groups = {"qa"},dataProvider = "reviewBill",dependsOnGroups="crm_confirmBatch",ignoreMissingDependencies = true)
	public void test_01_reviewBill(String ...args){
		String sn = args[1];
		String discountAmountStr = args[2];
		String platformPayAmountStr = args[3];
		String remarkStr = args[4];
		long discountAmount = 0l;
		long platformPayAmount = 0l;
		long consumeQuotaAmount = 0l;
		String remark = null;
		TradeReviewBillDTO dto = new TradeReviewBillDTO();
		if(!IsArgsNull(sn)){
			//捞取医院平台审核中的账单
			List<TradeHospitalPlatformBill> hp = SettleChecker.getTradeHospitalPlatformBillByColumn(defSettHospitalId,HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode());
			if(hp.size()==0){
				log.error("医院id="+defSettHospitalId+"没有可用于审核的平台账单");
				return;
			}
			sn = hp.get(0).getSn();
			platBillSn = sn;
			dto.setSn(sn);
		}	
		if(!IsArgsNull(discountAmountStr)){
			discountAmount = Long.parseLong(discountAmountStr);
			dto.setDiscountAmount(discountAmount);
			}
		if(!IsArgsNull(platformPayAmountStr)){
			platformPayAmount = Long.parseLong(platformPayAmountStr);
			dto.setPlatformActurallyPayAmount(platformPayAmount);
			}
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remark);
		  }
		consumeQuotaAmount = platformPayAmount-discountAmount;
		dto.setConsumeQuotaAmount(consumeQuotaAmount);
		
		HttpResult response = httpclient.post(Flag.OPS,OPS_ReviewBill, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"错误原因:"+response.getBody());
		Assert.assertEquals(response.getBody(),"");
	
		if(checkdb){
			//check平台账单折后应付/平台实收/消费额度金额/备注/状态
		   	List<TradeHospitalPlatformBill> dbPlatBill = SettleChecker.getTradeHospitalPlatformBillByColumn("sn","'"+sn+"'");
		   	Assert.assertEquals(dbPlatBill.size(),1);
		   	TradeHospitalPlatformBill dbPlat = dbPlatBill.get(0);
		   	Assert.assertEquals(dbPlat.getDiscountAmount().longValue(),discountAmount);
		   	Assert.assertEquals(dbPlat.getConsumeQuotaAmount().longValue(),consumeQuotaAmount);
		   	Assert.assertEquals(dbPlat.getPlatformActurallyPayAmount().longValue(),platformPayAmount);
		   	Assert.assertEquals(dbPlat.getStatus(),HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode());
		   	if(remark!=null)
		   		Assert.assertEquals(dbPlat.getRemark(),remark);
		   	//check新增消费额度记录
		   	List<TradeConsumeQuotaDetail> consumeDetails = SettleChecker.getTradeConsumeQuotaDetailByPlatformBillSn(sn);
		   	if(consumeQuotaAmount != 0){//消费额度不为0时
				Assert.assertEquals(consumeDetails.size(),1);
				TradeConsumeQuotaDetail detail = consumeDetails.get(0);
				Assert.assertEquals(detail.getPlatformBillSn(),sn);
				Assert.assertEquals(detail.getOrganizationId(),dbPlat.getHospitalId());
				Assert.assertEquals(detail.getCompanyId(),dbPlat.getCompanyId());
				Assert.assertEquals(detail.getAmount().longValue(),consumeQuotaAmount);
				Assert.assertEquals(sdf.format(detail.getPayTime()),sdf.format(new Date()));
				if(consumeQuotaAmount >0){
					Assert.assertEquals(detail.getScene(),ConsumeQuotaDetailSceneEnum.SETTELMENT_PROFIT.getCode());//结算盈余
					Assert.assertEquals(detail.getStatus(),ConsumeQuotaDetailStatusEnum.HOSPITAL_TO_BE_CONFIRMED.getCode());//未确认
				}else if(consumeQuotaAmount < 0){
					Assert.assertEquals(detail.getScene(),ConsumeQuotaDetailSceneEnum.SETTELMENT_PAY.getCode());//结算支出
					Assert.assertEquals(detail.getStatus(),ConsumeQuotaDetailStatusEnum.FREEZING.getCode());//冻结中
					Assert.assertEquals(detail.getIsDeleted().intValue(),0);
				}
				Assert.assertEquals(detail.getIsDeleted().intValue(),0);
				//新增的消费额度，如果金额最后一位是0，会过滤掉，如2.20直接显示为2.2
				String expectRemark = "";
				DecimalFormat df = new DecimalFormat("0.00");
				String remarkPlatformCharge = df.format((float)(dbPlat.getPlatformChargedAmount().intValue())/100);
				String remarkDiscount =  df.format((float)(dbPlat.getDiscountAmount().intValue())/100);
				String remarkPlatActual  = df.format((float)(dbPlat.getPlatformActurallyPayAmount().intValue())/100);
				char last = remarkPlatformCharge.charAt(remarkPlatformCharge.length()-1);
				if(last == '0')
					remarkPlatformCharge = remarkPlatformCharge.substring(0, remarkPlatformCharge.length()-1);
				last = remarkDiscount.charAt(remarkDiscount.length()-1);
				if(last == '0')
					remarkDiscount = remarkDiscount.substring(0, remarkDiscount.length()-1);
				last = remarkPlatActual.charAt(remarkPlatActual.length()-1);
				if(last == '0')
					remarkPlatActual = remarkPlatActual.substring(0, remarkPlatActual.length()-1);

				expectRemark += "结算批次号:"+dbPlat.getBatchSn()+"/n"+"账单金额: ￥"+remarkPlatformCharge+"/n折后应付: ￥"+remarkDiscount+"/n平台实付: ￥"+remarkPlatActual;
				System.out.println("期望的结算批次号是.."+expectRemark);
				Assert.assertEquals(detail.getRemark(),expectRemark);
				Assert.assertEquals(sdf.format(detail.getGmtCreated()),sdf.format(new Date()));
				Assert.assertEquals(sdf.format(detail.getGmtModified()),sdf.format(new Date()));
				//check流转日志
				Account account = AccountChecker.getOpsAccount(defusername);
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platBillSn,LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),"desc");//获取结算平台账单审核
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
				Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台审核");
				Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),platBillSn);
				Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),1);
				Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

				dbCommonLogList = SettleChecker.getTradeCommonLogList(detail.getSn(),LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),"desc");//获取消费明细
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
				Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台审核");
				Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),detail.getSn());
				Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),2);
				Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));
			}else{//消费额度为0时，消费额度表不插入记录
				Assert.assertEquals(consumeDetails.size(),0);
				//check流转日志
				Account account = AccountChecker.getOpsAccount(defusername);
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platBillSn,LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),"desc");//获取结算平台账单审核
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
				Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
				Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台审核");
				Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),platBillSn);
				Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),1);
				Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

			}




	   }
			
	}
	
	@Test(description="修改审核记录",groups = {"qa","ops_updateReviewBill"},dataProvider="ops_updateReviewBill",dependsOnMethods="test_01_reviewBill")
	public void test_02_reviewBill_update(String ...args){
		waitto(1);
		String sn = args[1];
		String discountAmountStr = args[2];
		String platformPayAmountStr = args[3];
		String remarkStr = args[4];
		long discountAmount = 0l;
		long platformPayAmount = 0l;
		long consumeQuotaAmount = 0l;
		String remark = null;
		TradeReviewBillDTO dto = new TradeReviewBillDTO();
		if(!IsArgsNull(sn)){
			if(platBillSn == null){
				log.error("医院id="+defSettHospitalId+"没有可用于审核的平台账单,无法修改审核记录");
				return;
			}
			sn = platBillSn;
			dto.setSn(sn);
		}	
		if(!IsArgsNull(discountAmountStr)){
			discountAmount = Long.parseLong(discountAmountStr);
			dto.setDiscountAmount(discountAmount);
			}
		if(!IsArgsNull(platformPayAmountStr)){
			platformPayAmount = Long.parseLong(platformPayAmountStr);
			dto.setPlatformActurallyPayAmount(platformPayAmount);
			}
		if(!IsArgsNull(remarkStr)){
			remark = remarkStr;
			dto.setRemark(remark);
		  }
		consumeQuotaAmount = platformPayAmount-discountAmount;
		dto.setConsumeQuotaAmount(consumeQuotaAmount);
		
		HttpResult response = httpclient.post(Flag.OPS,OPS_ReviewBill, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"错误原因:"+response.getBody());
		Assert.assertEquals(response.getBody(),"");
	
		if(checkdb){
			//check平台账单折后应付/平台实收/消费额度金额/备注
		   	List<TradeHospitalPlatformBill> dbPlatBill = SettleChecker.getTradeHospitalPlatformBillByColumn("sn","'"+sn+"'");
		   	Assert.assertEquals(dbPlatBill.size(),1);
		   	TradeHospitalPlatformBill dbPlat = dbPlatBill.get(0);
		   	Assert.assertEquals(dbPlat.getDiscountAmount().longValue(),discountAmount);
		   	Assert.assertEquals(dbPlat.getConsumeQuotaAmount().longValue(),consumeQuotaAmount);
		   	Assert.assertEquals(dbPlat.getPlatformActurallyPayAmount().longValue(),platformPayAmount);
		   	Assert.assertEquals(dbPlat.getStatus(),HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode());

		   	if(remark!=null)
		   		Assert.assertEquals(dbPlat.getRemark(),remark);
		   	//check更新消费额度记录
		   	List<TradeConsumeQuotaDetail> consumeDetails = SettleChecker.getTradeConsumeQuotaDetailByPlatformBillSn(sn);
		   	Assert.assertEquals(consumeDetails.size(),1);
		   	TradeConsumeQuotaDetail detail = consumeDetails.get(0);
		   	Assert.assertEquals(detail.getPlatformBillSn(),sn);
		   	Assert.assertEquals(detail.getOrganizationId(),dbPlat.getHospitalId());
		   	Assert.assertEquals(detail.getCompanyId(),dbPlat.getCompanyId());
		   	Assert.assertEquals(detail.getAmount().longValue(),consumeQuotaAmount);
		   	Assert.assertEquals(sdf.format(detail.getPayTime()),sdf.format(new Date()));
		   	if(consumeQuotaAmount >0){
		   		Assert.assertEquals(detail.getScene(),ConsumeQuotaDetailSceneEnum.SETTELMENT_PROFIT.getCode());//结算盈余
		   		Assert.assertEquals(detail.getStatus(),ConsumeQuotaDetailStatusEnum.HOSPITAL_TO_BE_CONFIRMED.getCode());//未确认
				Assert.assertEquals(detail.getIsDeleted().intValue(),0);
			}else if(consumeQuotaAmount < 0){
		   		Assert.assertEquals(detail.getScene(),ConsumeQuotaDetailSceneEnum.SETTELMENT_PAY.getCode());//结算支出
		   		Assert.assertEquals(detail.getStatus(),ConsumeQuotaDetailStatusEnum.FREEZING.getCode());//冻结中
				Assert.assertEquals(detail.getIsDeleted().intValue(),0);
			}else if(consumeQuotaAmount == 0)//如果修改账单，自动消费额度为0，删除此纪录
				Assert.assertEquals(detail.getIsDeleted().intValue(),1);


		   	//更新消费额度，如果金额最后一位是0，会过滤掉，如2.20直接显示为2.2
		   	String expectRemark = "";
		   	DecimalFormat df = new DecimalFormat("0.00");
		   	String remarkPlatformCharge = df.format((float)(dbPlat.getPlatformChargedAmount().intValue())/100);
		   	String remarkDiscount =  df.format((float)(dbPlat.getDiscountAmount().intValue())/100);
		   	String remarkPlatActual  = df.format((float)(dbPlat.getPlatformActurallyPayAmount().intValue())/100);
		   	char last = remarkPlatformCharge.charAt(remarkPlatformCharge.length()-1);
		   	if(last == '0')
		   		remarkPlatformCharge = remarkPlatformCharge.substring(0, remarkPlatformCharge.length()-1);
		   	last = remarkDiscount.charAt(remarkDiscount.length()-1);
		   	if(last == '0')
		   		remarkDiscount = remarkDiscount.substring(0, remarkDiscount.length()-1);
		   	last = remarkPlatActual.charAt(remarkPlatActual.length()-1);
		   	if(last == '0')
		   		remarkPlatActual = remarkPlatActual.substring(0, remarkPlatActual.length()-1);
		   	
		   	expectRemark += "结算批次号:"+dbPlat.getBatchSn()+"/n"+"账单金额: ￥"+remarkPlatformCharge+"/n折后应付: ￥"+remarkDiscount+"/n平台实付: ￥"+remarkPlatActual;
		   	System.out.println("期望的结算批次号是.."+expectRemark);
		   	Assert.assertEquals(detail.getRemark(),expectRemark);
		   	Assert.assertEquals(sdf.format(detail.getGmtCreated()),sdf.format(new Date()));
		   	Assert.assertEquals(sdf.format(detail.getGmtModified()),sdf.format(new Date()));
		    //check流转日志
			Account account = AccountChecker.getOpsAccount(defusername);
			List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platBillSn,LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue()
					,"desc");//获取结算平台账单审核
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
			Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台审核");
			Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),platBillSn);
			Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),1);
			Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

			dbCommonLogList = SettleChecker.getTradeCommonLogList(detail.getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),"desc");//获取消费明细
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorId(),account.getId());
			Assert.assertEquals(dbCommonLogList.get(0).getOperatorName(),account.getName());
			Assert.assertEquals(dbCommonLogList.get(0).getOperation(),"平台审核");
			Assert.assertEquals(dbCommonLogList.get(0).getRefSn(),detail.getSn());
			Assert.assertEquals(dbCommonLogList.get(0).getLogType().intValue(),2);
			Assert.assertEquals(sdf.format(dbCommonLogList.get(0).getGmtCreated().getTime()),sdf.format(new Date()));

	   }
			
	}
	
	
	  @DataProvider
	  public Iterator<String[]> reviewBill(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_reviewBill.csv",18);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			return null;
		}
	  
	  @DataProvider
	  public Iterator<String[]> ops_updateReviewBill(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_updateReviewBill.csv",18);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			return null;
		}
	  
//	  public static void main(String[] args) {
//		String a = "2.20";
//		char last = a.charAt(a.length()-1);
//		if(last == '0'){
//			a = a.substring(0, a.length()-1);
//		}
//	    System.out.println(a);
//	}
}
