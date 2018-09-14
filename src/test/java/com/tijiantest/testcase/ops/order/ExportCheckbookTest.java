package com.tijiantest.testcase.ops.order;

import java.util.ArrayList;
import java.util.List;

import com.tijiantest.testcase.ops.OpsBase;
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

public class ExportCheckbookTest extends OpsBase {

	@Test(description="订单管理-导出查看",groups= {"qa"},dependsOnGroups= {"ops_queryOrder"})
	public void test_exportCheckbook() {
		//先获取订单
		JSONArray queryOrderList = QueryOrderTest.queryOrderList;
		
		//取一个已预约状态的订单
		Integer orderId=0;
		
		for (int i = 0; i < queryOrderList.size(); i++) {
			JSONObject job = queryOrderList.getJSONObject(i);
			System.out.println(job.getIntValue("id")+"状态："+job.getString("status"));
			if (job.getString("status") ==OrderStatus.ALREADY_BOOKED.toString()) {
				orderId = job.getIntValue("id");
			}
		}
		if (orderId!=0) {
			List<NameValuePair> pairs = new ArrayList<>();
			pairs.add(new BasicNameValuePair("orderIds[]", orderId+""));
			System.out.println("导出订单ID:" + orderId);
			
			HttpResult result = httpclient.get(Flag.OPS,OpsOrder_ExportCheckbook,pairs);
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);			
		}
	}
}
