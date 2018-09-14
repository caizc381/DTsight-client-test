package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.order.OrderBatch;
import com.tijiantest.model.order.channel.OrderQueryRequestParams;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.MapUtil;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.ss.formula.functions.Na;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 查看批量代预约订单
 * 位置：CRM->单位体检->单位订单->客户下单/批量代预约订单调用
 * @author huifang
 *
 */
public class OrderBatchTest extends CrmBase{

	private OrderBatch firstOrderBatch = null;
	@Test(description = "查看批量代预约订单" ,groups = {"qa"})
	public void test_01_get_orderBatch() {
		HospitalCompany hc = CompanyChecker.getRandomCommonHospitalCompany(defhospital.getId());
		int companyId = hc.getId();
		List<NameValuePair>  nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("companyId",companyId+""));
		HttpResult result = httpclient.get(Order_OrderBatch,nameValuePairs);
		Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		String body = result.getBody();
		log.info("获取订单批次返回.."+ body);
		List<OrderBatch> retList = JSON.parseArray(body,OrderBatch.class);
		if(checkdb) {
			List<OrderBatch> orderBatchList = OrderChecker.getCompanyOrderBatch(defhospital.getId(),companyId,defaccountId);
			Assert.assertEquals(retList.size(),orderBatchList.size());
			firstOrderBatch = retList.get(0);
			for(int i=0;i<orderBatchList.size();i++){
					Assert.assertEquals(retList.get(i).getAmount(),orderBatchList.get(i).getAmount());
					Assert.assertEquals(retList.get(i).getBookExportPrice(),orderBatchList.get(i).getBookExportPrice());
					Assert.assertEquals(retList.get(i).getMealId(),orderBatchList.get(i).getMealId());
					Assert.assertEquals(retList.get(i).getMealGender(),orderBatchList.get(i).getMealGender());
					Assert.assertEquals(retList.get(i).getMealName(),orderBatchList.get(i).getMealName());
					Assert.assertEquals(retList.get(i).getMealPrice(),orderBatchList.get(i).getMealPrice());
					Assert.assertEquals(retList.get(i).getIsChangeDate(),orderBatchList.get(i).getIsChangeDate());
					Assert.assertEquals(retList.get(i).getIsHidePrice(),orderBatchList.get(i).getIsHidePrice());
					Assert.assertEquals(retList.get(i).getIsProxyCard(),orderBatchList.get(i).getIsProxyCard());
					Assert.assertEquals(retList.get(i).getIsReduceItem(),orderBatchList.get(i).getIsReduceItem());
					Assert.assertEquals(retList.get(i).getIsSitePay(),orderBatchList.get(i).getIsSitePay());
					Assert.assertEquals(retList.get(i).getExamDate(),orderBatchList.get(i).getExamDate());
					Assert.assertEquals(retList.get(i).getBookTime(),orderBatchList.get(i).getBookTime());

				}

		}
	}

	@Test(description = "更新订单的批次属性",groups = {"qa"},dependsOnMethods = "test_01_get_orderBatch")
	public void test_02_post_orderBatch(){
		Boolean isChangeDate = firstOrderBatch.getIsChangeDate();
		Boolean isHidePrice = firstOrderBatch.getIsHidePrice();
		if(isChangeDate.booleanValue())
			firstOrderBatch.setIsChangeDate(false);
		else
			firstOrderBatch.setIsChangeDate(true);
		if(isHidePrice)
			firstOrderBatch.setIsHidePrice(false);
		else
		  	firstOrderBatch.setIsHidePrice(true);
		HttpResult result = httpclient.post(Order_OrderBatch,JSON.toJSONString(firstOrderBatch));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
		Assert.assertTrue(result.getBody().equals("")||result.getBody().equals("{}"),"返回.."+result.getBody());
		if(checkdb){
			OrderBatch batch = OrderChecker.getOrderBatch(firstOrderBatch.getId());
			Assert.assertEquals(firstOrderBatch.getIsChangeDate(),batch.getIsChangeDate());
			Assert.assertEquals(firstOrderBatch.getIsHidePrice(),batch.getIsHidePrice());

		}
	}
}

