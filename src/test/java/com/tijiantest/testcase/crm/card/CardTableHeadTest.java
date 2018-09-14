package com.tijiantest.testcase.crm.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.organization.OrganizationTypeEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * 卡头信息
 * CRM->单位体检->发卡记录
 * @author huifang
 *
 */
public class CardTableHeadTest extends CrmBase{


	@Test(description = "卡头信息",groups = {"qa"})
	public void test_01_cardTableHead() throws ParseException, IOException, SqlException{
		int companyId = CompanyChecker.getRandomCommonHospitalCompany(defhospital.getId()).getId();
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("organizationType", OrganizationTypeEnum.HOSPITAL.getCode()+""));
		params.add(new BasicNameValuePair("newCompanyId",companyId+""));
		params.add(new BasicNameValuePair("tableHeads","position,group,department"));
	    HttpResult response = httpclient.get(Card_CardTableHead,params);
	    //assert
	    Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		String body = response.getBody();
		log.info(body);
		List<String> groupList = JSON.parseArray(JsonPath.read(body,"$.group").toString(),String.class);
		List<String> departmentList = JSON.parseArray(JsonPath.read(body,"$.department").toString(),String.class);
		List<String> positionList = JSON.parseArray(JsonPath.read(body,"$.position").toString(),String.class);

		if(checkdb){
			Map<String,List<String>> dbMap = AccountChecker.getCompanyHeadTables(companyId,defaccountId);
			List<String> igroupList = dbMap.get("igroup");
			Assert.assertEquals(groupList,dbMap.get("igroup"));
			Assert.assertEquals(departmentList,dbMap.get("department"));
			Assert.assertEquals(positionList,dbMap.get("position"));
		}
	}
}
