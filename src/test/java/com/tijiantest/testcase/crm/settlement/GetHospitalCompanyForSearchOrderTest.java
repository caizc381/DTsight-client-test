package com.tijiantest.testcase.crm.settlement;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import bsh.ParseException;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.company.BaseCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.SqlException;

/**
 * 指定体检中心,查询单位列表(CRM结算管理->结算批次->体检单位）
 * @author huifang
 *
 */
public class GetHospitalCompanyForSearchOrderTest extends CrmBase{

	private int hospitalId = defhospital.getId();
	
	@Test(description="列举指定医院的单位列表")
	public void test_01_listRefundApplyRecord() throws ParseException, SqlException{
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		
		HttpResult result = httpclient.get(GetHospitalCompanyForSearchOrder,pairs);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		List<BaseCompany> retComplList = JSON.parseArray(result.getBody(),BaseCompany.class);
		Collections.sort(retComplList, new Comparator<BaseCompany>() {
			@Override
			public int compare(BaseCompany t1, BaseCompany t2) {
				return t1.getId() - t2.getId();
			}
		});
		if(checkdb){
		  List<HospitalCompany> hospitalCompanys =  CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId,"id",true);
		  Assert.assertEquals(retComplList.size(),hospitalCompanys.size());
		  for(int i=0; i< hospitalCompanys.size();i++){
			  log.debug("expect:"+retComplList.get(i).getId()+"actual.."+hospitalCompanys.get(i).getId());
			  Assert.assertEquals(retComplList.get(i).getName(),hospitalCompanys.get(i).getName());
			  Assert.assertEquals(retComplList.get(i).getId(),hospitalCompanys.get(i).getId());
			  Assert.assertEquals(retComplList.get(i).getPinyin(),hospitalCompanys.get(i).getPinyin());
		  }
			
		}	
	
	}
	
}
