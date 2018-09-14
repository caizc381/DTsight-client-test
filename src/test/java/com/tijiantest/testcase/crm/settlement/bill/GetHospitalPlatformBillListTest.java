package com.tijiantest.testcase.crm.settlement.bill;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * 获取医院平台账单列表
 * 只展示已确认的医院平台账单
 * @author huifang
 * @param 
 *
 */
public class GetHospitalPlatformBillListTest extends SettleBase{

	@Test(description = "获取医院平台账单列表",groups = {"qa"},dataProvider = "platBill")
	public void test_01_getHospitalPlatFormBillList(String ...args){
		String companyStr = args[2];
		int companyId = -1;
		int hospitalId = defSettHospitalId;
		String hospitalName = defSettHospital.getName();

		if(!IsArgsNull(companyStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			}
		JSONObject jo = new JSONObject();
		if(companyId == -1)
			jo.put("companyId","");
		else 
			jo.put("companyId", companyId+"");
		jo.put("organizationId", hospitalId+"");
		HttpResult response = httpclient.post(GetHospitalPlatformBillList, jo.toJSONString());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		//定义部分变量
		int organizationId = -1;
		String organizationName = null;
		List<TradeHospitalPlatformBill> platList = new ArrayList<TradeHospitalPlatformBill>();
		long totalPayment = 0l;
		long payableTotalAmount = 0l;
		long totalConsumeQuotaAmount = 0l;
		long totalDiscountAmount = 0l;
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			organizationId = Integer.parseInt(JsonPath.read(body, "$.organizationId").toString());
			organizationName = JsonPath.read(body,"$.organizationName").toString();
			totalPayment = Long.parseLong(JsonPath.read(body,"$.totalPayment").toString());
			payableTotalAmount = Long.parseLong(JsonPath.read(body,"$.payableTotalAmount").toString());
			totalConsumeQuotaAmount = Long.parseLong(JsonPath.read(body,"$.totalConsumeQuotaAmount").toString());
			totalDiscountAmount = Long.parseLong(JsonPath.read(body,"$.totalDiscountAmount").toString());
			String tradeHospitalPlatformBillList = JsonPath.read(body,"$.tradeHospitalPlatformBillList").toString();
			platList = JSONArray.parseArray(tradeHospitalPlatformBillList, TradeHospitalPlatformBill.class);
			Assert.assertEquals(organizationId,hospitalId);
			Assert.assertEquals(organizationName,hospitalName);
		}
	
		if(checkdb){
			List<TradeHospitalPlatformBill> dbPlatBills = null;
			if(companyId == -1){
					
				String sql = "select * from tb_trade_hospital_platform_bill where hospital_id = "+hospitalId + " and  settlement_view_type = 0 "
						+ "and status  in  ("+HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode()
						+","+ HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode()
						+","+ HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode()
						+") "
								+ " order by gmt_created desc";
				dbPlatBills = SettleChecker.getTradeHospitalPlatformBillByColumn(sql);
				}
			else {
				String sql = "select * from tb_trade_hospital_platform_bill where hospital_id = "+hospitalId + " and  settlement_view_type=0  "
						+ "and status  in ("+HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode()
						+","+ HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode()
						+","+ HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode()
						+") "
								+ " and company_id = "+companyId
								+ " order by gmt_created desc";
				dbPlatBills = SettleChecker.getTradeHospitalPlatformBillByColumn(sql);
			}
			if(!body.equals("{}") && !body.equals(""))
				Assert.assertEquals(platList.size(),dbPlatBills.size());
			else
				Assert.assertEquals(0,dbPlatBills.size());
			long dbNeedPayment = 0;
			long dbActualPayment = 0;
			long dbtotalConsumeQuotaAmount = 0;
			long dbtotalDiscountAmount = 0;
			//所有的平台记录验证
			for(int i=0 ; i<dbPlatBills.size();i++){
				Assert.assertEquals(platList.get(i).getId(),dbPlatBills.get(i).getId());
				Assert.assertEquals(platList.get(i).getBatchSn(),dbPlatBills.get(i).getBatchSn());
				Assert.assertEquals(platList.get(i).getCompanyId(),dbPlatBills.get(i).getCompanyId());
				Assert.assertEquals(platList.get(i).getCompanyName(),dbPlatBills.get(i).getCompanyName());
				Assert.assertEquals(platList.get(i).getCompanyType(),dbPlatBills.get(i).getCompanyType());
				Assert.assertEquals(platList.get(i).getGmtCreated(),dbPlatBills.get(i).getGmtCreated());
				Assert.assertEquals(platList.get(i).getGmtModified(),dbPlatBills.get(i).getGmtModified());
				Assert.assertEquals(platList.get(i).getHospitalId(),dbPlatBills.get(i).getHospitalId());
				Assert.assertEquals(platList.get(i).getIsDeleted(),dbPlatBills.get(i).getIsDeleted());
				Assert.assertEquals(platList.get(i).getOperatorId(),dbPlatBills.get(i).getOperatorId());	
				if(platList.get(i).getPlatformActurallyPayAmount() !=null )
					Assert.assertEquals(platList.get(i).getPlatformActurallyPayAmount(),dbPlatBills.get(i).getPlatformActurallyPayAmount());
				Assert.assertEquals(platList.get(i).getPlatformChargedAmount(),dbPlatBills.get(i).getPlatformChargedAmount());
				Assert.assertEquals(platList.get(i).getPlatformDiscount(),dbPlatBills.get(i).getPlatformDiscount());
				Assert.assertEquals(platList.get(i).getPlatformPayAmount(),dbPlatBills.get(i).getPlatformPayAmount());
				Assert.assertEquals(platList.get(i).getPlatformPrepaymentAmount(),dbPlatBills.get(i).getPlatformPrepaymentAmount());
				Assert.assertEquals(platList.get(i).getPlatformRefundAmount(),dbPlatBills.get(i).getPlatformRefundAmount());
				Assert.assertEquals(platList.get(i).getSn(),dbPlatBills.get(i).getSn());
				Assert.assertEquals(platList.get(i).getStatus(),dbPlatBills.get(i).getStatus());
				Assert.assertEquals(platList.get(i).getRemark(),dbPlatBills.get(i).getRemark());
				Assert.assertEquals(platList.get(i).getDiscountAmount(),dbPlatBills.get(i).getDiscountAmount());//折后应付
				Assert.assertEquals(platList.get(i).getConsumeQuotaAmount(),dbPlatBills.get(i).getConsumeQuotaAmount());//消费额度
				//平台账单流转日志
				log.info("sn..."+dbPlatBills.get(i).getSn());
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbPlatBills.get(i).getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),null);//获取结算平台账单审核
				List<TradeCommonLogResultDTO> retCommonLogList = platList.get(i).getCirculationLog();
				Assert.assertEquals(retCommonLogList.size(),dbCommonLogList.size());
				for(int k=0;k<dbCommonLogList.size();k++){
					Assert.assertEquals(retCommonLogList.get(k).getOperation(),dbCommonLogList.get(k).getOperation());
					Assert.assertEquals(retCommonLogList.get(k).getOperatorName(),dbCommonLogList.get(k).getOperatorName());
					Assert.assertEquals(retCommonLogList.get(k).getRefSn(),dbCommonLogList.get(k).getRefSn());
					Assert.assertEquals(retCommonLogList.get(k).getLogType(),dbCommonLogList.get(k).getLogType());
					Assert.assertEquals(retCommonLogList.get(k).getGmtCreated().getTime(),dbCommonLogList.get(k).getGmtCreated().getTime());
				}
				dbNeedPayment += dbPlatBills.get(i).getPlatformChargedAmount().longValue();
				dbActualPayment += dbPlatBills.get(i).getPlatformActurallyPayAmount().longValue();
				if(dbPlatBills.get(i).getConsumeQuotaAmount()!=null)
					dbtotalConsumeQuotaAmount += dbPlatBills.get(i).getConsumeQuotaAmount();
				if(dbPlatBills.get(i).getDiscountAmount()!=null)
					dbtotalDiscountAmount += dbPlatBills.get(i).getDiscountAmount();
				
			}
			Assert.assertEquals(totalPayment,dbActualPayment);//实收金额验证
			Assert.assertEquals(payableTotalAmount,dbNeedPayment);//应收金额验证
			Assert.assertEquals(totalConsumeQuotaAmount,dbtotalConsumeQuotaAmount);//总的消费额度合计
			Assert.assertEquals(totalDiscountAmount,dbtotalDiscountAmount);//总的折后应付合计
		}
	}
	
	  @DataProvider
	  public Iterator<String[]> platBill(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/platBill.csv",18);
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
