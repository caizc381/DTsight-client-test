package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
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
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.HospitalPlatformBillStatusEnum;
import com.tijiantest.model.settlement.HospitalPlatformSummaryBill;
import com.tijiantest.model.settlement.SettlementBatchQueryDTO;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;

/**
 * OPS->结算系统
 * 付款账单
 * @author huifang
 * @param 
 *
 */
public class GetHospitalPlatformSummaryBillListTest extends OpsBase{

	@Test(description = "获取体检机构/地区搜索下的平台付款账单列表",groups = {"qa"},dataProvider = "ops_platSummaryBill")
	public void test_01_getHospitalPlatFormSummaryBillList(String ...args){
		String hosptialStr = args[1];
		String provinceStr = args[2];
		String cityStr = args[3];
		String distrinctStr = args[4];
		int hospitalId = -1;
		int province = -1;
		int city = -1;
		int district = -1;
		SettlementBatchQueryDTO jo = new SettlementBatchQueryDTO();

		if(!IsArgsNull(hosptialStr)){
			hospitalId = Integer.parseInt(hosptialStr);
			jo.setOrganizationId(hospitalId);
			}
		if(!IsArgsNull(provinceStr)){
			province = Integer.parseInt(provinceStr);
			jo.setProvinceId(province);
		}
		if(!IsArgsNull(cityStr)){
			city = Integer.parseInt(cityStr);
			jo.setCityId(city);
		}
		if(!IsArgsNull(distrinctStr)){
			district = Integer.parseInt(distrinctStr);
			jo.setDistrictId(district);
		}
		HttpResult response = httpclient.post(Flag.OPS,OPS_GetHospitalPlatformSummaryBillList, JSON.toJSONString(jo));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		System.out.println(body);
		List<HospitalPlatformSummaryBill> retPlatformSummaryList = null;
		if(! body.equals("")&&!body.equals("[]")){
		//返回医院平台账单列表
		 retPlatformSummaryList = JSON.parseArray(body, HospitalPlatformSummaryBill.class);
		//排序，按照医院Id正序
		 Collections.sort(retPlatformSummaryList, new Comparator<HospitalPlatformSummaryBill>() {
		    	@Override
		    	public int compare(HospitalPlatformSummaryBill o1,
		    			HospitalPlatformSummaryBill o2) {
		    		return o1.getOrganizationId() - o2.getOrganizationId();
		    	}
			});
		 }
		 if(checkdb){
			 List<Integer> dbHospitalList = SettleChecker.getHavePlatFormSummaryHospitalList(province, city, district, hospitalId);
			 log.info(dbHospitalList);
			 //check对比医院总数量
			 if(! body.equals("")&&!body.equals("[]"))
				 Assert.assertEquals(retPlatformSummaryList.size(),dbHospitalList.size());
			 else
				 Assert.assertEquals(dbHospitalList.size(),0);
			 //check 每个医院内部的平台账单内容
			 for(int j=0;j<dbHospitalList.size();j++){
				 List<TradeHospitalPlatformBill> retPlatBills = retPlatformSummaryList.get(j).getTradeHospitalPlatformBillList();
				 //排序，按照医院Id正序
				 Collections.sort(retPlatBills, new Comparator<TradeHospitalPlatformBill>() {
					 @Override
					 public int compare(TradeHospitalPlatformBill o1,
										TradeHospitalPlatformBill o2) {
						 return  o2.getId() - o1.getId();
					 }
				 });

					List<TradeHospitalPlatformBill> dbPlatBills = null;
					String sql = "select * from tb_trade_hospital_platform_bill where hospital_id = "+dbHospitalList.get(j) + "  and settlement_view_type = 0  "
								+ "and status in  ("+HospitalPlatformBillStatusEnum.PLATFORM_TO_BE_CONFIRMD.getCode()
								+","+ HospitalPlatformBillStatusEnum.PLATFORM_CONFIREMD.getCode()
								+","+ HospitalPlatformBillStatusEnum.PLATFORM_CAIWU_TOBE_CONFIRM.getCode()
								+") "
										+ " order by  id desc";
					log.info("平台sql"+sql);
					dbPlatBills = SettleChecker.getTradeHospitalPlatformBillByColumn(sql);	
					//check医院的平台账单数量对比
					Assert.assertEquals(retPlatBills.size(),dbPlatBills.size());
					
					//check平台账单内部(医院内部)
					long dbHospitalNeedPayment = 0;
					long dbHospitalActualPayment = 0;
					long dbHospitaltotalConsumeQuotaAmount = 0;
					long dbHospitaltotalDiscountAmount = 0;
					for(int i=0 ; i<dbPlatBills.size();i++){
						Assert.assertEquals(retPlatBills.get(i).getId(),dbPlatBills.get(i).getId());
						Assert.assertEquals(retPlatBills.get(i).getBatchSn(),dbPlatBills.get(i).getBatchSn());
						Assert.assertEquals(retPlatBills.get(i).getCompanyId(),dbPlatBills.get(i).getCompanyId());
						Assert.assertEquals(retPlatBills.get(i).getCompanyName(),dbPlatBills.get(i).getCompanyName());
						Assert.assertEquals(retPlatBills.get(i).getCompanyType(),dbPlatBills.get(i).getCompanyType());
						Assert.assertEquals(retPlatBills.get(i).getGmtCreated(),dbPlatBills.get(i).getGmtCreated());
						Assert.assertEquals(retPlatBills.get(i).getGmtModified(),dbPlatBills.get(i).getGmtModified());
						Assert.assertEquals(retPlatBills.get(i).getHospitalId(),dbPlatBills.get(i).getHospitalId());
						Assert.assertEquals(retPlatBills.get(i).getIsDeleted(),dbPlatBills.get(i).getIsDeleted());
						Assert.assertEquals(retPlatBills.get(i).getOperatorId(),dbPlatBills.get(i).getOperatorId());	
						if(retPlatBills.get(i).getPlatformActurallyPayAmount() !=null )
							Assert.assertEquals(retPlatBills.get(i).getPlatformActurallyPayAmount(),dbPlatBills.get(i).getPlatformActurallyPayAmount());
						Assert.assertEquals(retPlatBills.get(i).getPlatformChargedAmount(),dbPlatBills.get(i).getPlatformChargedAmount());
						Assert.assertEquals(retPlatBills.get(i).getPlatformDiscount(),dbPlatBills.get(i).getPlatformDiscount());
						Assert.assertEquals(retPlatBills.get(i).getPlatformPayAmount(),dbPlatBills.get(i).getPlatformPayAmount());
						Assert.assertEquals(retPlatBills.get(i).getPlatformPrepaymentAmount(),dbPlatBills.get(i).getPlatformPrepaymentAmount());
						Assert.assertEquals(retPlatBills.get(i).getPlatformRefundAmount(),dbPlatBills.get(i).getPlatformRefundAmount());
						Assert.assertEquals(retPlatBills.get(i).getSn(),dbPlatBills.get(i).getSn());
						Assert.assertEquals(retPlatBills.get(i).getStatus(),dbPlatBills.get(i).getStatus());
						Assert.assertEquals(retPlatBills.get(i).getRemark(),dbPlatBills.get(i).getRemark());
						Assert.assertEquals(retPlatBills.get(i).getDiscountAmount(),dbPlatBills.get(i).getDiscountAmount());//折后应付
						Assert.assertEquals(retPlatBills.get(i).getConsumeQuotaAmount(),dbPlatBills.get(i).getConsumeQuotaAmount());//消费额度
						//平台账单流转日志
						log.debug("sn..."+dbPlatBills.get(i).getSn());
						List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbPlatBills.get(i).getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_PLATFORM_AUDIT.getValue(),null);//获取结算平台账单审核
						List<TradeCommonLogResultDTO> retCommonLogList = retPlatBills.get(i).getCirculationLog();
						Assert.assertEquals(retCommonLogList.size(),dbCommonLogList.size());
						for(int k=0;k<dbCommonLogList.size();k++){
							Assert.assertEquals(retCommonLogList.get(k).getOperation(),dbCommonLogList.get(k).getOperation());
							Assert.assertEquals(retCommonLogList.get(k).getOperatorName(),dbCommonLogList.get(k).getOperatorName());
							Assert.assertEquals(retCommonLogList.get(k).getRefSn(),dbCommonLogList.get(k).getRefSn());
							Assert.assertEquals(retCommonLogList.get(k).getLogType(),dbCommonLogList.get(k).getLogType());
							Assert.assertEquals(retCommonLogList.get(k).getGmtCreated().getTime(),dbCommonLogList.get(k).getGmtCreated().getTime());
						}
						dbHospitalNeedPayment += dbPlatBills.get(i).getPlatformChargedAmount().longValue();
						if(dbPlatBills.get(i).getPlatformActurallyPayAmount() !=null)
							dbHospitalActualPayment += dbPlatBills.get(i).getPlatformActurallyPayAmount().longValue();
						if(dbPlatBills.get(i).getConsumeQuotaAmount()!=null)
							dbHospitaltotalConsumeQuotaAmount += dbPlatBills.get(i).getConsumeQuotaAmount();
						if(dbPlatBills.get(i).getDiscountAmount()!=null)
							dbHospitaltotalDiscountAmount += dbPlatBills.get(i).getDiscountAmount();
						
					}
					log.info("通过数据库计算获得 医院"+dbHospitalList.get(j)+"消费额度使用合计:"+dbHospitaltotalConsumeQuotaAmount+"折后应付:"+dbHospitaltotalDiscountAmount+"应收金额:"+dbHospitalNeedPayment+"实收金额:"+dbHospitalActualPayment);
					Assert.assertEquals(retPlatformSummaryList.get(j).getHospitalConsumeQuotaAmount().longValue(),SettleChecker.getConsumeQuotaByHospital(dbHospitalList.get(j)));//医院的总消费额度
					Assert.assertEquals(retPlatformSummaryList.get(j).getTotalPayment().longValue(),dbHospitalActualPayment);//某医院实收金额验证
					Assert.assertEquals(retPlatformSummaryList.get(j).getPayableTotalAmount().longValue(),dbHospitalNeedPayment);//某医院应收金额验证
					Assert.assertEquals(retPlatformSummaryList.get(j).getTotalConsumeQuotaAmount().longValue(),dbHospitaltotalConsumeQuotaAmount);//某医院账单的消费额度使用合计
					Assert.assertEquals(retPlatformSummaryList.get(j).getTotalDiscountAmount().longValue(),dbHospitaltotalDiscountAmount);//某医院总的折后应付合计
				}

			 }	 	
	}
	
	  @DataProvider
	  public Iterator<String[]> ops_platSummaryBill(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_platSummaryBill.csv",18);
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
