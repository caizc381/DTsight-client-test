package com.tijiantest.testcase.ops.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONReader;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.model.order.OrderQueryParams;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.db.MongoDBUtils;

public class QueryAllOrderInfoTest extends OpsBase {

	@SuppressWarnings("deprecation")
	@Test(description = "全选订单", groups = { "qa" })
	public void test_01_queryAllOrderInfo() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String examStartDate = simpleDateFormat.format(new Date()) + " 00:00:00";
		String examEndDate = simpleDateFormat.format(new Date()) + " 23:59:59";

		int currentPage = 1;
		OrderQueryParams pairs = new OrderQueryParams();
		// List<NameValuePair> pairs = new ArrayList<>();
		pairs.setExamStartDate(simplehms.parse(examStartDate));
		pairs.setExamEndDate(simplehms.parse(examEndDate));

		HttpResult result = httpclient.post(Flag.OPS, OpsOrder_QueryAllOrderInfo, JSON.toJSONString(pairs));
		String body = result.getBody();
		int code = result.getCode();
		log.info("body..." + body);
		if (code == HttpStatus.SC_OK) {
			List<Order> orders = JSON.parseArray(body, Order.class);
			if (checkdb) {
				List<Map<String, Object>> list = MongoDBUtils.queryByPageAndExameDate("{'isDeleted':{$exists:false}}", "insertTime", -1, 0, null,
						examStartDate, examEndDate, MONGO_COLLECTION);
				System.out.println(list.size());
				Assert.assertEquals(orders.size(), list.size());

				for (int i = 0; i < orders.size(); i++) {
					Order order = orders.get(i);
					Map<String, Object> map = list.get(i);
//					log.info("数据库订单号"+map.get("id").toString()+"接口返回"+order.getId());
					// account
					Object obj = map.get("orderAccount");
					if (obj != null) {
						JSONObject jso = JSONObject.parseObject(obj.toString());
						if(order.getOrderAccount().getId() !=null)
							Assert.assertEquals(order.getOrderAccount().getId(), jso.get("_id"));
						else
							Assert.assertNull(jso.get("_id"));
						Assert.assertEquals(order.getOrderAccount().getName(), jso.getString("name"));
						Assert.assertEquals(order.getOrderAccount().getIdCard(), jso.getString("idCard"));
						Assert.assertEquals(order.getOrderAccount().getMobile(), jso.getString("mobile"));
						if(order.getOrderAccount().getStatus()!=null)
							Assert.assertEquals(order.getOrderAccount().getStatus(), jso.get("status"));
						else
							Assert.assertNull(jso.getString("status"));
						if (order.getOrderAccount().getType() != null)
							Assert.assertEquals(order.getOrderAccount().getType(), jso.get("type"));
						else
							Assert.assertNull(order.getOrderAccount().getType());
					}

					Assert.assertEquals(order.getId(), map.get("id"));
					Assert.assertEquals(order.getIsExport() ? "true" : "false", map.get("isExport").toString());
					// Assert.assertEquals(order.getSource(), map.get("source"));
					Assert.assertEquals(order.getStatus(), map.get("status"), order.getId() + "");

				}
			}
		} else if (code == HttpStatus.SC_BAD_REQUEST) {
			String text = JsonPath.read(body, "$.text");
			Assert.assertTrue(text.contains("全选订单数量不能多于1000条"));
			if (checkdb) {
				List<Map<String, Object>> list = MongoDBUtils.queryByPageAndExameDate("{}", "insertTime", -1, 0, null,
						examStartDate, examEndDate, MONGO_COLLECTION);
				System.out.println(list.size());
				Assert.assertTrue(list.size() > 1000);
			}


		}
	}

}
