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
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.payment.invoice.InvoiceApply;
import com.tijiantest.model.payment.invoice.InvoiceApplyVO;
import com.tijiantest.model.payment.invoice.InvoiceStatusEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;

public class InvoiceTest extends CrmBase {
	String orderId = "";

	@Test(description = "查询订单的发票信息", groups = { "qa" }, dataProvider = "mongoOrder")
	public void test_01_invoice(String... args) throws SqlException, ParseException {
		// TODO: 取列表，然后取第一个ID
		JSONObject orderObject = this.initMongoOrder(args);
		if (orderObject == null) {
			return;
		}
		orderId = orderObject.get("id").toString();

		List<NameValuePair> params1 = new ArrayList<>();
		params1.add(new BasicNameValuePair("orderId", orderId));
		HttpResult result = httpclient.get(Order_Invoice, params1);
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (body.equals("{}") || body.equals("")) {
			System.out.println("未申请过发票=========" + body);
			// 在C端未申请过发票
			if (checkdb) {
				String sql = "select * from tb_invoice_apply where order_id=?";
				List<Map<String, Object>> list = DBMapper.query(sql, orderId);
				Assert.assertEquals(0, list.size());
			}
		} else {
			// 在C端申请过发票
			System.out.println("已申请过发票=========" + body);
			InvoiceApply invoiceApply = JSON.parseObject(JsonPath.read(body, "$.invoiceApply").toString(),
					InvoiceApply.class);
			if (checkdb) {
				String invoiceApplySql = "SELECT id, title,content, apply_amount as applyAmount, amount, remark, status, order_id as orderId, delivery_type as deliveryType, address_id as addressId, proposer, approver, postage FROM tb_invoice_apply WHERE order_id=?";
				List<Map<String, Object>> invoiceApplyList = DBMapper.query(invoiceApplySql, orderId);
				Assert.assertEquals(invoiceApply.getAmount(), invoiceApplyList.get(0).get("amount"));
				Assert.assertEquals(invoiceApply.getApplyAmount(), invoiceApplyList.get(0).get("applyAmount"));
				Assert.assertEquals(invoiceApply.getApprover(), invoiceApplyList.get(0).get("approver"));
				Assert.assertEquals(invoiceApply.getDeliveryType(), invoiceApplyList.get(0).get("deliveryType"));
				Assert.assertEquals(invoiceApply.getId(), invoiceApplyList.get(0).get("id"));
				Assert.assertEquals(invoiceApply.getProposer(), invoiceApplyList.get(0).get("proposer"));
				Assert.assertEquals(invoiceApply.getStatus(), invoiceApplyList.get(0).get("status"));
				Assert.assertEquals(invoiceApply.getTitle(), invoiceApplyList.get(0).get("title"));
			}
		}

		// TODO : 微信已经开过发票了
	}

