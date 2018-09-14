package com.tijiantest.testcase.ops.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.ops.order.OrderOrganizationListTest;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 * 根据体检中心ID，获取单位列表
 * 
 * @author admin
 *
 */
public class AllCompaniesTest extends OpsBase {

	@Test(description = "根据体检中心ID，获取单位列表", groups = {"qa" }, dataProvider = "allCompanies", dependsOnGroups = {"orderOrganizationList"})
	public void test_allCompanies(String... args) throws SqlException {
		String hasGuestCompany = args[1];
		List<Hospital> hospitals = OrderOrganizationListTest.hospitals;
		if (hospitals.size() == 0) {
			log.error("没有体检中心！！！");
			return;
		}

		// 随机获取一个体检中心
		Random random = new Random();
		int index = random.nextInt(hospitals.size()) % (hospitals.size() + 1);
		int hospitalId = hospitals.get(index).getId();
		System.out.println("体检中心ID ： "+ hospitalId);

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hasGuestCompany", hasGuestCompany));
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId + ""));

		HttpResult result = httpclient.get(Flag.OPS, OpsOrder_AllCompanies, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		List<HospitalCompany> hospitalCompanies = JSON.parseArray(body, HospitalCompany.class);

		if (checkdb) {
			String sql = "select id,gmt_created,gmt_modified,name,platform_company_id,organization_id,organization_name,discount,show_report,settlement_mode,his_name,advance_export_order,send_exam_sms,"
					+" send_exam_sms_days,pinyin,is_deleted,tb_exam_company_id,examination_address,examreport_interval_time,refund_rule,reserve_company_pay,remind_examreport_sms_template_id"
					+" from tb_hospital_company where organization_id=? and is_deleted=0 order by id desc";
			List<Map<String, Object>> list = DBMapper.query(sql, hospitalId);
			
			Assert.assertEquals(hospitalCompanies.size(), list.size());
			
			for (int i = 0; i < hospitalCompanies.size(); i++) {
				HospitalCompany hospitalCompany = hospitalCompanies.get(i);
				Map<String, Object> map = list.get(i);
				Assert.assertEquals(hospitalCompany.getName(), map.get("name").toString());
				Assert.assertEquals(hospitalCompany.getId(), map.get("id"));
				Assert.assertEquals(hospitalCompany.getOrganizationId(), map.get("organization_id"));
				Assert.assertEquals(hospitalCompany.getOrganizationName(), map.get("organization_name").toString());
				Assert.assertEquals(hospitalCompany.getPinyin(), map.get("pinyin"));
			}
		}

	}

	@DataProvider(name = "allCompanies")
	public Iterator<String[]> allCompanies() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/company/ops/allCompanies.csv", 7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
