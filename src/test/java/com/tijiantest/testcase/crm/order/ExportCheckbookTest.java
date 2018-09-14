package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 订单&用户->订单管理->导出查看
 */
public class ExportCheckbookTest extends CrmBase {

	@Test(description="订单&用户->订单管理-导出查看",groups= {"qa"}, dataProvider = "isExportCheckbookOverRange")
	public void test_crm_exportCheckbook(String ...args) {
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
		orderQueryRequestParams.setOrderStatuses(""+ListUtil.IntegerlistToString(orderStatuses)+"");

		if(!IsArgsNull(settleSignStr)){
			settleSign = Integer.parseInt(settleSignStr);
			orderQueryRequestParams.setSettleSign(settleSign);
		}
		if(!IsArgsNull(showImmediatelyImpOrderStr)){
			showImmediatelyImpOrder = Boolean.parseBoolean(showImmediatelyImpOrderStr);
			orderQueryRequestParams.setShowImmediatelyImpOrder(showImmediatelyImpOrder);
		}

		   List<NameValuePair>  nameValuePairs = MapUtil.parseJSON2NameValuePairs(JSON.toJSONString(orderQueryRequestParams));
			
			HttpResult result = httpclient.post(Order_Crm_ExportCheckbook,nameValuePairs);
			String body = result.getBody();
			log.info(body);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			Assert.assertNotNull(body);
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
