package com.tijiantest.testcase.ops.settlement;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.model.settlement.TradeConsumeQuotaDetail;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * OPS->消费额度
 * 
 * 上传凭证
 * @author huifang
 *
 */
public class UploadImageTest extends OpsBase{

	private String fileName = "./csv/settlement/healthlogo.png";
	@Test(description = "上传凭证" , groups = {"qa"})
	public void test_01_UploadImage() throws ParseException, IOException {
		File file = new File(fileName);
		HttpResult response = httpclient.upload(Flag.OPS,OPS_UploadImage,null,file);
		System.out.println(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Assert.assertTrue(body.contains("test-i.oss-cn-shanghai.aliyuncs.com"));
	}
	

	  
}