	@Test(description = "保存订单发票信息", groups = { "qa" }, dataProvider = "invoice")
	public void test_02_invoice(String... args) throws SqlException, ParseException {
		// TODO: 取列表，然后取第一个ID
		String title = args[9];
		String remark = args[10];

		JSONObject orderObject = this.initMongoOrder(args);
		if (orderObject == null) {
			return;
		}
		orderId = orderObject.get("id").toString();

		List<NameValuePair> params1 = new ArrayList<>();
		params1.add(new BasicNameValuePair("orderId", orderId));
		HttpResult result = httpclient.get(Order_Invoice, params1);
		String body = result.getBody();
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		log.info("body..."+body);
		// 取得自付金额
		int selfMoney = orderObject.getJSONObject("orderExtInfo").getIntValue("selfMoney");

		InvoiceApplyVO vo = new InvoiceApplyVO();
		// 在C端申请过发票
			vo.setTitle(title);
			vo.setMoney(selfMoney+"");
			vo.setOrderId(Integer.valueOf(orderId));
			vo.setStatus(InvoiceStatusEnum.MARK_OUT_INVOICE.getCode());
			InvoiceApply invoiceApply = new InvoiceApply();
			if(JsonPath.read(body, "$.invoiceApply") != null){
				invoiceApply = JSON.parseObject(JsonPath.read(body, "$.invoiceApply").toString(),
						InvoiceApply.class);
				invoiceApply.setApprover(defaccountId);

			}else{
				remark = "";
				invoiceApply.setOrderId(Integer.parseInt(orderId));
			}
			invoiceApply.setRemark(remark);
			invoiceApply.setAmount(selfMoney);
			invoiceApply.setApplyAmount(selfMoney);
			invoiceApply.setTitle(title);
			invoiceApply.setStatus(InvoiceStatusEnum.MARK_OUT_INVOICE.getCode());

		vo.setInvoiceApply(invoiceApply);



		String json = JSON.toJSONString(vo);
		result = httpclient.post(Order_Invoice, json);
		System.out.println(result.getBody());
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		if (checkdb) {

			String sql = "select * from tb_invoice_apply where order_id=?";
			List<Map<String, Object>> list = DBMapper.query(sql, orderId);
			//金额为0
//			if(vo.getMoney().equals("0")||vo.getMoney().equals("0.0")||vo.getMoney().equals("0.00")){
//				Assert.assertEquals(list.size(),0);
//			}else{
				Assert.assertEquals(String.valueOf(selfMoney), list.get(0).get("amount").toString());
				Assert.assertEquals(title, list.get(0).get("title"));
				Assert.assertEquals(InvoiceStatusEnum.MARK_OUT_INVOICE.getCode(), list.get(0).get("status"));
				Assert.assertEquals(remark, list.get(0).get("remark"));
				Assert.assertEquals(defaccountId, list.get(0).get("approver"));

				if (list.get(0).get("address_id") != null) {
					String userAddressSql = "select * from tb_user_address where id=?";
					List<Map<String, Object>> userAddressList = DBMapper.query(userAddressSql,
							list.get(0).get("address_id"));
					Assert.assertEquals(1, userAddressList.size());
				}
//			}

		}
	}

	/**
	 * 用例需要完善后打开
	 * @param args
	 */
	@Test(description = "撤销开票", groups = { "qa" },enabled=false)
	public void test_03_invoice(String... args) throws ParseException {
		// TODO: 取列表，然后取第一个ID
//		String title = args[9];
//		String remark = args[10];

		JSONObject orderObject = this.initMongoOrder(args);
		if (orderObject == null) {
			return;
		}
		orderId = orderObject.get("id").toString();
		String status = orderObject.getString("status").toString();
		if (!status.equals("3")) {
			return;
		}

		// TODO:代码接口构造不合理，待修改后再写接口测试
	}

	private JSONObject initMongoOrder(String... args) throws ParseException {
		String rowCount = args[1];
		String currentPage = args[2];
		String pageSize = args[3];
		String gte = args[4];
		String lte = args[5];
		Boolean isExport = Boolean.valueOf(args[6]);
		String where = args[7];
		String accountRelationName = args[8];

		@SuppressWarnings("serial")
		List<Integer> status = new ArrayList<Integer>() {
			{
				add(OrderStatus.ALREADY_BOOKED.intValue());
				add(OrderStatus.EXAM_FINISHED.intValue());
				add(OrderStatus.NOT_EXAM.intValue());
				add(OrderStatus.PART_BACK.intValue());
			}
		};

		String jsonBody = JSON.toJSONString(OrderBase.generateQueryOrderObj(gte, lte, defhospital.getId(), status,
				isExport, where, accountRelationName));
//		List<NameValuePair> pairs = new ArrayList<>();
//		pairs.add(new BasicNameValuePair("rowCount", rowCount));
//		pairs.add(new BasicNameValuePair("currentPage", currentPage));
//		pairs.add(new BasicNameValuePair("pageSize", pageSize));

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
			params.setAccountCompanyName(accountRelationName);
//		Map<String, String> params = new HashMap<>();
//		params.put("queryOrderObj", jsonBody);
		HttpResult result = httpclient.post(Flag.CRM, Order_MongoOrder, JSON.toJSONString(params));

		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

		JSONObject jsonArrayList = JSON.parseObject(result.getBody());
		String stringArray = jsonArrayList.get("records").toString();
		JSONArray records = JSONArray.parseArray(stringArray);

		if (records == null || records.isEmpty()) {
			return null;
		}

		// 取第一个orderId
		JSONObject orderObject = (JSONObject) records.get(0);
		// String orderId = jsonObject.get("id").toString();
		return orderObject;
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

	@DataProvider(name = "invoice")
	public Iterator<String[]> invoice() {
		try {
			return CvsFileUtil.fromCvsFileToIterator("./csv/order/invoice.csv", 18);
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
