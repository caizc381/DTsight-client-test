package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.MongoDBUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 查询有几个可导订单
 * 位置：CRM->单位体检->单位订单->导出所有到体检软件
 * @author huifang
 *
 */
public class ExportOrderNumTest extends CrmBase{

	@Test(description = "导出所有查询订单数量" ,groups = {"qa"})
	public void test_01_exportOrderNum() throws ParseException {

		int hospitalId = defhospital.getId();
		int companyId = CompanyChecker.getRandomCommonHospitalCompany(hospitalId).getId();
		String startDate = "2015-01-01 00:00:00";
		OrderQueryParams query = new OrderQueryParams();
		query.setHospitalIds(Arrays.asList(hospitalId));
		query.setExamCompanyIds(Arrays.asList(companyId));
		query.setOrderStatuses(Arrays.asList(2));
		query.setIsExport(false);
		query.setAccountTypes(Arrays.asList(1,2,3,5,6,7));
		query.setExamStartDate(simplehms.parse(startDate));
		query.setExportImmediately(false);
		String queryOrderObj = "{\"hospital._id\":{\"$eq\":"+hospitalId+"},\"examCompanyId\":{\"$eq\":"+companyId+"},\"status\":{\"$eq\":2}" +
				",\"isExport\":{\"$eq\":false},\"account.type\":{\"$in\":[1,2,3,5,6,7]},\"examDate\":{\"$ne\":null},\"exportImmediately\":{\"$eq\":false}}";
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("queryOrderObj",queryOrderObj));
		HttpResult result = httpclient.post(Order_ExportOrderNumber, JSON.toJSONString(query));
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("立即统计返回.."+ body);
		int text = Integer.parseInt(JsonPath.read(body,"$.result").toString());
		if(checkmongo) {
			log.info("querySql"+queryOrderObj);
			List<Map<String, Object>> mongoList = MongoDBUtils.query(queryOrderObj,MONGO_COLLECTION);
			log.info("mongo返回.."+mongoList.size());
			Assert.assertEquals(text,mongoList.size());

		}
	}

}

