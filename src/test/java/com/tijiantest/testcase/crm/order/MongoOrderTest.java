package com.tijiantest.testcase.crm.order;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

public class MongoOrderTest extends CrmBase {

	@Test(description = "根据订单查询条件对象查询订单信息", groups = { "qa" }, dataProvider = "mongoOrder")
	public void test_01_mongoOrder(String... args) throws SqlException, ParseException {
		String rowCount = args[1];
		String currentPage = args[2];
		String pageSize = args[3];
		String gte = args[4];
		String lte = args[5];
		Boolean isExport = Boolean.valueOf(args[6]);
		String invoiceManagerQueryFlag = args[7];
		String accountRelationName = args[8];
		String where = "";

		@SuppressWarnings("serial")
		List<Integer> status = new ArrayList<Integer>() {
			{
				add(OrderStatus.ALREADY_BOOKED.intValue());
				add(OrderStatus.EXAM_FINISHED.intValue());
				add(OrderStatus.NOT_EXAM.intValue());
				add(OrderStatus.PART_BACK.intValue());
			}
		};



		OrderQueryParams params = new OrderQueryParams();
		Page page = new Page(Integer.parseInt(currentPage),Integer.parseInt(pageSize));
		page.setRowCount(Integer.parseInt(rowCount));
		params.setPage(page);
		if(gte != null && !gte.equals(""))
			params.setExamStartDate(DateUtils.offsetDestDay(DateUtils.getStartTime(new Date()),Integer.parseInt(gte)));
		if(lte != null && !lte.equals(""))
			params.setExamEndDate(DateUtils.offsetDestDay(DateUtils.getStartTime(new Date()),Integer.parseInt(lte)));
		params.setHospitalIds(Arrays.asList(defhospital.getId()));
		params.setOrderStatuses(status);
		params.setIsExport(isExport);
		if(accountRelationName!=null && !accountRelationName.equals(""))
			params.setAccountName(accountRelationName);
		if(invoiceManagerQueryFlag != null && !invoiceManagerQueryFlag.equals("")){
			if(invoiceManagerQueryFlag.equals("committed"))
					where = "if(this.invoiceApply && this.orderExtInfo.selfMoney>0){ return (this.invoiceApply.approver != this.invoiceApply.proposer && this.invoiceApply.status != 2)}";
			if(invoiceManagerQueryFlag.equals("uncommitted"))
				where = "if(this.orderExtInfo.selfMoney>0){ return (this.invoice && (this.invoice.status == 1||this.invoice.status == 3))||!this.invoice ? true : false }";
			params.setInvoiceManagerQueryFlag(invoiceManagerQueryFlag);

		}
//		List<NameValuePair> pairs = new ArrayList<>();
//		pairs.add(new BasicNameValuePair("rowCount", rowCount));
//		pairs.add(new BasicNameValuePair("currentPage", currentPage));
//		pairs.add(new BasicNameValuePair("pageSize", pageSize));

		HttpResult result = httpclient.post(Flag.CRM, Order_MongoOrder, JSON.toJSONString(params));
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		log.info("body..."+body);
		JSONObject jsonArrayList = JSON.parseObject(body);
		String stringArray = jsonArrayList.get("records").toString();
		JSONArray records = JSONArray.parseArray(stringArray);

		if (checkmongo) {
			String moSql = JSON.toJSONString(OrderBase.generateQueryOrderObj(gte, lte, defhospital.getId(), status,
					isExport, where, accountRelationName));
			log.info(moSql);
			List<Map<String, Object>> mongoList = MongoDBUtils.queryByPage(moSql, "insertTime", -1, 0,
					Integer.valueOf(pageSize), MONGO_COLLECTION);

//			Assert.assertEquals(mongoList.size(), records.size());
			for (int i = 0; i < records.size(); i++) {
				JSONObject jsonObject = (JSONObject) records.get(i);
				Map<String, Object> mogoMap = mongoList.get(i);
				log.info("order_id:"+jsonObject.get("id")+"mogoMap-orderId"+mogoMap.get("id"));
				Assert.assertEquals(((JSONObject) jsonObject.get("orderAccount")).get("name"),
						((BasicDBObject) mogoMap.get("orderAccount")).get("name"));
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("gender"),
						((BasicDBObject) mogoMap.get("examiner")).get("gender"));
				Assert.assertEquals(((JSONObject) jsonObject.get("examiner")).get("idCard"),
						((BasicDBObject) mogoMap.get("examiner")).get("idCard"));
				Assert.assertEquals(jsonObject.get("orderPrice"), mogoMap.get("orderPrice"));
				Assert.assertEquals(jsonObject.getJSONObject("orderExtInfo").get("selfMoney"), ((BasicDBObject)mogoMap.get("orderExtInfo")).get("selfMoney"));
				if (jsonObject.get("invoice") != null) {
					Assert.assertEquals(((JSONObject) jsonObject.get("invoice")).get("money"),
							((BasicDBObject) mogoMap.get("invoice")).get("money"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoice")).get("orderId"),
							((BasicDBObject) mogoMap.get("invoice")).get("orderId"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoice")).get("title"),
							((BasicDBObject) mogoMap.get("invoice")).get("title"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoice")).get("orderId"),
							((BasicDBObject) mogoMap.get("invoice")).get("orderId"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoice")).get("operator"),
							((BasicDBObject) mogoMap.get("invoice")).get("operator"));

				}

				if (jsonObject.get("invoiceApply") != null) {
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("addressId"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("addressId"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("amount"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("amount"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("applyAmount"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("applyAmount"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("approver"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("approver"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("content"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("content"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("deliveryType"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("deliveryType"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("id"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("_id"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("orderId"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("orderId"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("proposer"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("proposer"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("remark"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("remark"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("status"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("status"));
					Assert.assertEquals(((JSONObject) jsonObject.get("invoiceApply")).get("title"),
							((BasicDBObject) mogoMap.get("invoiceApply")).get("title"));

					Object userAddress = ((JSONObject) jsonObject.get("invoiceApply")).get("userAddress");
					Object mogoUserAddress = ((BasicDBObject) mogoMap.get("invoiceApply")).get("userAddress");
					if (userAddress != null) {
						Assert.assertEquals(((JSONObject) userAddress).get("accountId"),
								((BasicDBObject) mogoUserAddress).get("accountId"));
						Assert.assertEquals(((JSONObject) userAddress).get("addressAlias"),
								((BasicDBObject) mogoUserAddress).get("addressAlias"));
						Assert.assertEquals(((JSONObject) userAddress).get("addressId"),
								((BasicDBObject) mogoUserAddress).get("addressId"));
						Assert.assertEquals(((JSONObject) userAddress).get("addressee"),
								((BasicDBObject) mogoUserAddress).get("addressee"));
						Assert.assertEquals(((JSONObject) userAddress).get("detailedAddress"),
								((BasicDBObject) mogoUserAddress).get("detailedAddress"));
						Assert.assertEquals(((JSONObject) userAddress).get("email"),
								((BasicDBObject) mogoUserAddress).get("email"));
						Assert.assertEquals(((JSONObject) userAddress).get("id"),
								((BasicDBObject) mogoUserAddress).get("id"));
						Assert.assertEquals(((JSONObject) userAddress).get("mobile"),
								((BasicDBObject) mogoUserAddress).get("mobile"));
						Assert.assertEquals(((JSONObject) userAddress).get("phoneNumber"),
								((BasicDBObject) mogoUserAddress).get("phoneNumber"));
						Assert.assertEquals(((JSONObject) userAddress).get("remark"),
								((BasicDBObject) mogoUserAddress).get("remark"));
					}
				}
			}
		}
	}

	@DataProvider(name = "mongoOrder")
	public Iterator<String[]> mongoOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/mongoOrder.csv", 18);
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
