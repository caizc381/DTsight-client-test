package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 查询订单可提前导出时间
 * 位置：CRM->首页->订单查询
 * @author huifang
 *
 */
public class OrderPreExpDayTest extends CrmBase{

	@Test(description = "查看体检中心提前导出时间",groups = {"qa"})
	public void test_01_check_orderPreExpDay(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		HttpResult result = httpclient.get(Order_OrderPreExpDay,params);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info(body);
		int rt = Integer.parseInt(JsonPath.read(body,"$.result").toString());
		if(checkdb){
			Map<String,Object> maps = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.PREVIOUS_EXPORT_DAYS);
			int previous_export_days = Integer.parseInt(maps.get(HospitalParam.PREVIOUS_EXPORT_DAYS).toString());
			Assert.assertTrue(rt == previous_export_days);
		}
	}

}

