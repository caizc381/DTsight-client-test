package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.annotations.DeepHospital;
import com.tijiantest.testcase.crm.CrmDeepBase;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 单位体检->单位订单
 * 导出所有到体检软件（深对接）
 */
public class ManualExportOrderTest extends CrmDeepBase {

	@DeepHospital(description = "只在深对接体检中心中使用")
	@Test(description = "导出所有到体检软件", groups = { "qa" })
	public void test_01_manualExportOrder() throws ParseException, SqlException {
		boolean showImmediatelyImpOrder = true;
		List<Integer> accountTypes = Arrays.asList(1, 2, 3, 5, 6, 7);
		List<HospitalCompany> hcList = CompanyChecker.getHospitalCompanyByOrganizationId(defDeepHosptailId, null, false);
		int examCompanyId = 0;
		List<Order> orderList = null;
		for (HospitalCompany hc : hcList) {
			examCompanyId = hc.getId();
			if (hc.getPlatformCompanyId() != null && (hc.getPlatformCompanyId() == 1 || hc.getPlatformCompanyId() == 3))
				continue;
			orderList = OrderChecker.getOrderListBySql("select * from tb_order where hospital_id = " + defDeepHosptailId + " " +
					"and hospital_company_id=" + examCompanyId + " and status = 2 and is_export=false order by id desc");
			if (orderList == null && orderList.size() == 0) {
				continue;
			}
		}
		if (orderList.size() == 0) {
			log.info("该体检中心没有可用于导出到体检中心的单位，开始创建订单~~");
			orderList = OrderChecker.crm_createOrder(deepClient,deepManagerId,defDeepHosptailId,1);
		}
		log.info("订单列表.."+orderList);
		OrderQueryParams orderParams = new OrderQueryParams();
		orderParams.setAccountTypes(accountTypes);
		orderParams.setExamCompanyIds(Arrays.asList(examCompanyId));
		orderParams.setExamStartDate(simplehms.parse("2015-01-01 00:00:00"));
		orderParams.setExportImmediately(false);
		orderParams.setHospitalIds(Arrays.asList(defDeepHosptailId));
		orderParams.setIsExport(false);
		orderParams.setOrderStatuses(Arrays.asList(OrderStatus.ALREADY_BOOKED.intValue()));
		HttpResult result = deepClient.post(Order_ManualExportOrder,
				JSON.toJSONString(orderParams));
		String body = result.getBody();
		System.out.println(body);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		Assert.assertTrue(body.equals("{}") || body.equals(""));

		if (checkdb) {
			if (orderList != null) {
				for (Order order : orderList) {
					log.info("订单id:"+order.getId());
					Order retOrder = OrderChecker.getOrderInfo(order.getId());
					Assert.assertEquals(retOrder.getIsExport().booleanValue(), true,"订单导出失败，请检查医院ID为"+defDeepHosptailId+"的agent端是否正常开启，此时订单状态为"+retOrder.getStatus());
				}
			}
		}
		if (checkmongo) {
			if (orderList != null) {
				for (Order order : orderList) {
					List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + order.getId() + "}", MONGO_COLLECTION);
					Assert.assertEquals(1, list.size());
					Assert.assertEquals(Boolean.parseBoolean(list.get(0).get("isExport").toString()), true);
				}
			}
		}
	}
}
