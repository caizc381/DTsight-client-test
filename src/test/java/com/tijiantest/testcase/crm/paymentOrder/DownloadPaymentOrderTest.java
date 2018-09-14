package com.tijiantest.testcase.crm.paymentOrder;

import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 医院CRM->订单&用户->收款订单
 * 操作:导出查看
 * @author huifang
 *
 */
public class DownloadPaymentOrderTest extends CrmBase{

	@Test(description = "导出查看",groups = {"qa"},dataProvider = "downloadPaymentOrder")
	public void test_01_paymentOrder_download(String ...args) throws SqlException {
		String startTime = args[2];
		String endTime = args[3];
		String statusStr = args[4];
		String settlementStr = args[5];
		String manageIdStr = args[6];
		int hosptialId = defhospital.getId();
		int managerId = defaccountId;
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new  BasicNameValuePair("organizationId",hosptialId+""));
		if(!IsArgsNull(startTime))
			pairs.add(new  BasicNameValuePair("startTime",startTime+""));
		if(!IsArgsNull(endTime))
			pairs.add(new  BasicNameValuePair("endTime",endTime+""));
		if(!IsArgsNull(statusStr))
			pairs.add(new  BasicNameValuePair("status",statusStr));
		if(!IsArgsNull(settlementStr))
			pairs.add(new  BasicNameValuePair("settlementStatus",settlementStr));
		if(!IsArgsNull(manageIdStr))
			pairs.add(new  BasicNameValuePair("managerId",manageIdStr));

		HttpResult response = httpclient.get(Payment_DownloadPaymentOrder, pairs);
		log.debug(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Assert.assertFalse(body.contains("response"));
	}

	@DataProvider
	public Iterator<String[]> downloadPaymentOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/downloadPaymentOrder.csv", 16);
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
