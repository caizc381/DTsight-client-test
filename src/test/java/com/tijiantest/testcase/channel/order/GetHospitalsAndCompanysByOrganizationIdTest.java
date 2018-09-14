package com.tijiantest.testcase.channel.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.channel.HospitalsCompanysManagersVO;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class GetHospitalsAndCompanysByOrganizationIdTest extends ChannelBase {

	@Test(description = "获取渠道商分配的体检中心", groups = { "qa" })
	public void test_01_getHospitalsAndCompanysByOrganizationId() throws SqlException {
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("channelId", defChannelid + ""));

		HttpResult result = httpclient.get(Flag.CHANNEL, Order_GetHospitalsAndCompanysByOrganizationId, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();

		HospitalsCompanysManagersVO hospitalsCompanysManagersVO = JSON.parseObject(body,
				HospitalsCompanysManagersVO.class);
		List<Hospital> hospitals = hospitalsCompanysManagersVO.getHospitals();
		Collections.sort(hospitals, new Comparator<Hospital>() {
			@Override
			public int compare(Hospital o1, Hospital o2) {
				return o1.getId() - o2.getId();
			}
		});

		List<ChannelCompany> companies = hospitalsCompanysManagersVO.getCompanies();
		Collections.sort(companies, new Comparator<ChannelCompany>() {
			@Override
			public int compare(ChannelCompany o1, ChannelCompany o2) {
				return o1.getId() - o2.getId();
			}
		});

		List<Account> managers = hospitalsCompanysManagersVO.getManagers();
		Collections.sort(managers, new Comparator<Account>() {
			@Override
			public int compare(Account o1, Account o2) {
				return o1.getId() - o2.getId();
			}
		});

		if (checkdb) {
			// hospital
			String hospitalSql = "select * from tb_organization_hospital_relation where organization_id=?  and status = 1 order by hospital_id asc ";

			List<Map<String, Object>> hospitalList = DBMapper.query(hospitalSql, defChannelid);

			// 如果hospitalList什么都没有，表示支持所有体检中心；如果hospitalId=-1，表示不支持体检中心
			if (hospitalList.size() == 0) {
				// 支持所有体检中心
				hospitalSql = "SELECT * FROM tb_hospital WHERE  organization_type=? and `enable`=1 ";
				hospitalList = DBMapper.query(hospitalSql, OrganizationTypeEnum.HOSPITAL.getCode());
			} else if (hospitalList.size() == 1 && hospitalList.get(0).get("hospital_id").toString().equals("-1")) {
				// 不支持体检中心
				Assert.assertEquals(hospitalsCompanysManagersVO.getHospitals().size(), 0);
				hospitalList = new ArrayList<>();// 防止下面的执行不通过
			} else {//支持部分体检中心，必须在平台显示且可用
				// （以下为本渠道商支持的体检中心列表，若体检中心不可用或不在平台显示，则渠道同样不支持）
				List<Map<String, Object>> resultList = new ArrayList<>();
				for (int i = 0; i < hospitalList.size(); i++) {
					String hospitalsql = "select * from tb_hospital where id=? and enable = 1 ";
					List<Map<String, Object>> list = DBMapper.query(hospitalsql,
							hospitalList.get(i).get("hospital_id"));
					if(list.size()>0)
						resultList.add(list.get(0));
				}
				hospitalList = resultList;
			}
			Assert.assertEquals(hospitals.size(), hospitalList.size());

			for (int i = 0; i < hospitalsCompanysManagersVO.getHospitals().size(); i++) {
				Hospital hospital = hospitalsCompanysManagersVO.getHospitals().get(i);
				Map<String, Object> map = hospitalList.get(i);
				Assert.assertEquals(hospital.getId(), map.get("id"));
				Assert.assertEquals(hospital.getName(), map.get("name"));
			}

			// company
			String companySql = "select id,gmt_created, gmt_modified, name, platform_company_id, organization_id, organization_name, discount, settlement_mode,"
					+ " send_exam_sms, send_exam_sms_days, pinyin, is_deleted, tb_exam_company_id "
					+ " from  tb_channel_company where organization_id = ? and is_deleted = 0";
			List<Map<String, Object>> companyList = DBMapper.query(companySql, defChannelid);
			Assert.assertEquals(hospitalsCompanysManagersVO.getCompanies().size(), companyList.size());
			for (int i = 0; i < hospitalsCompanysManagersVO.getCompanies().size(); i++) {
				ChannelCompany company = hospitalsCompanysManagersVO.getCompanies().get(i);
				Map<String, Object> map = companyList.get(i);
				Assert.assertEquals(company.getName(), map.get("name"));
				Assert.assertEquals(company.getPinyin(), map.get("pinyin"));
				Assert.assertEquals(company.getDiscount(), map.get("discount"));
				Assert.assertEquals(company.getId(), map.get("id"));
				Assert.assertEquals(company.getOrganizationId(), map.get("organization_id"));
				Assert.assertEquals(company.getOrganizationName(), map.get("organization_name"));
				Assert.assertEquals(company.getPlatformCompanyId(), map.get("platform_company_id"));
				Assert.assertEquals(company.getSendExamSms() ? 1 : 0, map.get("send_exam_sms"));
				Assert.assertEquals(company.getSendExamSmsDays(), map.get("send_exam_sms_days"));
				Assert.assertEquals(company.getTbExamCompanyId(), map.get("tb_exam_company_id"));
			}

			// managers
			String managerSql = "select a.id, a.name, a.mobile, a.idcard, a.status, a.type, a.employee_id, a.create_time, a.update_time, a.id_type, a.system from tb_account a LEFT JOIN tb_hospital_manager_relation m on m.manager_id=a.id where m.hospital_id=? and m.is_belong=1";
			List<Map<String, Object>> managerList = DBMapper.query(managerSql, defChannelid);
			Assert.assertEquals(hospitalsCompanysManagersVO.getManagers().size(), managerList.size());
			for (int i = 0; i < hospitalsCompanysManagersVO.getManagers().size(); i++) {
				Account manager = hospitalsCompanysManagersVO.getManagers().get(i);
				Map<String, Object> map = managerList.get(i);
				Assert.assertEquals(manager.getName(), map.get("name"));
				Assert.assertEquals(manager.getId(), map.get("id"));
				Assert.assertEquals(manager.getIdCard(), map.get("idcard"));
				Assert.assertEquals(manager.getStatus(), map.get("status"));
			}
		}
	}
}
