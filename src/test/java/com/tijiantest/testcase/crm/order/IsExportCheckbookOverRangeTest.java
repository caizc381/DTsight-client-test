package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

public class IsExportCheckbookOverRangeTest extends CrmBase {

	@Test(description = "导出查看", groups = { "qa" }, dataProvider = "isExportCheckbookOverRange")
	public void test_01_isExportCheckbookOverRange(String... args) throws ParseException {
		String examEndDateStr = args[1];
		String examStartDateStr = args[2];
		String insertEndDateStr = args[3];
		String insertStartDateStr = args[4];
		String hosptialStr = args[5];
		String orderStrs = args[6];
		String settleSignStr = args[7];
		String showImmediatelyImpOrderStr = args[8];
		int hospitalId = -1;
		String examStartDate = null;
		String examEndDate = null;
		String insertStartDate = null;
		String insertEndDate = null;
		boolean showImmediatelyImpOrder = true;
		int  settleSign = -1;
		List<Integer> orderStatuses = Arrays.asList(1,2,3,9,10,11);

		OrderQueryRequestParams orderQueryRequestParams =  new OrderQueryRequestParams();

		if(!IsArgsNull(examStartDateStr)){
			examStartDate = examStartDateStr;
			orderQueryRequestParams.setExamStartDate(examStartDate);
		}
		if(!IsArgsNull(examEndDateStr)){
			examEndDate = examEndDateStr;
			orderQueryRequestParams.setExamEndDate(examEndDate);
		}
		if(!IsArgsNull(insertStartDateStr)){
			insertStartDate = insertStartDateStr;
			orderQueryRequestParams.setInsertStartDate(insertStartDate);
		}
		if(!IsArgsNull(insertEndDateStr)){
			insertEndDate = insertEndDateStr;
			orderQueryRequestParams.setInsertEndDate(insertEndDate);
		}
		if(!IsArgsNull(hosptialStr)){
			hospitalId = Integer.parseInt(hosptialStr);
			orderQueryRequestParams.setHospitalId(hospitalId);
		}
		if(!IsArgsNull(orderStrs)){
			String orders[] = orderStrs.split("#");
			orderStatuses = ListUtil.StringArraysToIntegerList(orders);
		}
		orderQueryRequestParams.setOrderStatuses(""+orderStatuses+"");

		if(!IsArgsNull(settleSignStr)){
			settleSign = Integer.parseInt(settleSignStr);
			orderQueryRequestParams.setSettleSign(settleSign);
		}
		if(!IsArgsNull(showImmediatelyImpOrderStr)){
			showImmediatelyImpOrder = Boolean.parseBoolean(showImmediatelyImpOrderStr);
			orderQueryRequestParams.setShowImmediatelyImpOrder(showImmediatelyImpOrder);
		}

		HttpResult result = httpclient.post(Order_Crm_IsExportCheckbookOverRange,
				JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		int count = JsonPath.read(body, "$.count");
		boolean isOverRange = JsonPath.read(body, "$.isOverRange");

		if(checkmongo) {
			String querySql = "{";
			if(hospitalId != -1)
				querySql += "\"orderHospital._id\":"+hospitalId+"";
			if(orderStatuses != null && orderStatuses.size() > 0)
				querySql += ",\"status\":{$in:["+ListUtil.IntegerlistToString(orderStatuses)+"]}";
			if(settleSign != -1){
				if(settleSign == 3)//已结算
					querySql += ",\"settleSign\":{$in:[4,5]}";
				if(settleSign == 4)//未结算
					querySql += ",\"settleSign\":{$in:[3,6,null]}";
				if(settleSign == 2)//结算中
					querySql += ",\"settleSign\":{$in:[2]}";
			}
			querySql+= "}";
			log.info("querySql"+querySql);
			List<Map<String, Object>> mongoList = null;
			if(insertStartDate != null && insertEndDate != null)
				mongoList = MongoDBUtils.queryByPageAndInsertTime(querySql, "insertTime", -1, 0,
						5000, insertStartDate,insertEndDate,MONGO_COLLECTION); //5000条限制
			if(examStartDate != null && examEndDate != null)
				mongoList = MongoDBUtils.queryByPageAndExameDate(querySql, "insertTime", -1, 0,
						5000, examStartDate,examEndDate,MONGO_COLLECTION);  //5000条限制

			Assert.assertEquals(count,mongoList.size());
			if (count>5000) {
				Assert.assertTrue(isOverRange);
			}else{
				Assert.assertFalse(isOverRange);
			}
		}
	}

	@DataProvider(name = "isExportCheckbookOverRange")
	public Iterator<String[]> isExportCheckbookOverRange() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/isExportCheckbookOverRange.csv", 16);
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
