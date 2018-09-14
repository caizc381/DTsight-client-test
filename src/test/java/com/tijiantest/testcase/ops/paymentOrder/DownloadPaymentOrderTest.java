package com.tijiantest.testcase.ops.paymentOrder;

import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.paymentOrder.PaymentOrderQueryVO;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.SqlException;
import com.tijiantest.util.pagination.Page;
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
 * OPS->用户&订单->收款订单管理
 * 操作:导出查看
 * @author huifang
 *
 */
public class DownloadPaymentOrderTest extends OpsBase{

	@Test(description = "导出查看",groups = {"qa"},dataProvider = "ops_downloadPaymentOrder")
	public void test_01_paymentOrder_download(String ...args) throws SqlException {
		String organizationStr = args[1];
		String provinceStr = args[2];
		String cityStr = args[3];
		String districtStr = args[4];
		String startTimeStr = args[5];
		String endTimeStr = args[6];
		String statusLists = args[7];
		String settlementStr = args[8];
		String name = args[9];
		String pageSize = args[10];
		int organizationId = -1;
		int province=-1;
		int city=-1;
		int district=-1;
		List<Integer> statusList = new ArrayList<>();
		int settlementStatus = -1;
		String start_time = null;
		String end_time = null;
		PaymentOrderQueryVO dto = new PaymentOrderQueryVO();

		List<NameValuePair> pairs = new ArrayList<>();
		if(!IsArgsNull(organizationStr)){
			organizationId = Integer.parseInt(organizationStr);
			pairs.add(new  BasicNameValuePair("organizationId",organizationStr));
		}
		if(!IsArgsNull(provinceStr)){
			province = Integer.parseInt(provinceStr);
			pairs.add(new  BasicNameValuePair("provinceId",provinceStr));
		}
		if(!IsArgsNull(cityStr)){
			city = Integer.parseInt(cityStr);
			pairs.add(new  BasicNameValuePair("cityId",cityStr));
		}
		if(!IsArgsNull(districtStr)){
			district = Integer.parseInt(districtStr);
			pairs.add(new  BasicNameValuePair("districtId",districtStr));
		}

		if(!IsArgsNull(startTimeStr)){
			start_time = startTimeStr;
			pairs.add(new  BasicNameValuePair("startTime",startTimeStr));
		}
		if(!IsArgsNull(endTimeStr)){
			end_time = endTimeStr;
			pairs.add(new  BasicNameValuePair("endTime",endTimeStr));
		}
		if(!IsArgsNull(statusLists)){
			String[] statusArray = statusLists.split("#");
			statusList = ListUtil.StringArraysToIntegerList(statusArray);
			pairs.add(new  BasicNameValuePair("statusList",ListUtil.IntegerlistToString(statusList)));
		}

		if(!IsArgsNull(settlementStr)){
			settlementStatus = Integer.parseInt(settlementStr);
			pairs.add(new  BasicNameValuePair("settlementStatus",settlementStr));
		}
		if(!IsArgsNull(name)){
			pairs.add(new  BasicNameValuePair("name",name));
		}

		HttpResult response = httpclient.get(Flag.OPS,OPS_DownLoadPaymentOrder, pairs);
		log.debug(response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		Assert.assertFalse(body.contains("response"));
	}

	@DataProvider
	public Iterator<String[]> ops_downloadPaymentOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/ops_downloadPaymentOrder.csv", 16);
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
