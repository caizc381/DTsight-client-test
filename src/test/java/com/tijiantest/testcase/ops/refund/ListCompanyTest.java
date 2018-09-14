package com.tijiantest.testcase.ops.refund;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import bsh.ParseException;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.company.BaseCompany;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.SqlException;

/**
 * 指定体检中心,查询单位列表(OPS->退款审批-单位表)
 * @author huifang
 *
 */
public class ListCompanyTest extends OpsBase{

	private int hospitalId = 1;
	
	@Test(description="列举指定医院的单位列表")
	public void test_01_listRefundApplyRecord() throws ParseException, SqlException{
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId+""));
		
		HttpResult result = httpclient.get(Flag.OPS,ListCompany,pairs);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		List<BaseCompany> retComplList = JSON.parseArray(result.getBody(),BaseCompany.class);
		if(checkdb){
		  List<HospitalCompany> hospitalCompanys =  CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId,"id",false);
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
