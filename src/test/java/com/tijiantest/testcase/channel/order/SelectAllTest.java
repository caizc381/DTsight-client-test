package com.tijiantest.testcase.channel.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.CvsFileUtil;

public class SelectAllTest extends ChannelBase {

	@Test(description = "全选", groups = { "qa" }, dataProvider = "listChannelMongoOrders")
	public void test_01_selectAll(String... args) throws ParseException {
		String message = args[16];

		OrderQueryParams orderQueryRequestParams = OrderBase.generateOrderQueryParams(args);

		HttpResult result = httpclient.post(Flag.CHANNEL, Order_SelectAll, JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		List<Integer> ids = JSON.parseArray(body, Integer.class);

		if (checkmongo) {
			List<Map<String, Object>> list = OrderBase.generateMongoResultList(false,args);
			System.out.println(list.size());
			Assert.assertEquals(ids.size(), list.size(),message);
			for (int i = 0; i < ids.size(); i++) {
				Assert.assertEquals(ids.get(i), list.get(i).get("id"),message);
			}
		}
	}

	@DataProvider(name = "listChannelMongoOrders")
	public Iterator<String[]> listChannelMongoOrders() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/listChannelMongoOrders.csv", 15);
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
