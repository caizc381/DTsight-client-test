package com.tijiantest.testcase.crm.settlement.consume;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.tijiantest.model.common.LogTypeEnum;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.HospitalConsumeQuotaDetailDTO;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetailQueryDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaStatistics;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 *  CRM->结算管理->消费额度
 * 列出医院所有的消费额度
 * @author huifang
 * @param 
 *
 */
public class ListHospitalConsumeQuotaDetailTest extends SettleBase{

	@Test(description = "列出医院所有的消费额度",groups = {"qa"},dataProvider = "consumeQuota")
	public void test_01_listHospitalConsumeQutoaDetail(String ...args){
		String hospitalStr = args[1];
		String startTimeStr = args[2];
		String endTimeStr = args[3];
		String sceneStr = args[4];
		String statusStr = args[5];
		String pageSize = args[6];
		int hospitalId = defSettHospitalId;
		String start_time = null;
		String end_time = null;
		int scene = -1;
		int status = -1;
		TradeConsumeQuotaDetailQueryDTO dto = new TradeConsumeQuotaDetailQueryDTO();
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
		if(!IsArgsNull(sceneStr)){
			scene = Integer.parseInt(sceneStr);
			dto.setScene(Arrays.asList(scene));
			}
		if(!IsArgsNull(statusStr)){
			status = Integer.parseInt(statusStr);
			dto.setStatus(Arrays.asList(status));
			}
		if(!IsArgsNull(pageSize)){
			Page page = new Page();
			page.setOffset(0);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		HttpResult response = httpclient.post(ListHospitalConsumeQuotaDetail, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		HospitalConsumeQuotaDetailDTO  retConsumeQuotaDetail = null;
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			retConsumeQuotaDetail = JSON.parseObject(body, HospitalConsumeQuotaDetailDTO.class);
		}
	
		if(checkdb){
		    //消费额度统计
			TradeConsumeQuotaStatistics retStatics = retConsumeQuotaDetail.getConsumeQuotaStatistics();
			TradeConsumeQuotaStatistics dbStatics = SettleChecker.getConsumeQuotaStatistics(hospitalId);
			System.out.println("本月消费额度.."+dbStatics.getPresentMounthAmont() + "上月的消费额度"+dbStatics.getForwardMounthAmont() + "总消费额度"+dbStatics.getTotalAmount());
			Assert.assertEquals(retStatics.getForwardMounthAmont(),dbStatics.getForwardMounthAmont());
			Assert.assertEquals(retStatics.getPresentMounthAmont(),dbStatics.getPresentMounthAmont());
			Assert.assertEquals(retStatics.getTotalAmount(),dbStatics.getTotalAmount());
			
			//消费额度记录对比
			List<TradeConsumeQuotaDetail> retQuotaList = retConsumeQuotaDetail.getConsumeQuotaDetails();
			String sql = "select * from tb_trade_consume_quota_detail where is_deleted = 0 and organization_id = "+hospitalId;
			if(start_time != null)
				sql += " and gmt_created > '"+start_time+"'";
			if (end_time != null)
				sql += " and gmt_created < '"+end_time+"'";
			if (scene != -1)
				sql += " and scene ="+scene;
			if (status != -1)
				sql += " and status ="+status;
			sql += "  order by gmt_created desc";
			if(pageSize != null)
				sql += " limit "+pageSize;
			List<TradeConsumeQuotaDetail> dbQuotaList = SettleChecker.getTradeConsumeQuotaDetail(sql);
			for(int i=0 ; i<dbQuotaList.size();i++){
				Assert.assertEquals(retQuotaList.get(i).getId(),dbQuotaList.get(i).getId());
				Assert.assertEquals(retQuotaList.get(i).getCompanyId(),dbQuotaList.get(i).getCompanyId());
				Assert.assertEquals(retQuotaList.get(i).getCompanyName(),dbQuotaList.get(i).getCompanyName());
				Assert.assertEquals(retQuotaList.get(i).getGmtCreated(),dbQuotaList.get(i).getGmtCreated());
				Assert.assertEquals(retQuotaList.get(i).getGmtModified(),dbQuotaList.get(i).getGmtModified());
				Assert.assertEquals(retQuotaList.get(i).getOrganizationId(),dbQuotaList.get(i).getOrganizationId());
				Assert.assertEquals(retQuotaList.get(i).getIsDeleted(),dbQuotaList.get(i).getIsDeleted());
				Assert.assertEquals(retQuotaList.get(i).getId(),dbQuotaList.get(i).getId());	
				Assert.assertEquals(retQuotaList.get(i).getPayTime(),dbQuotaList.get(i).getPayTime());
				Assert.assertEquals(retQuotaList.get(i).getRemark(),dbQuotaList.get(i).getRemark());
				Assert.assertEquals(retQuotaList.get(i).getScene(),dbQuotaList.get(i).getScene());
				Assert.assertEquals(retQuotaList.get(i).getSn(),dbQuotaList.get(i).getSn());
				Assert.assertEquals(retQuotaList.get(i).getStatus(),dbQuotaList.get(i).getStatus());
				Assert.assertEquals(retQuotaList.get(i).getVersion(),dbQuotaList.get(i).getVersion());
				Assert.assertEquals(retQuotaList.get(i).getAmount(),dbQuotaList.get(i).getAmount());
				//消费额度流转日志
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbQuotaList.get(i).getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),null);//获取消费额度流转日志
				List<TradeCommonLogResultDTO> retCommonLogList = retQuotaList.get(i).getCirculationLog();
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
	
	  @DataProvider
	  public Iterator<String[]> consumeQuota(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/consumeQuota.csv",18);
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
