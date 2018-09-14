package com.tijiantest.testcase.crm.settlement.record;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementPayRecordQueryDTO;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeHospitalCompanyBill;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 * 查询收款记录
 * 位置: CRM->结算管理->收款记录
 * @author huifang
 *
 */
public class GetSettlementPayRecordListTest extends SettleBase{

	@Test(description = "获取收款记录列表" , groups = {"qa"},dataProvider = "payRecord")
	public void test_01_payRecordList(String ...args) throws ParseException{
		String companyIdStr = args[1];
		String startTimeStr = args[3];
		String endTimeStr = args[4];
		String typeStr = args[5];
		String pageSize = args[6];
		int hospitalId = defSettHospitalId;
		int companyId = -1;
		String start_time = null;
		String end_time = null;
		int type = -1;
		SettlementPayRecordQueryDTO dto = new SettlementPayRecordQueryDTO();
		dto.setOrganizationId(hospitalId);
		if(!IsArgsNull(companyIdStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			dto.setCompanyId(companyId);
			}

		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(startTimeStr);
			}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(endTimeStr);
			}	
		if(!IsArgsNull(typeStr)){
			type = Integer.parseInt(typeStr);
			dto.setType(type);
			}
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		
	
		HttpResult response = httpclient.post(GetSettlementPayRecordList, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		log.info(response.getBody());
		String records = JsonPath.read(response.getBody(), "$.records").toString();
		List<TradeSettlementPayRecord> retList = JSON.parseArray(records, TradeSettlementPayRecord.class);
//		log.info("返回结果..."+response.getBody());
		if(checkdb){
			log.info("hospitalId"+hospitalId + "companyId"+companyId+"...start_time"+start_time+"...end_time"+end_time+"...type"+type);
			String sql = "select * from tb_trade_settlement_payment_record where is_deleted = 0 and organization_id = "+hospitalId;
			if(companyId != -1)
				sql += " and company_id = "+companyId;
			if(start_time != null)
				sql += " and gmt_created > '"+start_time+"'";
			if (end_time != null)
				sql += " and gmt_created < '"+end_time+"'";
			if (type != -1)
				sql += " and type ="+type;
			sql += " and is_deleted = 0 order by payment_time desc";
			if(pageSize != null)
				sql += " limit "+pageSize;
			log.info("sql.."+sql);
			List<TradeSettlementPayRecord> dbList = SettleChecker.getTradeSettlePaymentRecordBySql(sql);
			Assert.assertEquals(retList.size(),dbList.size());
			for(int i=0;i<dbList.size();i++){
				if(dbList.get(i).getCompanyId() !=null){
					Assert.assertEquals(retList.get(i).getCompanyId(), dbList.get(i).getCompanyId());
					Assert.assertEquals(retList.get(i).getCompanyName(), dbList.get(i).getCompanyName());
				}
				System.out.println("收款账单流水号..."+retList.get(i).getSn());
				Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());
				Assert.assertEquals(retList.get(i).getOperatorName(), dbList.get(i).getOperatorName());
				Assert.assertEquals(retList.get(i).getOperatorId(), dbList.get(i).getOperatorId());
				Assert.assertEquals(retList.get(i).getOrganizationId(), dbList.get(i).getOrganizationId());
				Assert.assertEquals(retList.get(i).getOrganizationName(), dbList.get(i).getOrganizationName());
				Assert.assertEquals(retList.get(i).getPayableAmount(), dbList.get(i).getPayableAmount());//应收金额
				Assert.assertEquals(retList.get(i).getRealAmount(), dbList.get(i).getRealAmount()); //实收金额
				Assert.assertEquals(retList.get(i).getPaymentTime(), dbList.get(i).getPaymentTime()); //收款时间
				Assert.assertEquals(retList.get(i).getSn(), dbList.get(i).getSn()); //流水账单号
				Assert.assertEquals(retList.get(i).getGmtCreated(), dbList.get(i).getGmtCreated());
				Assert.assertEquals(retList.get(i).getGmtModified(), dbList.get(i).getGmtModified());
				Assert.assertEquals(retList.get(i).getType(), dbList.get(i).getType()); //单位类型
				Assert.assertEquals(retList.get(i).getCertificate(), dbList.get(i).getCertificate());//凭证
				Assert.assertEquals(retList.get(i).getRemark(), dbList.get(i).getRemark());//收款记录备注
				Assert.assertEquals(retList.get(i).getTotalConsumeQuotaAmount(),dbList.get(i).getTotalConsumeQuotaAmount());
				Assert.assertEquals(retList.get(i).getTotalDiscountAmount(),dbList.get(i).getTotalDiscountAmount());
				TradeSettlementPayRecord record = dbList.get(i);
				if(record.getType() == 1){//平台收款
					List<TradeHospitalPlatformBill> platBills  = dbList.get(i).getTradeHospitalPlatformBillList();
					if(platBills.size() == 0)
						Assert.assertNull(retList.get(i).getTradeHospitalPlatformBillList());
					else{
						List<TradeHospitalPlatformBill> retPlatBills = retList.get(i).getTradeHospitalPlatformBillList();
						Assert.assertEquals(retPlatBills.size(), platBills.size());
//						System.out.println("平台账单。。。"+retPlatBills.size());
						for(int s=0;s<platBills.size();s++){
							Assert.assertEquals(retPlatBills.get(s).getBatchSn(),platBills.get(s).getBatchSn());
							Assert.assertEquals(retPlatBills.get(s).getId(),platBills.get(s).getId());
							Assert.assertEquals(retPlatBills.get(s).getSn(),platBills.get(s).getSn());
							Assert.assertEquals(retPlatBills.get(s).getCompanyId(),platBills.get(s).getCompanyId());
							Assert.assertEquals(retPlatBills.get(s).getPaymentRecordSn(),platBills.get(s).getPaymentRecordSn());
							Assert.assertEquals(retPlatBills.get(s).getHospitalId(),platBills.get(s).getHospitalId());
							Assert.assertEquals(retPlatBills.get(s).getPlatformActurallyPayAmount(),platBills.get(s).getPlatformActurallyPayAmount());
							Assert.assertEquals(retPlatBills.get(s).getPlatformChargedAmount(),platBills.get(s).getPlatformChargedAmount());
							Assert.assertEquals(retPlatBills.get(s).getPlatformPayAmount(),platBills.get(s).getPlatformPayAmount());
							Assert.assertEquals(retPlatBills.get(s).getPlatformPrepaymentAmount(),platBills.get(s).getPlatformPrepaymentAmount());
							Assert.assertEquals(retPlatBills.get(s).getPlatformRefundAmount(),platBills.get(s).getPlatformRefundAmount());
							Assert.assertEquals(retPlatBills.get(s).getPlatformDiscount(),platBills.get(s).getPlatformDiscount());
							Assert.assertEquals(retPlatBills.get(s).getStatus(),platBills.get(s).getStatus());
							Assert.assertEquals(retPlatBills.get(s).getOperatorId(),platBills.get(s).getOperatorId());
							Assert.assertEquals(retPlatBills.get(s).getIsDeleted(),platBills.get(s).getIsDeleted());
							Assert.assertEquals(retPlatBills.get(s).getRemark(),platBills.get(s).getRemark());
							Assert.assertEquals(retPlatBills.get(s).getDiscountAmount(),platBills.get(s).getDiscountAmount());
							Assert.assertEquals(retPlatBills.get(s).getConsumeQuotaAmount(),platBills.get(s).getConsumeQuotaAmount());
							//平台账单流转日志
							log.info("sn..."+platBills.get(s).getSn());
							List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(platBills.get(s).getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),null);//获取结算平台账单审核
							List<TradeCommonLogResultDTO> retCommonLogList = retPlatBills.get(s).getCirculationLog();
							Assert.assertEquals(retCommonLogList.size(),dbCommonLogList.size());
							for(int k=0;k<dbCommonLogList.size();k++){
								Assert.assertEquals(retCommonLogList.get(k).getOperation(),dbCommonLogList.get(k).getOperation());
								Assert.assertEquals(retCommonLogList.get(k).getOperatorName(),dbCommonLogList.get(k).getOperatorName());
								Assert.assertEquals(retCommonLogList.get(k).getRefSn(),dbCommonLogList.get(k).getRefSn());
								Assert.assertEquals(retCommonLogList.get(k).getLogType(),dbCommonLogList.get(k).getLogType());
								Assert.assertEquals(retCommonLogList.get(k).getGmtCreated().getTime(),dbCommonLogList.get(k).getGmtCreated().getTime());
							}
						}
					}
				}
					
				if(record.getType() == 0 || record.getType() == 2 || record.getType() == 3 || record.getType() == 4){//单位收款
					List<TradeHospitalCompanyBill> companyBills = dbList.get(i).getTradeHospitalCompanyBillList();
					if(companyBills.size() == 0)
						Assert.assertNull(retList.get(i).getTradeHospitalCompanyBillList());
					else{
						List<TradeHospitalCompanyBill> retCompanyBills = retList.get(i).getTradeHospitalCompanyBillList();
						Assert.assertEquals(retCompanyBills.size(),companyBills.size());
//						System.out.println("单位账单。。。"+companyBills.size());
						for(int s=0;s<companyBills.size();s++){
							Assert.assertEquals(retCompanyBills.get(s).getBatchSn(),companyBills.get(s).getBatchSn());
							Assert.assertEquals(retCompanyBills.get(s).getId(),companyBills.get(s).getId());
							Assert.assertEquals(retCompanyBills.get(s).getCompanyId(),companyBills.get(s).getCompanyId());
							Assert.assertEquals(retCompanyBills.get(s).getPaymentRecordSn(),companyBills.get(s).getPaymentRecordSn());
							Assert.assertEquals(retCompanyBills.get(s).getSn(),companyBills.get(s).getSn());
							Assert.assertEquals(retCompanyBills.get(s).getCompanyChargedAmount(),companyBills.get(s).getCompanyChargedAmount());
							Assert.assertEquals(retCompanyBills.get(s).getCompanyPayAmount(),companyBills.get(s).getCompanyPayAmount());
							Assert.assertEquals(retCompanyBills.get(s).getCompanyRefundAmount(),companyBills.get(s).getCompanyRefundAmount());
							Assert.assertEquals(retCompanyBills.get(s).getStatus(),companyBills.get(s).getStatus());
							Assert.assertEquals(retCompanyBills.get(s).getOperatorId(),companyBills.get(s).getOperatorId());
							Assert.assertEquals(retCompanyBills.get(s).getIsDeleted(),companyBills.get(s).getIsDeleted());
						}
					}
				}

			}
		}
	}
	
	  @DataProvider
	  public Iterator<String[]> payRecord(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/payRecord.csv",18);
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