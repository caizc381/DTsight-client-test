package com.tijiantest.testcase.crm.counter.company;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CounterChecker;
import com.tijiantest.model.counter.CompanyCapacityInfo;
import com.tijiantest.testcase.crm.counter.CounterBase;

public class GetCompanyConfigTest extends CounterBase{

	
	@Test(description = "查询单位人数控制的配置信息",groups={"qa"})
	public void test_01_getCompanyConfig(){
		int companyId = defnewcompany.getId();
		int hospitalId = defhospital.getId();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("companyId", String.valueOf(companyId)));
		params.add(new BasicNameValuePair("hospitalId", String.valueOf(hospitalId)));
		
		HttpResult response = httpclient.get(CountComp_Config, params);

		String body=response.getBody();
		System.out.println(body);
		if (body.equals("")) {
			return;
		}
		
		CompanyCapacityInfo cciRes= JSON.parseObject(response.getBody(),new TypeReference<CompanyCapacityInfo>(){});
		
		if(checkdb){		
			CompanyCapacityInfo cciDB = new CompanyCapacityInfo();
			cciDB = CounterChecker.getCapacitySetting(companyId,hospitalId);
			
			Assert.assertEquals(cciRes.getAheadDays(), cciDB.getAheadDays());
			Assert.assertEquals(cciRes.isCanOrder(), cciDB.isCanOrder());
//			Assert.assertEquals(cciRes.getId(), cciDB.getId());//2017-08-31 countrefactor2New版本去除
			Assert.assertEquals(cciRes.getPromptText(), cciDB.getPromptText());
		}
		
		
	}
}
