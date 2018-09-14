package com.tijiantest.testcase.crm.account.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.CrmMediaBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 卡代预约显示支付方式
 * 位置：CRM->用户查询->卡代预约
 * @author huifang
 *
 */
public class ShowPayWayTest extends CrmBase{

	
	@Test(description = "卡代预约显示支付方式" ,dataProvider="showPayWay" ,groups = {"qa"})
	public void test_01_showPayWay(String ... args) throws ParseException, IOException, SqlException{
		String hospitalStr = args[1];
		String examDateStr = args[2];
		String personPayPriceStr = args[3];
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(!IsArgsNull(hospitalStr)){
			params.add(new BasicNameValuePair("hospitalId", defhospital.getId()+""));
		}
		if(!IsArgsNull(examDateStr)){
			if(examDateStr.equalsIgnoreCase("today"))
				params.add(new BasicNameValuePair("examDate",sdf.format(new Date())+""));
			else{
				Date date = DateUtils.offsetDestDay((new Date()),3);
				params.add(new BasicNameValuePair("examDate",sdf.format(date)+""));
			}
		}
		if(!IsArgsNull(examDateStr)){
			int personPayPrice = Integer.parseInt(personPayPriceStr);
			params.add(new BasicNameValuePair("personalPayPrice",personPayPrice+""));
		}
		
		HttpResult result = httpclient.get(AccountQuery_ShowPayWay,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		
		String jbody = result.getBody();
		log.info(jbody);
		boolean  offlinePay =  Boolean.parseBoolean(JsonPath.read(jbody,"$.offlinePay").toString());
		boolean onlinePay = Boolean.parseBoolean(JsonPath.read(jbody,"$.onlinePay").toString());

		if(checkdb){
			Map<String,Object> maps = HospitalChecker.getHospitalSetting(defhospital.getId(),HospitalParam.NEED_LOCAL_PAY,HospitalParam.ALI_PAY,HospitalParam.WEIXIN_PAY,HospitalParam.ACCEPT_OFFLINE_PAY);
			boolean needLocalPay = ((int)maps.get(HospitalParam.NEED_LOCAL_PAY) == 1)? true:false;
			boolean aliPay = ((int)maps.get(HospitalParam.ALI_PAY) == 1)? true:false;
			boolean weixinPay = ((int)maps.get(HospitalParam.WEIXIN_PAY) == 1)? true:false;
			boolean accept_offline_pay = ((int)maps.get(HospitalParam.ACCEPT_OFFLINE_PAY) == 1)? true:false;
			if(examDateStr.equalsIgnoreCase("today")){
				Assert.assertTrue(offlinePay);
				if(aliPay || weixinPay)
					Assert.assertTrue(onlinePay);
				else
					Assert.assertFalse(onlinePay);
			}else{
				if(accept_offline_pay)
					Assert.assertTrue(offlinePay);
				else
					Assert.assertFalse(offlinePay);
				Assert.assertFalse(onlinePay);
			}
		}
		

	}
	
	@DataProvider
	public Iterator<String[]> showPayWay(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/account/query/showPayWay.csv",7);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

