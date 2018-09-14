package com.tijiantest.testcase.crm.counter.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.company.HospitalCompanyVO;
import com.tijiantest.model.counter.CompanyCapacityInfo;
import com.tijiantest.testcase.crm.counter.CounterBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class CompanyCapacityInfoTest extends CounterBase {

	int id;
	String companyId;

	@Test(description = "体检中心为体检单位预留信息设置", dataProvider = "config", groups = {
			"qa" }, dependsOnMethods = "test_02_config")
	public void test_01_config(String... args) throws ParseException, IOException, SqlException {

		String aheadDays = args[1];
		boolean canOrder = args[2].equals("true") ? true : false;
		
		int hospitalId = defhospital.getId();
		String promptText = args[4];

		CompanyCapacityInfo companyCapacityInfo = new CompanyCapacityInfo();
		if (!aheadDays.equals("")) {
			companyCapacityInfo.setAheadDays(Integer.valueOf(aheadDays));
		}
		
		companyCapacityInfo.setCanOrder(canOrder);
		companyCapacityInfo.setCompanyId(Integer.valueOf(companyId));
		companyCapacityInfo.setHospitalId(hospitalId);
		companyCapacityInfo.setOldCompanyId(Integer.valueOf(companyId));
		companyCapacityInfo.setPromptText(promptText);
		
		String jsonBody = JSON.toJSONString(companyCapacityInfo);
		HttpResult response = httpclient.post(CountComp_Config, jsonBody);
		waitto(3);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(response.getBody().equals("{}")||response.getBody().equals(""));

		if (checkdb) {
			String sql = "select * from tb_company_capacity_info where company_id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, companyId);
			Assert.assertEquals(list.get(0).get("prompt_text").toString(), promptText);
			Assert.assertEquals(list.get(0).get("ahead_days") != null ? list.get(0).get("ahead_days").toString() : "",
					aheadDays+"");
			Assert.assertEquals(list.get(0).get("can_order"), args[2].equals("true") ? 1 : 0);
		}
	}

	@Test(description = "获取体检中心为体检单位预留信息设置", groups = { "qa" }, dataProvider = "config")
	public void test_02_config(String... args) throws SqlException {
		List<HospitalCompanyVO> hospitalCompanyVOsResult = CompanyChecker.getHospitalCompanyVoResult(defhospital.getId(), httpclient);
		@SuppressWarnings("unused")
		List<HospitalCompanyVO> h = randomList(hospitalCompanyVOsResult,1);
		//companyId = h.get(0).getId()+"";
		companyId = 4405029+"";
		int hospitalId = defhospital.getId();

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("companyId", companyId));
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));

		HttpResult result = httpclient.get(CountComp_Config, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		CompanyCapacityInfo capacityInfo = new CompanyCapacityInfo();
		if(!result.getBody().equals("")){
			capacityInfo = JSON.parseObject(result.getBody(), CompanyCapacityInfo.class);
		}
		//id = capacityInfo.getId();

		if (checkdb) {
			String sql = "SELECT id, company_id, hospital_id, prompt_text, can_order, ahead_days FROM tb_company_capacity_info WHERE hospital_id = ? AND company_id = ?";
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId, companyId);
			if (list!=null && !list.isEmpty()) {
				Assert.assertEquals(capacityInfo.isCanOrder() ? "1" : "0", list.get(0).get("can_order").toString());
				if (capacityInfo.getAheadDays()==null) {
					Assert.assertNull(list.get(0).get("ahead_days"));
				}else {
					Assert.assertEquals(capacityInfo.getAheadDays()+"", list.get(0).get("ahead_days").toString());
				}
				if (capacityInfo.getPromptText()==null) {
					Assert.assertNull(list.get(0).get("prompt_text"));
				}else {
					Assert.assertEquals(capacityInfo.getPromptText(), list.get(0).get("prompt_text").toString());
				}				
			}			
		}
	}

	@DataProvider(name = "config")
	public Iterator<String[]> config() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/counter/company/config_add.csv", 6);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
