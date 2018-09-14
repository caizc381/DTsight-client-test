package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementPayRecordQueryDTO;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeHospitalPlatformBill;
import com.tijiantest.model.settlement.TradeSettlementPayRecord;
//import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 * 查询付款记录
 * 位置: ops->结算管理->付款记录
 * @author hongyan
 *
 */
public class GetSettlementPayRecordListTest extends OpsBase{

	@Test(description = "获取付款记录列表" , groups = {"qa"},dataProvider = "payRecord")
	public void test_01_payRecordList(String ...args) throws ParseException{
		String hospitalIdStr = args[1];
		String startTimeStr = args[5];
		String endTimeStr = args[6];
		String pageSize = args[7];
		String provinceStr = args[2];
		String cityStr = args[3];
		String districtStr = args[4];
		int hospitalId = -1;
		int province=-1;
		int city=-1;
		int district=-1;
		String start_time = null;
		String end_time = null;
		SettlementPayRecordQueryDTO dto = new SettlementPayRecordQueryDTO();

		if(!IsArgsNull(hospitalIdStr)){
			hospitalId = Integer.parseInt(hospitalIdStr);
			dto.setOrganizationId(hospitalId);
		}

		
		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(startTimeStr);
			}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(endTimeStr);
			}	
		if(!IsArgsNull(provinceStr)){
			province = Integer.parseInt(provinceStr);
			dto.setProvinceId(province);
			}
		if(!IsArgsNull(cityStr)){
			city = Integer.parseInt(cityStr);
			dto.setCityId(city);
			}
		if(!IsArgsNull(districtStr)){
			district = Integer.parseInt(districtStr);
			dto.setDistrictId(district);
			}
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			//log.info("pageSize:"+pageSize);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		
	
		HttpResult response = httpclient.post(Flag.OPS,GetSettlementPayRecordList, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String records = JsonPath.read(response.getBody(), "$.records").toString();
		String body = response.getBody();
		System.out.println(body);
		List<TradeSettlementPayRecord> retList = JSON.parseArray(records, TradeSettlementPayRecord.class);
		if(checkdb){
			 List<Integer> hospitalList = SettleChecker.getHavePayRecordList(province, city, district, hospitalId);
			 System.out.println("医院列表"+hospitalList); 
			 
				String sql = "select d.* from tb_trade_settlement_payment_record  d ";
				if(hospitalId != -1){
					sql += " where d.is_deleted = 0 and d.organization_id = "+hospitalId;
				}else  if(province == -1){//全国
					sql += " where d.is_deleted = 0 ";

				}else{
					if(city == -1){//有省的时候
						sql += " , tb_hospital h where  h.address_id  like  '"+province/1000 +"%'  and h.id = d.organization_id  and d.is_deleted = 0 ";
					}else{
						if(district == -1){//有省/市的时候
							sql += " , tb_hospital h where  h.address_id  like   '"+city/100 +"%'  and h.id = d.organization_id  and d.is_deleted = 0 ";
						}else//有省/市/区的时候
							sql += "  , tb_hospital h where  h.address_id  like   '"+district +"%'  and h.id = d.organization_id  and d.is_deleted = 0 ";
					}
				}
				
				if(start_time != null)
					sql += " and d.gmt_created > '"+start_time+"'";
				if (end_time != null)
					sql += " and d.gmt_created < '"+end_time+"'";
				sql += " and d.is_deleted = 0 order by d.payment_time desc";
				if(pageSize != null)
					sql += " limit "+pageSize;
				log.info("sql.."+sql);
		
			 
			log.info("sql.."+sql);
			
			List<TradeSettlementPayRecord> dbList = SettleChecker.getTradeSettlePaymentRecordBySql(sql);
			
			Assert.assertEquals(retList.size(),dbList.size());  
			for(int i=0;i<dbList.size();i++){
				if(dbList.get(i).getOrganizationId() !=null){
					Assert.assertEquals(retList.get(i).getOrganizationId(), dbList.get(i).getOrganizationId());
					Assert.assertEquals(retList.get(i).getOrganizationName(), dbList.get(i).getOrganizationName());
				}
									 						 
						 
				System.out.println("付款账单流水号..."+retList.get(i).getSn());
				Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());
				Assert.assertEquals(retList.get(i).getOperatorName(), dbList.get(i).getOperatorName());
				Assert.assertEquals(retList.get(i).getOperatorId(), dbList.get(i).getOperatorId());
				Assert.assertEquals(retList.get(i).getOrganizationId(), dbList.get(i).getOrganizationId());
				Assert.assertEquals(retList.get(i).getOrganizationName(), dbList.get(i).getOrganizationName());
				Assert.assertEquals(retList.get(i).getPayableAmount(), dbList.get(i).getPayableAmount());//账单金额
				Assert.assertEquals(retList.get(i).getRealAmount(), dbList.get(i).getRealAmount()); //平台实付金额
				Assert.assertEquals(retList.get(i).getPaymentTime(), dbList.get(i).getPaymentTime()); //付款时间
				Assert.assertEquals(retList.get(i).getSn(), dbList.get(i).getSn()); //流水账单号
				Assert.assertEquals(retList.get(i).getGmtCreated(), dbList.get(i).getGmtCreated());
				Assert.assertEquals(retList.get(i).getGmtModified(), dbList.get(i).getGmtModified());
				Assert.assertEquals(retList.get(i).getType(), dbList.get(i).getType()); //单位类型
				Assert.assertEquals(retList.get(i).getCertificate(), dbList.get(i).getCertificate());//凭证
				Assert.assertEquals(retList.get(i).getRemark(), dbList.get(i).getRemark());//付款记录备注
				Assert.assertEquals(retList.get(i).getTotalConsumeQuotaAmount(),dbList.get(i).getTotalConsumeQuotaAmount());
				Assert.assertEquals(retList.get(i).getTotalDiscountAmount(),dbList.get(i).getTotalDiscountAmount());
				
				
				if(dbList.get(i).getType() == 1){//平台付款
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
			}
		}
		
	}
	
	  @DataProvider
	  public Iterator<String[]> payRecord(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_payRecord.csv",18);
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