package com.tijiantest.testcase.main.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.counter.DayRange;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class DayRangeTest extends MainBase {

	@Test(description = "C端获取体检时间段", groups = { "qa" }, dataProvider = "mobileMealListPage")
	public void test_01_dayRange(String... args) throws SqlException {
		// 先获取meal 列表，然后取第一个

		String cardId = args[1];
		int hospitalId = defHospitalId;
		String currentPage = args[3];
		String pageSize = args[4];
		String rowCount = args[5];
		String _site = HospitalChecker.getSiteByOrganizationId(defHospitalId);
		String _siteType = args[7];
		String companyId = args[8];//为空

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("cardId", cardId));
		pairs.add(new BasicNameValuePair("hospitalId", String.valueOf(hospitalId)));
		pairs.add(new BasicNameValuePair("cuArrentPage", currentPage));
		pairs.add(new BasicNameValuePair("pageSize", pageSize));
		pairs.add(new BasicNameValuePair("rowCount", rowCount));
		pairs.add(new BasicNameValuePair("_site", _site));
		pairs.add(new BasicNameValuePair("_siteType", _siteType));
		Map<String, String> params = new HashMap<>();
		params.put("companyId", companyId);

		HttpResult result = httpclient.post(Flag.MAIN, Mobile_MobileMealListPage, pairs, params);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<Meal> mealPageView = JSON.parseObject(JsonPath.read(body, "$.mealPageView.records").toString(),
				new TypeReference<List<Meal>>() {
				});

		if (mealPageView.size() == 0) {
			return;
		}

		//取第一个ID
		int mealId = mealPageView.get(0).getId();

		List<NameValuePair> p = new ArrayList<>();
		p.add(new BasicNameValuePair("examCompanyId", companyId));
		p.add(new BasicNameValuePair("mealId", String.valueOf(mealId)));
		p.add(new BasicNameValuePair("_site", _site));
		p.add(new BasicNameValuePair("_siteType", _siteType));

		result = httpclient.post(Flag.MAIN, Order_dayRange, p);

		body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<DayRange> list = JSON.parseObject(body, new TypeReference<List<DayRange>>() {
		});

		if (checkdb) {
			String sql = " select id, name, internal_use_period from tb_hospital_period_settings where hospital_id = ?";
			List<Map<String, Object>> dayRangeList = DBMapper.query(sql, defHospitalId);
			Assert.assertEquals(list.size(), dayRangeList.size());
			for (int i = 0; i < list.size(); i++) {
				Assert.assertEquals(list.get(i).getId(), dayRangeList.get(i).get("id"));
				Assert.assertEquals(list.get(i).getName(), dayRangeList.get(i).get("name"));
				Assert.assertEquals(list.get(i).getInternalUsePeriod() ? 1 : 0,
						dayRangeList.get(i).get("internal_use_period"));
			}
		}
	}
	
	@DataProvider(name = "mobileMealListPage")
	public Iterator<String[]> mobileMealListPage() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/page/mobile/mobileMealListPage.csv", 4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	  
}
