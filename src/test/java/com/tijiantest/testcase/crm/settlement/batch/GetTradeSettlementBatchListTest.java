package com.tijiantest.testcase.crm.settlement.batch;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementBatchQueryDTO;
import com.tijiantest.model.settlement.TradeSettlementBatch;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 * 结算批次页面（显示所有结算批次）
 * 支持涮选[时间|单位|结算状态]
 * 最新的结算批次首页显示，结算批次序号倒序排列
 * @author huifang
 *
 */
public class GetTradeSettlementBatchListTest  extends SettleBase{

	@Test(description = "结算批次列表显示" ,groups={"qa"},dataProvider = "SettlementBatchList")
	public void test_01_getTradeSettlementBatchList(String ...args) throws ParseException{
		String companyIdStr = args[1];
		String companyPayStr = args[2];
		String companyRefundStr = args[3];
		String hospitalStr = args[4];
		String onlinepayStr = args[5];
		String onlineRefundStr = args[6];
		String platformPayStr = args[7];
		String platformRefundStr = args[8];
		String prepaymentStr = args[9];
		String startTimeStr = args[10];
		String endTimeStr = args[11];
		String statusStr = args[12];
		String pageSize = args[13];
		int hospitalId = defSettHospitalId;
		int companyId = -1;
		int companyPay = 0; //单位支付
		int onlinePay = 0; //线上支付
		int platformPay = 0; //平台支付
		int companyRefund = 0; //单位退款
		int onlineRefund = 0; //线上退款
		int platformRefund = 0; //平台退款
		int prepayment = 0; //特殊退款
		String start_time = null;
		String end_time = null;
		String orderColumn = "sn";
		boolean isAsc = false;
		int status = -1;
		SettlementBatchQueryDTO dto = new SettlementBatchQueryDTO();
		if(!IsArgsNull(companyIdStr)){
			companyId = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId, "id", true).get(0).getId();
			dto.setCompanyId(companyId);
			}
		if(!IsArgsNull(hospitalStr))
			dto.setOrganizationId(hospitalId);
		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			dto.setStartTime(startTimeStr);
			}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			dto.setEndTime(endTimeStr);
			}	
		if(!IsArgsNull(statusStr)){
			status = Integer.parseInt(statusStr);
			dto.setStatus(status);
			}
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		
	
		HttpResult response = httpclient.post(GetTradeSettlementBatchList, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String records = JsonPath.read(response.getBody(), "$.records").toString();
		List<TradeSettlementBatch> retList = JSON.parseArray(records, TradeSettlementBatch.class);
		log.info("返回结果..."+response.getBody());
		if(checkdb){
			log.info("hospitalId"+hospitalId + "companyId"+companyId+"...start_time"+start_time+"...end_time"+end_time+"...status"+status);
			List<TradeSettlementBatch> dbList = SettleChecker.getTradeSettlementBatch(hospitalId, companyId, start_time, end_time,status,orderColumn,isAsc,pageSize);
			Assert.assertEquals(retList.size(),dbList.size());
			for(int i=0;i<dbList.size();i++){
//				log.info("比较批次号.."+dbList.get(i).getSn()+"SS"+retList.get(i).getSn());
				Assert.assertEquals(retList.get(i).getCompanyId(), dbList.get(i).getCompanyId());
				Assert.assertEquals(retList.get(i).getCompanyPayAmount(), dbList.get(i).getCompanyPayAmount());
				Assert.assertEquals(retList.get(i).getCompanyRefundAmount(), dbList.get(i).getCompanyRefundAmount());
				Assert.assertEquals(retList.get(i).getHospitalId(), dbList.get(i).getHospitalId());
				Assert.assertEquals(retList.get(i).getHospitalSettlementStatus(), dbList.get(i).getHospitalSettlementStatus());
				Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());
				Assert.assertEquals(retList.get(i).getIsdeleted(), dbList.get(i).getIsdeleted());
				Assert.assertEquals(retList.get(i).getOnlinePayAmount(), dbList.get(i).getOnlinePayAmount());
				Assert.assertEquals(retList.get(i).getOnlineRefundAmount(), dbList.get(i).getOnlineRefundAmount());
				Assert.assertEquals(retList.get(i).getOperatorName(), dbList.get(i).getOperatorName());
				Assert.assertEquals(retList.get(i).getOperatorId(), dbList.get(i).getOperatorId());
				Assert.assertEquals(retList.get(i).getPlatformPayAmount(), dbList.get(i).getPlatformPayAmount());
				Assert.assertEquals(retList.get(i).getPlatformPrepaymentAmount(), dbList.get(i).getPlatformPrepaymentAmount());
				Assert.assertEquals(retList.get(i).getPlatformRefundAmount(), dbList.get(i).getPlatformRefundAmount());
				Assert.assertEquals(retList.get(i).getOfflinePayAmount(), dbList.get(i).getOfflinePayAmount());
				Assert.assertEquals(retList.get(i).getHospitalCouponAmount(), dbList.get(i).getHospitalCouponAmount());
				Assert.assertEquals(retList.get(i).getHospitalCouponRefundAmount(), dbList.get(i).getHospitalCouponRefundAmount());
				Assert.assertEquals(retList.get(i).getPlatformCouponAmount(), dbList.get(i).getPlatformCouponAmount());
				Assert.assertEquals(retList.get(i).getPlatformCouponRefundAmount(), dbList.get(i).getPlatformCouponRefundAmount());
				Assert.assertEquals(retList.get(i).getChannelCouponAmount(), dbList.get(i).getChannelCouponAmount());
				Assert.assertEquals(retList.get(i).getChannelCouponRefundAmount(), dbList.get(i).getChannelCouponRefundAmount());
				Assert.assertEquals(retList.get(i).getChannelCompanyPayAmount(), dbList.get(i).getChannelCompanyPayAmount());
				Assert.assertEquals(retList.get(i).getChannelCompanyRefundAmount(), dbList.get(i).getChannelCompanyRefundAmount());
				Assert.assertEquals(retList.get(i).getSn(), dbList.get(i).getSn());
				Assert.assertEquals(retList.get(i).getGmtCreated(), dbList.get(i).getGmtCreated());
				Assert.assertEquals(retList.get(i).getGmtModified(), dbList.get(i).getGmtModified());

			}
		}
	}
	
	  @DataProvider
	  public Iterator<String[]> SettlementBatchList(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/SettlementBatchList.csv",18);
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
