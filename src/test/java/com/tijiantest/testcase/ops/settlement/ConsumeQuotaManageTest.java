package com.tijiantest.testcase.ops.settlement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
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
public class ConsumeQuotaManageTest extends OpsBase{

	@Test(description = "列举医院统计部分",groups = {"qa"},dataProvider = "consumeQuotaManage")
	public void test_01_consumeQuotaManage(String ...args){
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
		HttpResult response = httpclient.post(Flag.OPS,OPS_ConsumeQuotaManage, JSON.toJSONString(dto));
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"错误原因:"+response.getBody());
		String body = response.getBody();
		List<TradeConsumeQuotaStatistics>  retConsumeQuotaStaticList = null;
		System.out.println(body);
		//当返回不为空,获取返回的字段值
		if(!body.equals("{}") && !body.equals("")){
			String records = JsonPath.read(body, "$.records").toString();
			retConsumeQuotaStaticList = JSON.parseArray(records, TradeConsumeQuotaStatistics.class);
		}
		
	
		if(checkdb){
			List<Integer> hospitalIntLists = SettleChecker.getHasConsumeQuotaHospitalList(province, city, district, hospitalId);
			System.out.println("医院列表..."+hospitalIntLists);
		   	List<TradeConsumeQuotaStatistics> dbConsumeQuotaStaticList = SettleChecker.getConsumeQuotaStatiList(hospitalIntLists);
		
			 //按照消费额度降序排序
			 Collections.sort(dbConsumeQuotaStaticList, new Comparator<TradeConsumeQuotaStatistics>() {
			    	@Override
			    	public int compare(TradeConsumeQuotaStatistics o1,
			    			TradeConsumeQuotaStatistics o2) {
			    		return (int)(-o1.getOrganizationId()+o2.getOrganizationId());
			    	}
				});

			Collections.sort(retConsumeQuotaStaticList, new Comparator<TradeConsumeQuotaStatistics>() {
				@Override
				public int compare(TradeConsumeQuotaStatistics o1,
								   TradeConsumeQuotaStatistics o2) {
					return (int)(-o1.getOrganizationId()+o2.getOrganizationId());
				}
			});


			if(dbConsumeQuotaStaticList.size() > 0){
		   		Assert.assertEquals(retConsumeQuotaStaticList.size(), dbConsumeQuotaStaticList.size());
		   	 	//总额
			   	for(int k=0;k<dbConsumeQuotaStaticList.size();k++){
			   		log.info("现在对比医院.."+dbConsumeQuotaStaticList.get(k).getOrganizationName());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getOrganizationName(),dbConsumeQuotaStaticList.get(k).getOrganizationName());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getOrganizationId(),dbConsumeQuotaStaticList.get(k).getOrganizationId());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getPresentMounthAmont(),dbConsumeQuotaStaticList.get(k).getPresentMounthAmont());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getForwardMounthAmont(),dbConsumeQuotaStaticList.get(k).getForwardMounthAmont());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getTodoList(),dbConsumeQuotaStaticList.get(k).getTodoList());
			   		Assert.assertEquals(retConsumeQuotaStaticList.get(k).getTotalAmount(),dbConsumeQuotaStaticList.get(k).getTotalAmount());
			   	}
		   	  }
		   	else
		   		Assert.assertEquals(retConsumeQuotaStaticList.size(), dbConsumeQuotaStaticList.size());
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
