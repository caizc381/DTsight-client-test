package com.tijiantest.testcase.channel.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.channel.MongoOrderVO;
import com.tijiantest.model.resource.meal.ExamItemSnap;
import com.tijiantest.util.db.SqlException;

public class OrderExamItemsTest extends OrderDetailBase {

	@Test(description = "获取订单单项id数组", groups = { "qa" }, dependsOnGroups = { "channel_listOrder" })
	public void test_01_orderExamItems() throws SqlException {

		System.out.println("===========  获取订单单项id数组  开始Start   =======================");
		// 先获取订单列表
		List<MongoOrderVO> mongoOrderVOs = ListChannelMongoOrdersTest.listOrder;
		if (mongoOrderVOs.size() == 0) {
			log.info("订单列表为空,请先下单");
			return;
		}
		// 随机取列表中的一个orderId
		Random random = new Random();
		int index = random.nextInt(mongoOrderVOs.size()) % (mongoOrderVOs.size() + 1);
		int orderId = mongoOrderVOs.get(index).getId();

		System.out.println("订单orderId=" + orderId + "   index=" + index);

		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("orderId", orderId + ""));
		HttpResult result = httpclient.get(Flag.CHANNEL, Channel_OrderExamItems, pairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		List<Integer> items = JSON.parseArray(body, Integer.class);

		// items 按照id 排序
		Collections.sort(items, new Comparator<Integer>() {
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});

		if (checkdb) {
			// 获取订单单项详情 并按照ID排序
			Map<Integer, ExamItemSnap> map = new TreeMap<Integer, ExamItemSnap>();
			Map<Integer, ExamItemSnap> snapMap = getExamItemsDetail(orderId);
			Assert.assertEquals(items.size(), snapMap.size());

			map.putAll(snapMap);
			int i = 0;
			// 处理套餐内删除的单项 typeToMeal - 1：套餐内项目 2：套餐内删除项 3：新增项
			for (Integer key : map.keySet()) {
				int typeToMeal = map.get(key).getTypeToMeal();
				if (typeToMeal == 2) {
					map.remove(key);
					continue;
				}
				Assert.assertEquals(items.get(i++), key);
			}
		}
		System.out.println("===========  获取订单单项id数组  结束END   =======================");
	}
}
