package com.tijiantest.testcase.crm.order.ordermanage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.order.BatchOrderBody;
import com.tijiantest.model.order.BatchOrderProcess;
import com.tijiantest.model.order.BatchOrderProcessRecord;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.crm.order.BatchOrderTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;

/**
 * CRM订单管理部分接口：批量发送短信/获取可以导出至体检软件的订单/导出至体检软件/获取可以恢复未导的订单/恢复未导
 * 
 * @author XpCHen
 *
 */
public class OrderBatchOperationTest extends CrmBase {
	private static List<Order> orderList = new ArrayList<Order>();
	private static List<Integer> orderIdList = new ArrayList<Integer>();
	List<Integer> accountIntList = new ArrayList<Integer>();
	private static List<Meal> mediaList = new ArrayList<Meal>();
	private int mealid = 0;
	private int companyId = 0;

	@Test(description = "CRM下单", dataProvider = "createOrder", groups = { "qa" })
	public void test_01_createOrder(String... args) throws ParseException, IOException, SqlException {
		String idCards = args[1].replace("#", ",");
		// MyHttpClient crmClient = new MyHttpClient();
		Meal sankeCrmCommonMeal = ResourceChecker.createMeal(httpclient, defSKXCnewcompany.getId(), "散客manage测试通用套餐", 2,defhospital.getId());
		mediaList.add(sankeCrmCommonMeal);
		mealid = sankeCrmCommonMeal.getId();
		String examedate = args[3];
		String accountnames = "\'" + args[4].replace("#", "\',\'") + "\'";
		String group = args[5];
		companyId = defSKXCnewcompany.getId();//Integer.parseInt(args[6]);

//		Integer organizationType = HospitalChecker.getOrganizationType(defhospital.getId());
		// step1:导入用户
		AccountChecker.uploadAccount(httpclient, companyId, CrmBase.defhospital.getId(), group,
				"./csv/order/manage/crm_batchOrder_1.xlsx",AddAccountTypeEnum.idCard);
		// step2:获取导入用户accountid
		String sql1 = "SELECT  DISTINCT a.id FROM tb_account a,tb_examiner r WHERE r.id_card in (" + idCards
				+ ") AND a.id = r.customer_id " + "AND r.igroup = \'" + group + "\' AND r.name in (" + accountnames
				+ ") AND r.manager_id = ?";
		log.info("sql:" + sql1);
		List<Map<String, Object>> aclist = DBMapper.query(sql1, CrmBase.defaccountId);
		for (Map<String, Object> al : aclist) {
			accountIntList.add((Integer) al.get("id"));
		}

		log.info("accountlist:" + accountIntList);
		Meal meal = ResourceChecker.getMealInfo(mealid);
		BatchOrderBody batchBody = new BatchOrderBody(CrmBase.defhospital.getId(),
				CrmBase.defhospital.getName(), meal.getId(), meal.getPrice(), meal.getGender(), meal.getName(),
				companyId, defSKXCnewcompany.getName(),accountIntList, examedate);
		String jbody = JSON.toJSONString(batchBody);

		// step3:批量下单
		HttpResult response = httpclient.post(Order_BatchOrder, jbody);

		// Assert
		Assert.assertEquals(response.getCode() , HttpStatus.SC_OK);
		Integer processId = JsonPath.read(response.getBody(),"$.result");

		OrderChecker.waitBatchOrderProc(httpclient,processId.intValue());

		// check database
		if (checkdb) {
			for (Order order : checkOrder(accountIntList)) {
				orderList.add(order);
				Assert.assertTrue(order.getStatus() == OrderStatus.ALREADY_BOOKED.intValue()
						|| order.getStatus() == OrderStatus.ALREADY_PAY.intValue());
				if (checkmongo) {
					List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}",
							MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
				}
			}
			SortOrderById(orderList); // 排序

		}
	}

	@Test(description = "批量发送短信", dependsOnMethods = "test_01_createOrder", groups = { "qa" })
	public void test_02_batchSendMsg_success() {
		String msg = "测试订单管理批量发送短信";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("msg", msg));
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		// params.add(new BasicNameValuePair("orderIds",
		// "["+orderList.get(0).getId()+"]"));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}
		HttpResult response = httpclient.post(Message_BatchSendMsg, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(), "{}");
	}

	@Test(description = "获取可以导出至体检软件的订单", dependsOnMethods = "test_02_batchSendMsg_success", groups = { "qa" })
	public void test_03_getOrderCanExport_success() throws SqlException {
		// 设置体检中心为自动导出
		String sql = "update tb_hospital_settings set export_with_xls=1 WHERE hospital_id=" + defhospital.getId() + "";
		DBMapper.update(sql);
		if (checkdb) {
			String sql1 = "SELECT export_with_xls from tb_hospital_settings WHERE hospital_id=" + defhospital.getId()
					+ "";
			List<Map<String, Object>> list1 = DBMapper.query(sql1);
			Assert.assertEquals(list1.get(0).get("export_with_xls").toString(), "1");
		}

		// 获取可以导出至体检软件的订单
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}

		HttpResult response = httpclient.post(Order_GetOrderCanExport, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		log.info("GETORDERCANEXPORT body...." + body);
		int num = JsonPath.read(body, "$num");
		Assert.assertEquals(num, orderList.size());

		JSONObject JsonList = JSON.parseObject(body);
		String orders = JsonList.get("orderIds").toString();
		JSONArray JsonArrayList = JSONArray.parseArray(orders);
		Assert.assertEquals(JsonArrayList.size(), orderList.size());
		List<Integer> ilist = ListUtil.SortJSONIntegerArrayById(JsonArrayList); // 排序
		log.info("--接口返回订单列表:" + ilist);
		for (int i = 0; i < orderList.size(); i++) {
			int orderId = ilist.get(i).intValue();
			log.info("orderId:" + orderId);
			log.info("orderlist:" + orderList.get(i).getId());
			Assert.assertEquals(orderId, orderList.get(i).getId());
		}
	}

	@Test(description = "CRM->首页->订单查看->恢复未导（订单是已预约状态）",dependsOnMethods = "test_03_getOrderCanExport_success",groups = {"qa"})
	public void test_04_orderFoNotExport(){
		List<Integer> paramOrderList = new ArrayList<Integer>();
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				paramOrderList.add(order.getId());
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("doAll",false);
		jsonObject.put("orderIdList",paramOrderList);
		HttpResult result = httpclient.post(Order_OrderForNotExport,JSON.toJSONString(jsonObject));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_BAD_REQUEST);
		String body = result.getBody();
		log.info(body);
		String code = JsonPath.read(body,"$.code").toString();
		String text = JsonPath.read(body,"$.text").toString();
		Assert.assertEquals(code,"EX_1_2_ORDER_03_01_002");
		Assert.assertTrue(text.contains("批量恢复订单未导状态，成功")||text.contains("失败"));
	}

	@Test(description = "导出至体检软件", dependsOnMethods = "test_03_getOrderCanExport_success", groups = { "qa" })
	public void test_04_batchExportOrder_success() throws SqlException {
		String sql = "update tb_hospital_settings set open_queue=1,open_print_exam_guide=1 WHERE hospital_id=" + defhospital.getId() + "";
		DBMapper.update(sql);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}
		HttpResult response = httpclient.post(Order_BatchExportOrder, params);
		log.info("导出至体检软件..."+response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"对接服务com.mytijian.mediator.order.inner.service.MediatorExportOrderService是否开启，请确认");
		Assert.assertEquals(response.getBody(), "{}");

	}
	@Test(description = "打印导检单,后端接口已经废除",dependsOnMethods = "test_04_batchExportOrder_success",groups = {"qa"},enabled = false)
	public void test_04_printAndQueue(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				NameValuePair nvp = new BasicNameValuePair("orderIds[]",
						order.getId()+"");
				params.add(nvp);
			}
		}
		params.add(new BasicNameValuePair("joinQueue","true"));
		HttpResult response = httpclient.post(Order_PrintAndQueue, params);
		log.info("打印导检单..."+response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"打印导检单"+response.getBody());
		Assert.assertEquals(response.getBody(), "{}");
	}

	@Test(description = "获取可以恢复未导的订单", dependsOnMethods = "test_04_batchExportOrder_success", groups = { "qa" })
	public void test_05_getOrderCanUnexport() throws SqlException {
		for (Order order : orderList) {
			// 把订单改为已导出
			String sql = "update tb_order set is_export = 1 where id = " + order.getId() + "";
			DBMapper.update(sql);
			waitto(3);
			String query_sql = "{'id':" + order.getId() + "}";
			String update_sql = "{$set:{'isExport':true}}";
			MongoDBUtils.updateMongo(query_sql, update_sql, MONGO_COLLECTION);

			// 验证订单是否修改成功
			if (checkdb) {
				waitto(mysqlWaitTime);
				String sql1 = "select is_export from tb_order where id =" + order.getId() + "";
				List<Map<String, Object>> list1 = DBMapper.query(sql1);
				Assert.assertEquals(list1.get(0).get("is_export").toString(), "1");
			}
			if (checkmongo) {
				waitto(mongoWaitTime);
				String sql2 = "{'id':" + order.getId() + "},{'isExport':1}";
				List<Map<String, Object>> list2 = MongoDBUtils.query(sql2, MONGO_COLLECTION);
				Assert.assertEquals(list2.get(0).get("isExport").toString(), "true");
			}
		}

		/***** 获取可以恢复未导的订单 *****/
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}
		HttpResult response = httpclient.post(Order_getOrderCanUnexport, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);

		String body = response.getBody();
		int num1 = JsonPath.read(body, "$exportFailedNum");
		int num2 = JsonPath.read(body, "$exportedNum");
		Assert.assertEquals(num1, 0);
		Assert.assertEquals(num2, orderList.size());

		JSONObject JsonList = JSON.parseObject(body);
		String orders = JsonList.get("orderIds").toString();
		JSONArray JsonArrayList = JSONArray.parseArray(orders);
		List<Integer> ilist = ListUtil.SortJSONIntegerArrayById(JsonArrayList); // 排序
		for (int i = 0; i < JsonArrayList.size(); i++) {
			Assert.assertEquals(ilist.get(i).intValue(), orderList.get(i).getId());
		}

	}

	@Test(description = "批量恢复未导", dependsOnMethods = "test_05_getOrderCanUnexport", groups = { "qa" })
	public void test_06_changeToUnExport() throws SqlException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}
		HttpResult response = httpclient.post(Order_batchChangeOrderToUnExport, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getBody(), "{}");

		for (Order order : orderList) {
			if (checkdb) {
				waitto(mysqlWaitTime);
				String sql1 = "select is_export from tb_order where id =" + order.getId() + "";
				List<Map<String, Object>> list1 = DBMapper.query(sql1);
				Assert.assertEquals(list1.get(0).get("is_export").toString(), "0");
			}
			if (checkmongo) {
				waitto(mongoWaitTime);
				String sql2 = "{'id':" + order.getId() + "},{'isExport':1}";
				List<Map<String, Object>> list2 = MongoDBUtils.query(sql2, MONGO_COLLECTION);
				Assert.assertEquals(list2.get(0).get("isExport").toString(), "false");
			}
		}
	}

	@Test(description = "先导出再调用另外一个恢复未导的接口（/orderForNoExport）",groups = {"qa"},dependsOnMethods ="test_06_changeToUnExport" )
	public void test_07_orderForNotExport() throws SqlException {
		String sql = "update tb_hospital_settings set export_with_xls=1 WHERE hospital_id=" + defhospital.getId() + "";
		DBMapper.update(sql);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		List<Integer> paramOrderList = new ArrayList<Integer>();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orderList.size() != 0) {
			for (Order order : orderList) {
				orderIds = orderIds + order.getId() + ",";
				paramOrderList.add(order.getId());
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}
		HttpResult response = httpclient.post(Order_OrderInfoForExportXls, params);
		log.info("导出至体检软件..."+response.getBody());
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK,"对接服务com.mytijian.mediator.order.inner.service.MediatorExportOrderService是否开启，请确认");
		for(Order order : orderList){
			Assert.assertTrue(response.getBody().contains(order.getId()+""));
		}
		params.add(new BasicNameValuePair("readOnly","false"));
		response = httpclient.post(Order_ExportOrderXls, params);
		Assert.assertEquals(response.getCode(),HttpStatus.SC_OK,"接口返回.."+response.getBody());

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("doAll",false);
		jsonObject.put("orderIdList",paramOrderList);
		HttpResult result = httpclient.post(Order_OrderForNotExport,JSON.toJSONString(jsonObject));
		String body = result.getBody();
		log.info(body);
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		Assert.assertTrue(body.equals("")||body.equals("{}"),"返回.."+body);

		if(checkdb){
			for(Order order : orderList){
				Order newOrder = OrderChecker.getOrderInfo(order.getId());
				Assert.assertFalse(newOrder.getIsExport());
				if(checkmongo){
					List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}",
							MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					Assert.assertFalse(Boolean.parseBoolean(list.get(0).get("isExport").toString()));
				}
			}

		}

	}
	@Test(description = "自选日期订单受manage设置限制", groups = { "qa" }, dependsOnGroups = "crm_selfExamDate")
	public void test_08_selfExamDateOrderExport() throws SqlException {
		// 先设为“无体检日期订单导入”不支持
		String sql = "update tb_hospital_settings set export_with_no_exam_date=0 where hospital_id=?";
		DBMapper.update(sql, defhospital.getId());
		List<Order> orders = BatchOrderTest.selfExamDateOrders;
		
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order t1, Order t2) {
				return t1.getId() - t2.getId();
			}
		});
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//判断订单状态
		for(Order paramOrder : orders){
			if(paramOrder.getStatus() == OrderStatus.SITE_PAY.intValue()){
				params.clear();
				params.add(new BasicNameValuePair("orderId", paramOrder.getId() + ""));
				HttpResult ret = httpclient.post(Order_NEEDLOCALPAY_V2, params);
				Assert.assertEquals(ret.getCode(),HttpStatus.SC_OK);
				log.info(paramOrder.getId()+"订单状态.."+OrderChecker.getOrderInfo(paramOrder.getId()).getStatus());
			}
		}
		// 获取可以导出至体检软件的订单
		params.clear();
		params.add(new BasicNameValuePair("hospitalId", defhospital.getId() + ""));
		String orderIds = "";
		if (orders.size() != 0) {
			for (Order order : orders) {
				orderIds = orderIds + order.getId() + ",";
			}
			NameValuePair nvp = new BasicNameValuePair("orderIds",
					"[" + orderIds.substring(0, orderIds.length() - 1) + "]");
			params.add(nvp);
		}

		HttpResult response = httpclient.post(Order_GetOrderCanExport, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		String body = response.getBody();
		int num = JsonPath.read(body, "$num");
		Assert.assertEquals(num, 0);
		JSONObject JsonList = JSON.parseObject(body);
		String ids = JsonList.get("orderIds").toString();
		Assert.assertEquals(ids, "[]");

		// 设为“无体检日期订单导入”支持
		sql = "update tb_hospital_settings set export_with_no_exam_date=1 where hospital_id=?";
		DBMapper.update(sql, defhospital.getId());
		response = httpclient.post(Order_GetOrderCanExport, params);
		Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
		body = response.getBody();

		log.info("GETORDERCANEXPORT body...." + body);
		num = JsonPath.read(body, "$num");
		Assert.assertEquals(num, orders.size());

		JsonList = JSON.parseObject(body);
		ids = JsonList.get("orderIds").toString();
		JSONArray JsonArrayList = JSONArray.parseArray(ids);
		Assert.assertEquals(JsonArrayList.size(), orders.size());
		List<Integer> ilist = ListUtil.SortJSONIntegerArrayById(JsonArrayList); // 排序
		log.info("--接口返回订单列表:" + ilist);
		System.out.println("-------------------ilist : ----------------------");
		for (int i = 0; i < ilist.size(); i++) {
			System.out.println("订单ID:" + ilist.get(i));
		}

		System.out.println("--------------------orders:-----------------------");
		for (int i = 0; i < orders.size(); i++) {
			System.out.println("order id:" + orders.get(i).getId());
		}

		for (int i = 0; i < orders.size(); i++) {
			int orderId = ilist.get(i).intValue();
			log.info("orderId:" + orderId);
			log.info("orderlist:" + orders.get(i).getId());
			Assert.assertEquals(orderId, orders.get(i).getId());
		}
	}

	// @Test(description="撤销订单和移除用户",dependsOnMethods =
	// "test_06_changeToUnExport", groups = {"qa"})
	@AfterClass(alwaysRun = true)
	public void doAfter() throws SqlException {
		/***** 撤销订单 ******/
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		log.info("orderList:" + orderList);
		for(Order order : orderList){
			orderIdList.add(order.getId());
		}
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient,orderIdList,false,true,true);

		/***** 移除用户 ******/
		String acStr = "";
		for (Integer ai : accountIntList) {
			acStr = acStr + ai + ",";
		}
		int organizationId = defhospital.getId();
		int organizationType = HospitalChecker.getOrganizationType(organizationId);

		params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("accountIds", acStr.substring(0, acStr.length() - 1)+""));
		params.add(new BasicNameValuePair("newCompanyId", companyId+""));
		params.add(new BasicNameValuePair("organizationType", organizationType+""));
		HttpResult delete = httpclient.post(Account_RemoveCustomer, params);
		
		// assert
		Assert.assertEquals(delete.getCode(), HttpStatus.SC_OK);
		Assert.assertEquals(delete.getBody(), "{\"result\": " + accountIntList.size() + "}");

		/***** 删除套餐 *****/
		String mealId = null;
		for (int i = 0; i < mediaList.size(); i++) {
			mealId = mediaList.get(i).getId().toString();
			HttpResult response = httpclient.get(Meal_DeleteMeal, mealId);
			System.out.println("response of delete:......" + response.getBody());
			Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
			log.info("id为" + mealId + "的套餐已经删除");
		}
	}

	@DataProvider
	public Iterator<String[]> createOrder() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/manage/crm_batchOrder_2.csv", 10);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	public List<Order> checkOrder(List<Integer> accountlist) {
		List<Order> retlist = new ArrayList<Order>();
		String accounts = "(" + ListUtil.IntegerlistToString(accountlist) + ")";
		waitto(mysqlWaitTime);
		String sqlStr = "select id,status from tb_order where account_id in " + accounts
				+ "  and status = 2  order by  insert_time desc ,id limit 3";

		log.info("sql:" + sqlStr);
		try {
			List<Map<String, Object>> list = DBMapper.query(sqlStr);
			for (Map<String, Object> m : list) {
				Order order = new Order();
		    	order.setId(Integer.parseInt(m.get("id").toString()));
		    	order.setStatus(Integer.parseInt(m.get("status").toString()));
		    	retlist.add(order);
			}

			System.out.println("relist:" + retlist);
			return retlist;

		} catch (SqlException e) {
			log.error("catch exception while get order status from db!", e);
			e.printStackTrace();
		}
		return null;
	}
}
