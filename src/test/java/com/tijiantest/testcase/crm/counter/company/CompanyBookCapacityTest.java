package com.tijiantest.testcase.crm.counter.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.counter.CompanyCapacityCell;
import com.tijiantest.model.counter.DayRange;
import com.tijiantest.model.counter.HospitalCapacityCell;
import com.tijiantest.model.counter.OrderableCapacityCell;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.SqlException;


public class CompanyBookCapacityTest extends CounterBase {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CompanyBookCapacityTest.class);
	
	@Test(description = "单位预约当前时间段可预约量", dataProvider = "bookCapacity", groups={"qa"})
	public void test_01_getBookAvail(String... args) throws ParseException, IOException, SqlException {
		System.out.println("----------------------获取单位预约当前时间段可预约量测试Start----------------------");
		Date start = DateUtils.theFirstDayOfMonth(0);
		Date end = DateUtils.theLastDayOfMonth(0);
		String startDate = DateUtils.format("yyyy-MM-dd", start);
		String endDate = DateUtils.format("yyyy-MM-dd", end);
		
		//int orderNum = Integer.parseInt(args[3]);
		int orderCount = 1;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("startDate", startDate));
		params.add(new BasicNameValuePair("endDate", endDate));
		params.add(new BasicNameValuePair("companyId", defnewcompany.getId() + ""));
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		params.add(new BasicNameValuePair("orderNum", orderCount + ""));
		HttpResult response = httpclient.get(CountComp_BookCapacity, params);

		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK, "错误原因:" + response.getBody());
		String body = response.getBody();
		//获取返回值-start
		List<Map<Integer, OrderableCapacityCell>> ocResponse = new ArrayList<Map<Integer, OrderableCapacityCell>>();
		JSONArray getJsonArray = JSONArray.parseArray(body);
		for (int i = 0; i < getJsonArray.size(); i++) {
			JSONObject jo = getJsonArray.getJSONObject(i);
			//Map<Integer, OrderableCapacityCell> h = (Map<Integer, OrderableCapacityCell>) JSON.parse(jo.toJSONString());
			Map<Integer, OrderableCapacityCell> h = JSON.parseObject(jo.toJSONString(), new TypeReference<Map<Integer, OrderableCapacityCell>>(){});
			ocResponse.add(h);
			/*for (Object object : h.entrySet()) {
				System.out.println(((Map.Entry) object).getKey() + "=============" + ((Map.Entry) object).getValue());		
			}*/
		}
		logger.debug(body);//获取返回值-end

		if (checkdb) {
			//获取数据库数据
			List<Map<Integer, OrderableCapacityCell>> ocDB = getOrderableCapacity(defnewcompany.getId(),
					defhospital.getId(),orderCount,start,end);
			
			for(int i = 0;i<ocDB.size()&&i<ocResponse.size();i++){
				Map<Integer, OrderableCapacityCell> mapDB = ocDB.get(i);
				Map<Integer, OrderableCapacityCell> mapResponse = ocResponse.get(i);
				
				Assert.assertEquals(mapDB.keySet().size(), mapResponse.keySet().size());
				List<OrderableCapacityCell> mapDBVal = mapDB.values().stream().collect(Collectors.toList());
				List<OrderableCapacityCell> mapResponseVal = mapResponse.values().stream().collect(Collectors.toList());
//				System.out.println("mapDBVal:"+JSON.toJSONString(mapDBVal)+" \nmapResponseVal:"+JSON.toJSONString(mapResponseVal));
				for(int j=0;j<mapDBVal.size()&&j<mapResponseVal.size();j++){
					Assert.assertEquals(mapDBVal.get(j).getAvailableNum(), mapResponseVal.get(j).getAvailableNum());
				}
			}
		}
		System.out.println("----------------------获取单位预约当前时间段可预约量测试End----------------------");
	}
	
	public List<Map<Integer, OrderableCapacityCell>> getOrderableCapacity(Integer companyId, Integer hospitalId,
			Integer orderCount, Date startDate, Date endDate) {
		//获取时段
		List<DayRange> dayRangeLst = CounterChecker.getDayRange(hospitalId);
		//获取体检中心容量及可预约量
		List<Map<Integer, HospitalCapacityCell>> hospitalCapacityLst = CounterChecker.getPeriodCapacityByHospital(
				hospitalId, startDate, endDate);
		//获取单位人数人数设置和可用量
		List<Map<Integer, CompanyCapacityCell>> companyCapacityLst = CounterChecker.getPeriodCapacityByCompany(
				hospitalId, companyId, startDate, endDate);
		logger.debug("getOrderableCapacity companyCapacityLst is {}",companyCapacityLst);
		List<Map<Integer, OrderableCapacityCell>> orderableCapacityLst = new ArrayList<Map<Integer, OrderableCapacityCell>>();
		int daysInterval = DateUtils.daysBetween(startDate, endDate);
		Calendar current = Calendar.getInstance();
		current.setTime(startDate);
		for (int i = 0; i <= daysInterval; i++) {
			Map<Integer, HospitalCapacityCell> hospitalCapacity = hospitalCapacityLst.get(i);
			Map<Integer, CompanyCapacityCell> companyCapacity = companyCapacityLst.get(i);
			Map<Integer, OrderableCapacityCell> orderableCapacity = new HashMap<Integer, OrderableCapacityCell>();
			for (DayRange dayRange : dayRangeLst) {
				CompanyCapacityCell companyCapacityCell = null == companyCapacity ? null : companyCapacity.get(dayRange
						.getId());
				HospitalCapacityCell hospitalCapacityCell = null == hospitalCapacity ? null : hospitalCapacity
						.get(dayRange.getId());
				OrderableCapacityCell orderableCapacityCell = CounterChecker.mergeCellValue(hospitalCapacityCell, companyCapacityCell,
						orderCount);
				orderableCapacityCell.setExpireDay(OrderChecker.isExpiredOrder(hospitalId, current.getTime()));
				orderableCapacity.put(dayRange.getId(), orderableCapacityCell);
				
			}
			orderableCapacityLst.add(orderableCapacity);
			current.add(Calendar.DAY_OF_MONTH, 1);
		}
		logger.debug("getOrderableCapacity orderableCapacityLst is {}",orderableCapacityLst);
		return orderableCapacityLst;
	};

	@DataProvider(name = "bookCapacity")
	public Iterator<String[]> bookCapacity() {
		try {
			return CvsFileUtil.fromCvsFileToIterator(
					"./csv/counter/company/bookCapacity.csv", 6);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
