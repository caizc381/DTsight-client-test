package com.tijiantest.testcase.ops.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.organization.OrganizationListVo;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 订单管理-获取体检中心/渠道列表
 * @author admin
 *
 */
public class OrderOrganizationListTest extends OpsBase{
	
	public static List<Hospital> hospitals = new ArrayList<>();
	public static List<Hospital> channels = new ArrayList<>();

	@Test(description="获取体检中心/渠道列表",groups= {"qa","orderOrganizationList"})
	public void test_orderOrganizationList() throws SqlException {
		HttpResult result = httpclient.get(Flag.OPS,OpsOrder_OrderOrganizationList);
		
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		OrganizationListVo organizationListVo =JSON.parseObject(body, OrganizationListVo.class); 
		hospitals = organizationListVo.getHospitals();
		channels = organizationListVo.getChannels();
		
		if (checkdb) {
			String hospitalSql = "select id,name,enable,show_in_list as showInList, default_manager_id as defaultManagerId,pinyin as pinYin"
					+" from tb_hospital where organization_type=? and enable=?";
			List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, OrganizationTypeEnum.HOSPITAL.getCode(),1);
			Assert.assertEquals(hospitals.size(), hospitalList.size());
			
			for (int i = 0; i < hospitals.size(); i++) {	
				Hospital hospital = hospitals.get(i);
				Map<String, Object> map = hospitalList.get(i);
				
				Assert.assertEquals(hospital.getId(), map.get("id"));
				Assert.assertEquals(hospital.getName(), map.get("name").toString());
				Assert.assertEquals(hospital.getEnable(), map.get("enable"));
				Assert.assertEquals(hospital.getShowInList(), map.get("showInList")==null?0:map.get("showInList"));
				Assert.assertEquals(hospital.getDefaultManagerId(), map.get("defaultManagerId"));
			}
			
			List<Map<String, Object>> channelList = DBMapper.query(hospitalSql, OrganizationTypeEnum.CHANNEL.getCode(),1);
			Assert.assertEquals(channels.size(), channelList.size());
			
			for (int i = 0; i < channels.size(); i++) {
				Hospital channel = channels.get(i);
				Map<String, Object> map = channelList.get(i);
				Assert.assertEquals(channel.getId(), map.get("id"));
				Assert.assertEquals(channel.getName(), map.get("name").toString());
				Assert.assertEquals(channel.getEnable(), map.get("enable"));
				Assert.assertEquals(channel.getShowInList(), map.get("showInList")==null?0:map.get("showInList"));
				Assert.assertEquals(channel.getDefaultManagerId(), map.get("defaultManagerId"));
			}
			
		}
	}
}
