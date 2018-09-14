package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.CrmDeepBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * 单位体检->单位订单
 * 设置单位导出时间（深对接）
 */
public class ExportOrderJobTest extends CrmDeepBase {

	private int getCompanyId = 0;

	@Test(description = "设置单位导出时间", groups = { "qa" },dataProvider = "exportOrderJob")
	public void test_01_exportOrderJob(String ...args) throws ParseException, SqlException {
		int hospitalId = defDeepHosptailId;
		List<HospitalCompany> hcList = CompanyChecker.getHospitalCompanyByOrganizationId(hospitalId,null,false);
		int index = RandomUtils.nextInt(hcList.size());
		int companyId = hcList.get(index).getId();
		String exportDate = args[3];
		String autoExport = args[4];
		getCompanyId = companyId;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId",defDeepHosptailId+""));
		params.add(new BasicNameValuePair("companyId",companyId+""));
		params.add(new BasicNameValuePair("exportDate",exportDate));
		params.add(new BasicNameValuePair("autoExport",autoExport));
		HttpResult result = deepClient.post(Order_ExportOrderJob, params);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(body.equals("{}") || body.equals(""));

		if (checkdb) {
		   String sql = "select * from tb_company_export_order_job where hospital_id = "+hospitalId + " and company_id ="+companyId ;
		   List<Map<String,Object>> dblist = DBMapper.query(sql);
		   Assert.assertEquals(dblist.size(),1);
		   Map<String,Object> map = dblist.get(0);
		   Assert.assertEquals(Integer.parseInt(map.get("auto_export").toString()) == 1 ? true:false,Boolean.parseBoolean(autoExport));
		   Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);//未执行
			Assert.assertEquals(map.get("export_date").toString(),exportDate);

		}
	}

	@Test(description = "查看单位导出时间", groups = { "qa" })
	public void test_02_read_exportOrderJob() throws ParseException, SqlException {
		int hospitalId = defDeepHosptailId;
		int companyId = getCompanyId;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId",defDeepHosptailId+""));
		params.add(new BasicNameValuePair("companyId",companyId+""));
		HttpResult result = deepClient.get(Order_ExportOrderJob, params);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	    int retHospitalId = JsonPath.read(body,"$.hospitalId");
	    int retCompanyId = JsonPath.read(body,"$.companyId");
	    boolean retAutoExport = JsonPath.read(body,"$.autoExport");
	    boolean retStatus = JsonPath.read(body,"$.status");
	    long exportDate = JsonPath.read(body,"$.exportDate");

		if (checkdb) {
			String sql = "select * from tb_company_export_order_job where hospital_id = "+hospitalId + " and company_id ="+companyId ;
			List<Map<String,Object>> dblist = DBMapper.query(sql);
			Assert.assertEquals(dblist.size(),1);
			Map<String,Object> map = dblist.get(0);
			Assert.assertEquals(Integer.parseInt(map.get("auto_export").toString()) == 1 ? true:false,retAutoExport);
			Assert.assertEquals(Integer.parseInt(map.get("status").toString())==1?true:false,retStatus);
			Assert.assertEquals(sdf.parse(map.get("export_date").toString()).getTime(),exportDate);

		}
	}

	@DataProvider
	public Iterator<String[]> exportOrderJob(){
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/exportOrderJob.csv",22);
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
