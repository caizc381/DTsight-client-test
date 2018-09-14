package com.tijiantest.testcase.crm.counter.hospital;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.counter.DayRange;
import com.tijiantest.model.item.ExamItem;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class HeadCatalogTest extends CounterBase{

	@Test(description="获取体检中心时间段和受限项目设置",groups={"qa"})
	public void test_01_headCatalog() throws SqlException{
		int hospitalId = defhospital.getId();
		List<NameValuePair> pairs =new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		
		HttpResult result = httpclient.get(CountHosp_HeadCatalog,pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		
//		log.info("body..."+body);
		List<DayRange> dayRange = JSON.parseObject(JsonPath.read(body,"$.dayRange").toString(),new TypeReference<List<DayRange>>(){});
		List<ExamItem> examItem = JSON.parseObject(JsonPath.read(body, "$.examItem").toString(),new TypeReference<List<ExamItem>>(){});
		
		if (checkdb) {
			String sql = "select id, name  from tb_hospital_period_settings where hospital_id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
			
			Assert.assertEquals(dayRange.size(), list.size());
			if (dayRange!=null && !dayRange.isEmpty()) {
				for (int i = 0; i < dayRange.size(); i++) {
					DayRange dr = dayRange.get(i);
					Map<String,Object> map = list.get(i);
					
					Assert.assertEquals(dr.getName(), map.get("name").toString());
				}
			}
			
			sql = "select distinct a.id as id, a.name as name ,a.sequence from tb_examitem a  inner join tb_examitem_relation b on a.id = b.item_id and a.hospital_id = ? "
					+ "and b.type = 4  order by a.sequence";
			list = DBMapper.query(sql, hospitalId);
			
			Assert.assertEquals(examItem.size(), list.size());
			if (examItem!=null &&! examItem.isEmpty()) {
				for (int i = 0; i < examItem.size(); i++) {
					ExamItem ei = examItem.get(i);
					Map<String, Object> map = list.get(i);
					Assert.assertEquals(ei.getName(), map.get("name").toString());
					Assert.assertEquals(ei.getId(), map.get("id"));
				}
			}
					
		}
	}
}
