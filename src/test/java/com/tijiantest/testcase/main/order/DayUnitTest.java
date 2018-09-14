package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.counter.DateUnit;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class DayUnitTest extends MainBase {

	@Test(description = "C端获取可选择的时间段", groups = { "qa" }, dataProvider = "mobileMealListPage")
	public void test_01_dayUnit(String... args) throws SqlException {
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
		Date theFirstDay = DateUtils.theFirstDayOfMonth(0);
		Date theLastDay = DateUtils.theLastDayOfMonth(0);
		int offset = DateUtils.dayForWeek(theLastDay);
		if(offset < 7){
			theLastDay = DateUtils.offsetDestDay(theLastDay,7-offset);
		}
		Date theStartDay =  DateUtils.offsetDestDay(theLastDay,-34);
		List<NameValuePair> p = new ArrayList<>();
		p.add(new BasicNameValuePair("examCompanyId", companyId));
		p.add(new BasicNameValuePair("mealId", String.valueOf(mealId)));
		p.add(new BasicNameValuePair("_site", _site));
		p.add(new BasicNameValuePair("start",sdf.format(theStartDay)));
		p.add(new BasicNameValuePair("end", sdf.format(theLastDay)));
		p.add(new BasicNameValuePair("_siteType", _siteType));
		p.add(new BasicNameValuePair("isInLocation","false"));
		p.add(new BasicNameValuePair("infoDay",theFirstDay.toGMTString()));

		result = httpclient.post(Flag.MAIN, Order_DateUnit, p);

		body = result.getBody();
		log.info("日期排版.."+body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		List<DateUnit> list = JSON.parseObject(body, new TypeReference<List<DateUnit>>() {
		});
		Assert.assertEquals(list.size(),35);
		for(DateUnit unit : list){
			if(unit.getDate().compareTo(new Date()) == -1)//小于当前日期
				Assert.assertFalse(unit.isEnable());
			else{
				//网上个人预约单位
				HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(1,hospitalId);
				//提前几天可约
				Map<String,Object> maps = HospitalChecker.getHospitalSetting(hospitalId,HospitalParam.PREVIOUS_BOOK_DAYS);
				List<Integer> dayRangeList = HospitalChecker.getHospitalPeriodRangeLists(hospitalId);
				int previous_book_days = (int)maps.get(HospitalParam.PREVIOUS_BOOK_DAYS);
				if(unit.getDate().compareTo(DateUtils.offDate(previous_book_days)) == 1){
					//如果是休息日
					if(CounterChecker.isRestDay(hospitalId,unit.getDate(),dayRangeList)){
						log.info("当前休息日期..."+unit.getDate());
						Assert.assertFalse(unit.isEnable());
					}
						//如果是预约满
					else if(CounterChecker.getDateBookableFromStartDate(unit.getDate(),unit.getDate(),hc.getId(),hospitalId) == null)
						Assert.assertFalse(unit.isEnable());
					  else{
						log.info("当前可约日期..."+unit.getDate());
						Assert.assertTrue(unit.isEnable());
					}
				}
				//当天是否可约,根据距离，设置来 toTo
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
