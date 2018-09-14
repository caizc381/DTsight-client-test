package com.tijiantest.testcase.main.card;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.account.Examiner;
import com.tijiantest.model.account.ExaminerVo;
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
import com.tijiantest.model.account.Account;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class GetBindingCardPageTest extends MainBase {

	@Test(description="绑定实体卡页面",groups={"qa"},dataProvider="getBindingCardPage")
	public void test_01_getBindingCardPage(String... args) throws SqlException{
		String _site = args[1];
		String _siteType =args[2];

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("_site", _site));
		pairs.add(new BasicNameValuePair("_siteType", _siteType));
		
		HttpResult result = httpclient.get(Flag.MAIN,Card_GetBindingCardPage,pairs);
		String body = result.getBody();
		log.info(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Examiner account = JSON.parseObject(JsonPath.read(body, "$.account").toString(),new TypeReference<Examiner>(){});
		
		 
		System.out.println(body);
		
		if (checkdb) {
			ExaminerVo examinerVo = AccountChecker.getExaminerByCustomerId(defaccountId,defHospitalId);
			Assert.assertEquals(account.getCustomerId().intValue(), examinerVo.getCustomerId());

		}
	}
	
	
	@DataProvider(name = "getBindingCardPage")
	public Iterator<String[]> getBindingCardPage() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/card/main/getBindingCardPage.csv", 10);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}
}
