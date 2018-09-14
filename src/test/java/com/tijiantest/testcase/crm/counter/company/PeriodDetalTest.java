package com.tijiantest.testcase.crm.counter.company;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.CompanyCapacityCell;
import com.tijiantest.model.counter.CompanyPeriodCapacityDto;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;

import net.sf.json.JSONObject;

public class PeriodDetalTest extends CounterBase {
	@SuppressWarnings("rawtypes")
	@Test(description = "获取单位某日预留情况", dataProvider = "periodDetal", groups = { "qa" })
	public void test_periodDetal(String... args) {
		System.out.println("----------------------获取单位某日预留情况测试Start----------------------");
		Integer hospitalId = defhospital.getId();
		Integer companyId = defnewcompany.getId();
		String dateStr = args[1];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));
		pairs.add(new BasicNameValuePair("companyId", companyId + ""));
		pairs.add(new BasicNameValuePair("date", dateStr));

		HttpResult result = httpclient.get(CountComp_PeriodDetal, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		System.out.println(result.getBody());
		List<Map> resultBody = JSON.parseArray(result.getBody(), Map.class);
		List<CompanyPeriodCapacityDto> companyPeriodCapacityList = new ArrayList<>();
		if (resultBody != null && !resultBody.isEmpty())
			for (Map m : resultBody) {
				String companyName = m.get("companyName").toString();
				Map<Integer, Map<Integer, CompanyCapacityCell>> periodRes = new HashMap<>();
				Map<String, Object> periodStr = Json2Map(m.get("companyPeriodSetting").toString());
				for (String in : periodStr.keySet()) {
					Map<String, Object> hccstr = Json2Map(periodStr.get(in).toString());
					Map<Integer, CompanyCapacityCell> hccMap = new HashMap<>();
					for (String inte : hccstr.keySet()) {
						CompanyCapacityCell hcc = JSON.parseObject(hccstr.get(inte).toString(),
								CompanyCapacityCell.class);
						hccMap.put(Integer.valueOf(inte), hcc);
					}
					periodRes.put(Integer.valueOf(in), hccMap);
				}
				CompanyPeriodCapacityDto companyPeriodCapacity = new CompanyPeriodCapacityDto();
				companyPeriodCapacity.setCompanyName(companyName);
				companyPeriodCapacity.setCompanyPeriodSetting(periodRes);
				companyPeriodCapacityList.add(companyPeriodCapacity);
			}
		else
			companyPeriodCapacityList = null;

		if (checkdb) {
			if (companyPeriodCapacityList != null) {
				Assert.assertEquals(companyPeriodCapacityList.size(), 1);
				CompanyPeriodCapacityDto compPeriodCapacity = companyPeriodCapacityList.get(0);
				Map<Integer, Map<Integer, CompanyCapacityCell>> compCapacityCell = compPeriodCapacity
						.getCompanyPeriodSetting();
				Assert.assertEquals(compPeriodCapacity.getCompanyName(), defnewcompany.getName());

				List<Integer> dayRangeIds = CounterChecker.getDayRangeIds(hospitalId);
				Date date = null;
				try {
					date = sdf.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//时间段
				List<HospitalPeriodSetting> periodSettings = HospitalChecker.getHospitalPeriodSettings(hospitalId);
				//<itemId, <dayRangeIds, CompanyCapacityCell>>
				Map<Integer, Map<Integer, CompanyCapacityCell>> periodMap = CounterChecker
						.getPeriodUsedCapacity(hospitalId, companyId, dayRangeIds, date);

				//返回的size = 时间段size
				Assert.assertEquals(compCapacityCell.size(),periodSettings.size());
				Set<Integer> keys = compCapacityCell.keySet();
				for(Integer k : keys){
					Map<Integer,CompanyCapacityCell> ceil = compCapacityCell.get(k);
					Set<Integer> itemSets = ceil.keySet();
					for(Integer s : itemSets){
						CompanyCapacityCell c = ceil.get(s);
						if(c != null ){
//							log.info("时段.."+k+"项目"+s);
							if(c.getLimit()!=null)
							Assert.assertEquals(c.getLimit(),periodMap.get(s).get(k).getLimit());
							if(c.getReserveNum()!=null)
							Assert.assertEquals(c.getReserveNum(),periodMap.get(s).get(k).getReserveNum());
							if(c.getUsedNum()!=null)
							Assert.assertEquals(c.getUsedNum(),periodMap.get(s).get(k).getUsedNum());

						}
					}

				}
				//限制项目？
				List<Integer> examItems = CounterChecker.getLimitItem(hospitalId).stream().map(e -> e.getId())
						.collect(Collectors.toList());
				examItems.add(-1);
				for (Integer e : examItems) {
					Map<Integer, CompanyCapacityCell> hMapRes = compCapacityCell.get(e);
					Map<Integer, CompanyCapacityCell> hMapDb = periodMap.get(e);
					if (hMapRes != null && hMapDb != null)
						for (Integer d : dayRangeIds) {
							CompanyCapacityCell hRes = hMapRes.get(d);
							CompanyCapacityCell hDb = hMapDb.get(d);
							Assert.assertEquals(hRes.getReserveNum(), hDb.getReserveNum());
							Assert.assertEquals(hRes.getUsedNum(), hDb.getUsedNum());
							Assert.assertEquals(hRes.getLimit(), hDb.getLimit());
						}
				}
			}
		}
		System.out.println("----------------------获取单位某日预留情况测试End----------------------");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> Json2Map(String str) {

		JSONObject jsonObject = JSONObject.fromObject(str);

		Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);

		/*
		 * for(Entry<String,Object> entry : mapJson.entrySet()){ Object strval1
		 * = entry.getValue(); JSONObject jsonObjectStrval1 =
		 * JSONObject.fromObject(strval1); Map<String, Object>
		 * mapJsonObjectStrval1 = JSONObject.fromObject(jsonObjectStrval1);
		 * System.out.println("KEY:"+entry.getKey()+"  -->  Value:"+entry.
		 * getValue()+"\n"); for(Entry<String, Object>
		 * entry1:mapJsonObjectStrval1.entrySet()){
		 * System.out.println("KEY:"+entry1.getKey()+"  -->  Value:"+entry1.
		 * getValue()+"\n"); }
		 * 
		 * }
		 */
		return mapJson;
	}

	@DataProvider(name = "periodDetal")
	public Iterator<String[]> periodDetal() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/company/periodDetal.csv", 2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
