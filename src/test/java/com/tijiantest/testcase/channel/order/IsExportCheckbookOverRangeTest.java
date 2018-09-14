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
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.testcase.channel.ChannelBase;
import com.tijiantest.util.CvsFileUtil;

public class IsExportCheckbookOverRangeTest extends ChannelBase {

	@Test(description = "导出查看", groups = { "qa" }, dataProvider = "isExportCheckbookOverRange")
	public void test_01_isExportCheckbookOverRange(String... args) throws ParseException {
		OrderQueryParams orderQueryRequestParams = OrderBase.generateOrderQueryParams(args);
		HttpResult result = httpclient.post(Flag.CHANNEL, Order_IsExportCheckbookOverRange,
				JSON.toJSONString(orderQueryRequestParams));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		int count = JsonPath.read(body, "$.count");
		boolean isOverRange = JsonPath.read(body, "$.isOverRange");

		if (checkmongo) {
			List<Map<String, Object>> list = OrderBase.generateMongoResultList(args);
			Assert.assertEquals(count, list.size());
			
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
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/channel/isExportCheckbookOverRange.csv", 16);
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
