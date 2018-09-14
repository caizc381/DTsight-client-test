package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.settlement.TradePrepaymentRecordVO;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.PrepaymentRecordQueryDTO;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.TradePrepaymentRecord;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 * OPS->特殊退款
 * 
 * 列举所有的特殊退款
 * @author huifang
 *
 */
public class GetPrepaymentRecordListTest extends OpsBase{

	@Test(description = "列举特殊退款" , groups = {"qa"},dataProvider = "prepaymentRecord")
	public void test_01_payRecordList(String ...args) throws ParseException{
		String companyIdStr = args[1];
		String hospitalStr = args[2];
		String startTimeStr = args[3];
		String endTimeStr = args[4];
		String hosptialSettleStr = args[5];
		String channelSettleStr = args[6];
		String pageSize = args[7];
		int hospitalId = -1;
		int companyId = -1;
		String start_time = null;
		String end_time = null;
		int hospitalSett = -1;
		int channelSett = -1;
		PrepaymentRecordQueryDTO dto = new PrepaymentRecordQueryDTO();
		if(!IsArgsNull(hospitalStr)){
			hospitalId = Integer.parseInt(hospitalStr); 
			dto.setOrganizationId(hospitalId);
			if(!IsArgsNull(companyIdStr)){
				companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
				dto.setCompanyIds(Arrays.asList(companyId));
			 }
		}	

		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(sdf.parse(startTimeStr));
			}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(sdf.parse(endTimeStr));
			}	
		if(!IsArgsNull(hosptialSettleStr)){
			hospitalSett = Integer.parseInt(hosptialSettleStr);
			dto.setSettlementStatus(hospitalSett);
			}else
				dto.setSettlementStatus(-1);

		if(!IsArgsNull(channelSettleStr)){
			channelSett = Integer.parseInt(channelSettleStr);
			dto.setChannelSettlementStatus(channelSett);
		}else
			dto.setChannelSettlementStatus(-1);
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			page.setCurrentPage(1);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		
	
		HttpResult response = httpclient.post(Flag.OPS,OPS_GetPrepaymentRecord, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		System.out.println(response.getBody());
		String records = JsonPath.read(response.getBody(), "$.records").toString();
		List<TradePrepaymentRecordVO> retList = JSON.parseArray(records, TradePrepaymentRecordVO.class);
//		log.info("返回结果..."+response.getBody());
		if(checkdb){
			log.info("hospitalId"+hospitalId + "companyId"+companyId+"...start_time"+start_time+"...end_time"+end_time+"...hospitalSettlement"+hospitalSett+"...channelSettlement"+channelSett);
			String sql = "select * from tb_trade_prepayments_record where id >= 0 ";
			if(hospitalId != -1)
				sql += " and organization_id = "+hospitalId;
			if(companyId != -1)
				sql += " and company_id = "+companyId;
			if(start_time != null)
				sql += " and gmt_created > '"+start_time+"'";
			if (end_time != null)
				sql += " and gmt_created < '"+end_time+"'";

			if(channelSett != -1) {//如果渠道结算查询，查询渠道退款记录
				sql += " and settlement_view_type=1 ";
				if (channelSett == 0) {
					String statusList = "(" + SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode() + "," + SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode() + ")";
					sql += " and status in " + statusList + " and is_deleted = 0";
				}
				if (channelSett == 1) {
					String statusList = "(" + SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode() + "," + SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode() + ")";
					sql += " and status in " + statusList + " and is_deleted = 0";
				}
				if (channelSett == 2)
					sql += " and is_deleted = 1 ";
			}
			else{//默认查询医院退款记录
				sql += " and settlement_view_type=0 ";
			}
			if (hospitalSett != -1){
				if(hospitalSett == 0){
					String statusList = "("+SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")";
					sql += " and status in " + statusList+" and is_deleted = 0";
				}
				if(hospitalSett == 1){
					String statusList = "("+SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CONFIRMD.getCode()+")";
					sql += " and status in " + statusList +" and is_deleted = 0";
				}
				if(hospitalSett == 2)
					sql += " and is_deleted = 1 ";
			}
			sql += " order by gmt_created desc ";
			if(pageSize != null)
					sql += " limit "+pageSize;
			log.info("sql.."+sql);

			List<TradePrepaymentRecordVO> dbList = SettleChecker.getTradePrepaymentRecordView(sql);
			Assert.assertEquals(retList.size(),dbList.size());
			for(int i=0;i<dbList.size();i++){
				log.info("index"+(i+1)+"..."+dbList.get(i).getId()+"retList:"+retList.get(i).getId());
				if(dbList.get(i).getCompanyId() !=null){
					Assert.assertEquals(retList.get(i).getCompanyId(), dbList.get(i).getCompanyId());
					Assert.assertEquals(retList.get(i).getCompanyName(), dbList.get(i).getCompanyName());
				}
				Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());
				Assert.assertEquals(retList.get(i).getOperatorName(), dbList.get(i).getOperatorName());
				Assert.assertEquals(retList.get(i).getOperatorId(), dbList.get(i).getOperatorId());
				Assert.assertEquals(retList.get(i).getOrganizationId(), dbList.get(i).getOrganizationId());
				Assert.assertEquals(retList.get(i).getOrganizationName(), dbList.get(i).getOrganizationName());
				Assert.assertEquals(retList.get(i).getRefundOrganizationId(),dbList.get(i).getRefundOrganizationId());
				Assert.assertEquals(retList.get(i).getRefundOrganizationName(),dbList.get(i).getRefundOrganizationName());
				Assert.assertEquals(retList.get(i).getRefundCompanyId(),dbList.get(i).getRefundCompanyId());
				Assert.assertEquals(retList.get(i).getRefundCompanyName(),dbList.get(i).getRefundCompanyName());
				Assert.assertEquals(retList.get(i).getIsPlatformCompany(),dbList.get(i).getIsPlatformCompany());
				Assert.assertEquals(retList.get(i).getChannelPlatformCompany(),dbList.get(i).getChannelPlatformCompany());
				Assert.assertEquals(retList.get(i).getPaymentTime(), dbList.get(i).getPaymentTime()); //退款时间
				Assert.assertEquals(retList.get(i).getGmtCreated(), dbList.get(i).getGmtCreated());
				Assert.assertEquals(retList.get(i).getGmtModified(), dbList.get(i).getGmtModified());
				Assert.assertEquals(retList.get(i).getType(), dbList.get(i).getType()); //类型
				Assert.assertEquals(retList.get(i).getStatus(), dbList.get(i).getStatus()); //状态
				Assert.assertEquals(retList.get(i).getAmount(), dbList.get(i).getAmount()); //金额
				Assert.assertEquals(retList.get(i).getCertificate(), dbList.get(i).getCertificate());//凭证
				Assert.assertEquals(retList.get(i).getRemark(), dbList.get(i).getRemark());//备注
				Assert.assertEquals(retList.get(i).getBatchSn(),dbList.get(i).getBatchSn());//医院结算批次号
				Assert.assertEquals(retList.get(i).getChannelSettlementBatch(),dbList.get(i).getChannelSettlementBatch());//渠道结算批次号
				Assert.assertEquals(retList.get(i).getSettlementStatus(),dbList.get(i).getSettlementStatus());//结算状态
				Assert.assertEquals(retList.get(i).getChannelSettlementStatus(),dbList.get(i).getChannelSettlementStatus());//渠道结算状态



			}
		}
				
	}
	
	  @DataProvider
	  public Iterator<String[]> prepaymentRecord(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_prepaymentRecord.csv",18);
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