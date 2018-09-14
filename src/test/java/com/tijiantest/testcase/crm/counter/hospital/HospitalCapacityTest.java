package com.tijiantest.testcase.crm.counter.hospital;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class HospitalCapacityTest extends CounterBase {

	@SuppressWarnings({ "unchecked", "unused" })
	@Test(description = "医院预约名额当前余量、容量信息", groups = { "qa" })
	public void test_01_capacity() throws SqlException, ParseException {
		String startDate = DateUtils.format("yyyy-MM-dd", DateUtils.theFirstDayOfMonth(0));
		String endDate = DateUtils.format("yyyy-MM-dd", DateUtils.theLastDayOfMonth(0));

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", String.valueOf(defhospital.getId())));
		pairs.add(new BasicNameValuePair("startDate", startDate));
		pairs.add(new BasicNameValuePair("endDate", endDate));

		HttpResult result = httpclient.get(CountHosp_Capacity, pairs);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		JSONArray getJsonArray = JSONArray.parseArray(body);
		for (int i = 0; i < getJsonArray.size(); i++) {
			JSONObject jo = getJsonArray.getJSONObject(i);

			Map<Integer, HospitalCapacityCell> h = (Map<Integer, HospitalCapacityCell>) JSON.parse(jo.toJSONString());
			
		}

		if (checkdb) {
			//先获取时段
			String dayRangeSql = "select id, name from tb_hospital_period_settings  where hospital_id =?";
			List<Map<String, Object>> dayRangeList = DBMapper.query(dayRangeSql, defhospital.getId());
			
			//获取人数控制项目
			String itemsSql = "select distinct a.id as id, a.name as name from tb_examitem a  inner join tb_examitem_relation b on a.id = b.item_id and a.hospital_id = ? and b.type = 4";
			List<Map<String, Object>> itemsList = DBMapper.query(itemsSql, defhospital.getId());
			//构造总人数
			Map<String, Object> totalMap = new HashMap<>();
			totalMap.put("id", "-1");
			totalMap.put("name", "总人数");
			itemsList.add(totalMap);
			
			//在获取tb_hospital_capacity_used
//			String usedSql = " select  id, hospital_id, period_id, cur_date, exam_item, available_num, max_num, config_type from tb_hospital_capacity_used where hospital_id = ? and cur_date between ? and ?";
			
			List<Map<String, Object>> usedList = HospitalCounterUtil.getItemUsedCapacity(defhospital.getId(), DateUtils.theFirstDayOfMonth(0), DateUtils.theLastDayOfMonth(0));
            
			
			//再获取tb_hospital_capacity_config
			String configSql = "select id, hospital_id, period_id, day_of_week, exam_item, capacity from tb_hospital_capacity_config where  hospital_id = ?";
			List<Map<String, Object>> configList = DBMapper.query(configSql, defhospital.getId());
			
			//合并数据
			
			//验证
		}
	}
}
