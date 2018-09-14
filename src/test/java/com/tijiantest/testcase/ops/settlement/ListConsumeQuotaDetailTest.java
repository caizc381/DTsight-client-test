package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

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
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetailQueryDTO;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.pagination.Page;

/**
 * OPS->消费额度
 * 
 * 消费明细
 * 
 * @author huifang
 * @param 
 *
 */
public class ListConsumeQuotaDetailTest extends OpsBase{

	@Test(description = "根据地区/体检机构涮选消费明细",groups = {"qa"},dataProvider = "consumeQuotaQuery")
	public void test_01_listHospitalConsumeQutoaDetail(String ...args){
		String hospitalStr = args[1];
		String startTimeStr = args[2];
		String endTimeStr = args[3];
		String sceneStr = args[4];
		String statusStr = args[5];
		String provinceStr = args[6];
		String cityStr = args[7];
		String districtStr = args[8];
		String pageSize = args[9];
		String start_time = null;
		String end_time = null;
		int scene = -1;
		int status = -1;
		int province = -1;
		int city = -1;
		int district = -1;
		int hospitalId = -1;
		TradeConsumeQuotaDetailQueryDTO dto = new TradeConsumeQuotaDetailQueryDTO();
		if(!IsArgsNull(hospitalStr)){
			hospitalId = Integer.parseInt(hospitalStr);
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
		if(!IsArgsNull(sceneStr)){
			scene = Integer.parseInt(sceneStr);
			dto.setScene(Arrays.asList(scene));
			}
		if(!IsArgsNull(statusStr)){
			status = Integer.parseInt(statusStr);
			dto.setStatus(Arrays.asList(status));
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
			page.setCurrentPage(1);
			page.setPageSize(Integer.parseInt(pageSize));
			dto.setPage(page);
		}
		HttpResult response = httpclient.post(Flag.OPS,OPS_ListConsumeQuotaDetail, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		List<TradeConsumeQuotaDetail>  retConsumeQuotaDetail = null;
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			String records = JsonPath.read(body,"$.records").toString();
			retConsumeQuotaDetail = JSON.parseArray(records, TradeConsumeQuotaDetail.class);
		}
	
		if(checkdb){
			//获取涮选的医院列表
			List<Integer> hospitalList = SettleChecker.getHasConsumeQuotaHospitalList(province, city, district, hospitalId);
			System.out.println("医院列表...."+hospitalList);
			//消费额度记录对比
			String sql = "select * from tb_trade_consume_quota_detail where is_deleted = 0 ";
			if(start_time != null)
				sql += " and gmt_created > '"+start_time+"'";
			if (end_time != null)
				sql += " and gmt_created < '"+end_time+"'";
			if (scene != -1)
				sql += " and scene ="+scene;
			if (status != -1)
				sql += " and status ="+status;
			if(hospitalList != null && hospitalList.size() > 0)
				sql += " and organization_id in ("+  ListUtil.IntegerlistToString(hospitalList) + ")";
			else
			    sql += " and organization_id = -2";
			sql += "  order by gmt_created desc ";
			if(pageSize != null)
				sql += " limit "+pageSize;
			System.out.println("sql.."+sql);
			List<TradeConsumeQuotaDetail> dbQuotaList = SettleChecker.getTradeConsumeQuotaDetail(sql);
			Collections.sort(dbQuotaList, new Comparator<TradeConsumeQuotaDetail>() {
				@Override
				public int compare(TradeConsumeQuotaDetail o1, TradeConsumeQuotaDetail o2) {
					return  o1.getId() - o2.getId();
				}
			});

			Collections.sort(retConsumeQuotaDetail, new Comparator<TradeConsumeQuotaDetail>() {
				@Override
				public int compare(TradeConsumeQuotaDetail o1, TradeConsumeQuotaDetail o2) {
					return  o1.getId() - o2.getId();
				}
			});
			for(int i=0 ; i<dbQuotaList.size();i++){
//				System.out.println("dbOu"+dbQuotaList.get(i).getId());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getId(),dbQuotaList.get(i).getId());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getCompanyId(),dbQuotaList.get(i).getCompanyId());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getCompanyName(),dbQuotaList.get(i).getCompanyName());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getGmtCreated(),dbQuotaList.get(i).getGmtCreated());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getGmtModified(),dbQuotaList.get(i).getGmtModified());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getOrganizationId(),dbQuotaList.get(i).getOrganizationId());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getIsDeleted(),dbQuotaList.get(i).getIsDeleted());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getId(),dbQuotaList.get(i).getId());	
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getPayTime(),dbQuotaList.get(i).getPayTime());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getRemark(),dbQuotaList.get(i).getRemark());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getScene(),dbQuotaList.get(i).getScene());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getSn(),dbQuotaList.get(i).getSn());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getStatus(),dbQuotaList.get(i).getStatus());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getVersion(),dbQuotaList.get(i).getVersion());
				Assert.assertEquals(retConsumeQuotaDetail.get(i).getAmount(),dbQuotaList.get(i).getAmount());
				//消费额度流转日志
				List<TradeCommonLogResultDTO> dbCommonLogList = SettleChecker.getTradeCommonLogList(dbQuotaList.get(i).getSn(), LogTypeEnum.LOG_TYPE_SETTLEMENT_CONSUME_QUOTA_AUDIT.getValue(),null);//获取消费额度流转日志
				List<TradeCommonLogResultDTO> retCommonLogList = retConsumeQuotaDetail.get(i).getCirculationLog();
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
	  public Iterator<String[]> consumeQuotaQuery(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_consumeQuotaQuery.csv",18);
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
