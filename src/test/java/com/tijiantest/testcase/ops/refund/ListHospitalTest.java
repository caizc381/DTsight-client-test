package com.tijiantest.testcase.ops.refund;


import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import bsh.ParseException;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.HospitalBref;
import com.tijiantest.model.hospital.HospitalVO;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.SqlException;

/**
 * 列举医院列表(OPS->退款审批->医院列表)搜索框
 * @author huifang
 *
 */
public class ListHospitalTest extends OpsBase{

	@Test(description="列举所有医院列表")
	public void test_01_listRefundApplyRecord() throws ParseException, SqlException{
		
		HttpResult result = httpclient.get(Flag.OPS,ListHospital);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		List<HospitalBref> hospitalList = JSON.parseArray(result.getBody(),HospitalBref.class);
		if(checkdb){
		 List<HospitalVO> dbHospitalList =  HospitalChecker.getAllOrganizations();
		 Assert.assertEquals(hospitalList.size(),dbHospitalList.size());
		 for(int i=0;i<hospitalList.size();i++){
			 log.debug("体检中心.."+hospitalList.get(i).getId());
			 Assert.assertEquals(hospitalList.get(i).getId(),dbHospitalList.get(i).getId());
			 Assert.assertEquals(hospitalList.get(i).getName(),dbHospitalList.get(i).getName());
			 Assert.assertEquals(hospitalList.get(i).getOrgType(),dbHospitalList.get(i).getOrganizationType());
			 Assert.assertEquals(hospitalList.get(i).getPinyin(),dbHospitalList.get(i).getPinyin());
		 }
		}
		
		
			
	}
	

	
}
