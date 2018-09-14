package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.*;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import java.util.*;

/**
 * 
 * 验证是否重复下单
 * @author huifang
 *
 */
public class VerifySameAccountOrderTest extends CrmBase{

	private List<Integer> orderIdList = new ArrayList<Integer>();

	@Test(description= "普通单位批量下重复单（母卡）",groups = {"qa"})
	public void test_01_order_duplicate() throws Exception{
		System.out.println("--------------------------普通用户下单开始-------------------------------------");
		Map<String,Object>  settings = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.SAME_DAY_ORDER_MAXIMUM);
		int same_order_max = Integer.parseInt(settings.get(HospitalParam.SAME_DAY_ORDER_MAXIMUM).toString());
		List<Order> orderList = OrderChecker.crm_createOrder(httpclient,defaccountId,defhospital.getId(),1);
		Order order = orderList.get(0);
		orderIdList.add(order.getId());
		String date = sdf.format(order.getExamDate());

		for(int k=0;k<same_order_max-1;k++){
			waitto(5);//给同一个用户重复下单，保持5s间隔(否则会有提示：该订单正在提交，请勿再次提交)
			JSONObject jo = new JSONObject();
			jo.put("accountIdList", Arrays.asList(order.getAccount().getId()));
			jo.put("bookType",1);
			jo.put("examDate",date);
			jo.put("hospitalId",defhospital.getId());
			//判断重复下单
			HttpResult result = httpclient.post(Order_VerifySameAccountOrder,JSON.toJSONString(jo));
			Assert.assertEquals(result.getCode(),HttpStatus.SC_BAD_REQUEST);
			log.info(result.getBody());
			String exceptType = JsonPath.read(result.getBody(),"$.exceptType");
			String accountListStr = JsonPath.read(result.getBody(),"$.accountList").toString();

			Assert.assertEquals(exceptType,"duplicate");//有重复下单
			List<JSONObject> jsonList = JSONArray.parseArray(accountListStr,JSONObject.class);
			//check database
			if(checkdb){
				for(JSONObject json :jsonList){
					Assert.assertTrue(Integer.parseInt(json.get("accountId").toString()) == order.getAccount().getId());
					Assert.assertTrue(json.get("examDate").toString().equals(sdf.format(order.getExamDate())));
				}
			}
			//再次下单
			Order secondOrder = OrderChecker.crm_createOrder(httpclient,order.getOrderMealSnapshot().getMealSnapshot().getId(),
					order.getAccount().getId(),order.getHospitalCompany().getId(),order.getHospitalCompany().getName(),date,defhospital);
			orderIdList.add(secondOrder.getId());
		}

		JSONObject jo = new JSONObject();
		jo.put("accountIdList", Arrays.asList(order.getAccount().getId()));
		jo.put("bookType",1);
		jo.put("examDate",date);
		jo.put("hospitalId",defhospital.getId());
		//判断重复下单
		HttpResult result = httpclient.post(Order_VerifySameAccountOrder,JSON.toJSONString(jo));
		Assert.assertEquals(result.getCode(),HttpStatus.SC_BAD_REQUEST);
		log.info("最后一次下单检查..."+result.getBody());
		String exceptType = JsonPath.read(result.getBody(),"$.exceptType");
		String accountListStr = JsonPath.read(result.getBody(),"$.accountList").toString();

		Assert.assertEquals(exceptType,"sameDayOrderMaximum");//重复下单最大上限
		List<JSONObject> jsonList = JSONArray.parseArray(accountListStr,JSONObject.class);
		//check database
		if(checkdb){
			for(JSONObject json :jsonList){
				Assert.assertTrue(Integer.parseInt(json.get("accountId").toString()) == order.getAccount().getId());
				Assert.assertTrue(json.get("examDate").toString().equals(sdf.format(order.getExamDate())));
			}
		}

		System.out.println("--------------------------普通用户下单结束-------------------------------------");
	}


	@Test(description= "普通单位批量下单，首次（母卡）",groups = {"qa"})
	public void test_01_order_first() throws Exception{
		System.out.println("--------------------------普通用户首次下单-------------------------------------");
		List<Order> hisOrderList = new ArrayList<Order>();
		String accountfileName = "./csv/opsRefund/company_account_tmpt1.xlsx";
		int hisAccountId = 0;
		//创建导入用户xls
		JSONArray idCardNameList = AccountChecker.makeUploadXls(1,accountfileName);
		HospitalCompany hc = CompanyChecker.getRandomCommonHospitalCompany(defhospital.getId());
		AccountChecker.uploadAccount(httpclient, hc.getId(), defhospital.getId(), "auto测试",
				accountfileName,AddAccountTypeEnum.idCard);

		for(int i=0;i<idCardNameList.size();i++) {
			JSONObject jo = (JSONObject) idCardNameList.get(i);
			String idCard = jo.getString("idCard");
			String name = jo.getString("name");
			hisAccountId = AccountChecker.getAccountId(idCard, name, "auto测试", defaccountId);
			String date = sdf.format(new Date());
			JSONObject joe = new JSONObject();
			joe.put("accountIdList", Arrays.asList(hisAccountId));
			joe.put("bookType", 1);
			joe.put("examDate", date);
			joe.put("hospitalId", defhospital.getId());
			HttpResult result = httpclient.post(Order_VerifySameAccountOrder, JSON.toJSONString(jo));
			log.info(result.getBody());
			Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
			Assert.assertTrue(result.getBody().equals("")||result.getBody().equals("{}"));
		}
		System.out.println("--------------------------普通用户首次下单-------------------------------------");
	}


	@AfterClass(description="代撤销订单",groups = {"qa"})
	public void afterTest() throws SqlException {
		/*****撤销订单******/
		if(orderIdList !=null && orderIdList.size()>0)
			OrderChecker.Run_CrmOrderRevokeOrder(httpclient,orderIdList,false,true,true);
	}
}