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
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.testcase.ops.OpsBase;

public class GetOrderCanExportTest extends OpsBase {
	@Test(description = "获取可以导出至体检软件的订单", groups = { "qa" }, dependsOnGroups = { "queryOrder_account" })
	public void test_getOrderCanExport() {
		JSONArray recordList  = QueryOrderTest.recordList;

		if (recordList.size() == 0) {
			log.info("用户订单列表无数据！！！！");
			return;
		}

		JSONObject vo = null;

		for (int i = 0; i < recordList.size(); i++) {
//			OrderListVO o = recordList.get(i);
			JSONObject o = recordList.getJSONObject(i);
			if (o.getString("isExport").equals("true")&& o.getString("status").equals(OrderStatus.ALREADY_BOOKED)) {
				vo = o;
			}
//			if (!o.getIsExport() && o.getStatus() == Integer.valueOf(OrderStatus.ALREADY_BOOKED + "")) {
//				vo = o;
//			}
//
			if (vo != null) {
				break;
			}
		}

		if (vo == null) {
			log.info("所有订单都不会出现‘导出至内网’按钮！！！！");
			return;
		}
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("hospitalId", vo.getString("hospitalId") + ""));
		pairs.add(new BasicNameValuePair("orderIds", "[" + vo.getString("id") + "]"));

		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_GetOrderCanExport, pairs);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

	}
}
