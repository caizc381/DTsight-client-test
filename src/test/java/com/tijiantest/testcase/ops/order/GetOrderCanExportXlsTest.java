package com.tijiantest.testcase.ops.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.testcase.ops.OpsBase;

/**
 * 订单管理 - 导出xls
 * 
 * @author admin
 *
 */
public class GetOrderCanExportXlsTest extends OpsBase {

	@Test(description = "订单管理 - 导出XLS", groups = { "qa" }, dependsOnGroups = { "ops_queryOrder" })
	public void test_getOrderCanExportXls() {
		// 先获取订单
		JSONArray queryOrderList = QueryOrderTest.queryOrderList;
		if (queryOrderList.size() == 0) {
			log.info("没有可导出为xls的订单！！！！");
			return;
		}

		// 取第一个订单，导出xls
		JSONObject o = queryOrderList.getJSONObject(0);
		String hospitalId = o.getString("hospitalId");
		String orderId = o.getString("id");

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", hospitalId));
		pairs.add(new BasicNameValuePair("orderIds", "[" + orderId + "]"));
		System.out.println("导出xls的订单ID:" + orderId);

		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_GetOrderCanExportXls, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
	}
}
