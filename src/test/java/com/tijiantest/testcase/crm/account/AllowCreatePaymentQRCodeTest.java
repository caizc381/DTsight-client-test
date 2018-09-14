package com.tijiantest.testcase.crm.account;

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

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

/**
 *  查看客户经理管理是否有付款二维码
 *  位置：crm——>维护后台——>客户经理管理
 *  @author  honyan
 * 
 */

public class AllowCreatePaymentQRCodeTest extends CrmBase{
	
	
	@Test(description = "查看客户经理管理是否有付款二维码",groups = {"qa"})
	public void test_01_paymentorder_success() throws ParseException, IOException, SqlException{
		
	
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId",defhospital.getId()+""));
		
		
		HttpResult response = httpclient.get(Manager_allowCreatePaymentQRCode,params);
		String body = response.getBody();
		System.out.println(body);
		
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);  
//		String result1=JsonPath.read(response.getBody(), "$.result").toString();
		boolean ret = JsonPath.read(response.getBody(), "$.result");
//		Result result = JSON.parseObject(response.getBody(),Result.class);
		//System.out.print(result1);
		if(checkdb){	    
		    	String sql = "SELECT * FROM tb_hospital_settings WHERE (ali_pay = 1 or weixin_pay =1) and hospital_id = ? ";
	    	
		    	List<Map<String,Object>> relist = DBMapper.query(sql, defhospital.getId());
		 
		 boolean result;   
//		 if (resultEquals"true" != null) {
		  if(ret)
		    	Assert.assertEquals(relist.size(),1);
		    	
//		  }
		 
		 else {
//			 if(result="false" != null){
			  Assert.assertEquals(relist.size(),0);
		  }
		}
//		 Assert.assertEquals(result,result1);
		  		
		
	}
	
}
