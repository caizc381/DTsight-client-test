package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetailQueryDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaStatistics;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;

/**
 * OPS->消费额度
 * 
 * 医院统计
 * 展示有消费明细的医院列表
 * @author huifang
 * 
 *
 */
public class GetPlatformConsumeQuotaStatisticsTest extends OpsBase{

	@Test(description = "列举医院统计",groups = {"qa"},dataProvider = "consumeQuotaManage")
	public void test_01_getplatformConsumeQuotaStatics(String ...args){
		String hospitalStr = args[1];
		String provinceStr = args[2];
		String cityStr = args[3];
		String districtStr = args[4];
		String pageSize = args[5];
		int hospitalId = -1;
		int province = -1;
		int city = -1;
		int district = -1;
		TradeConsumeQuotaDetailQueryDTO dto = new TradeConsumeQuotaDetailQueryDTO();
		if(!IsArgsNull(hospitalStr)){
			hospitalId = Integer.parseInt(hospitalStr);
			dto.setOrganizationId(hospitalId);
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
		HttpResult response = httpclient.post(Flag.OPS,OPS_GetPlatConsumeQuotaStatistics, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"错误原因:"+response.getBody());
		String body = response.getBody();
		TradeConsumeQuotaStatistics  retConsumeQuotaStatic = null;
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			retConsumeQuotaStatic = JSON.parseObject(body, TradeConsumeQuotaStatistics.class);
		}
		
	
		if(checkdb){
			long totalAmount = 0l;
			long presentMounthAmont = 0l;
			long forwardMounthAmont = 0l;
			List<Integer> hospitalIntLists = SettleChecker.getHasConsumeQuotaHospitalList(province, city, district, hospitalId);
		   	List<TradeConsumeQuotaStatistics> dbConsumeQuotaStaticList = SettleChecker.getConsumeQuotaStatiList(hospitalIntLists);		
			   	for(int k=0;k<dbConsumeQuotaStaticList.size();k++){
			   		totalAmount += dbConsumeQuotaStaticList.get(k).getTotalAmount();
			   		presentMounthAmont += dbConsumeQuotaStaticList.get(k).getPresentMounthAmont();
			   		forwardMounthAmont += dbConsumeQuotaStaticList.get(k).getForwardMounthAmont();
			   		
			   	}
			   	System.out.println("医院"+hospitalId+"省份id"+province+"城市id"+city+"区域id"+district+"平台消费额度"+totalAmount+"上月消费额度"+forwardMounthAmont+"本月消费额度"+presentMounthAmont);
			   	Assert.assertEquals(retConsumeQuotaStatic.getOrganizationId().intValue(),-1);
			   	Assert.assertEquals(retConsumeQuotaStatic.getTotalAmount().longValue(),totalAmount);
			   	Assert.assertEquals(retConsumeQuotaStatic.getPresentMounthAmont().longValue(),presentMounthAmont);
			   	Assert.assertEquals(retConsumeQuotaStatic.getForwardMounthAmont().longValue(),forwardMounthAmont);

		   	  }
			
	}
	
	  @DataProvider
	  public Iterator<String[]> consumeQuotaManage(){
			try {
				return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_consumeQuotaManage.csv",18);
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
