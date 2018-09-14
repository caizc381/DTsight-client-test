package com.tijiantest.testcase.crm.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.account.AccountGenderEnum;
import com.tijiantest.model.account.AddAccountTypeEnum;
import com.tijiantest.model.company.HospitalCompany;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

/**
 * 
 * CRM确认收款
 * @author huifang
 *
 */
public class NeedLocalPayTest extends CrmBase{

	private List<Integer> orderIdList = new ArrayList<Integer>();

	@Test(description= "CRM手动确认收款（现场支付的订单）",groups = {"qa"})
	public void test_01_needLocalPay() throws Exception{
		System.out.println("--------------------------普通用户下现场支付订单开始-------------------------------------");
		Map<String,Object>  settings = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.NEED_LOCAL_PAY);
		boolean needLocalPay = Integer.parseInt(settings.get(HospitalParam.NEED_LOCAL_PAY).toString()) == 1 ? true:false;
		//1.确保医院设置
		if(!needLocalPay){
			//强制修改数据库，使当前医院需要确认收款
			DBMapper.update("update tb_hospital_settings set need_local_pay = 1 where hospital_id = " +defhospital.getId());
		}

		//2.下现场支付订单
		String accountfileName = "./csv/opsRefund/company_account_tmpt2.xlsx";
		int hisAccountId = 0;
			//创建导入用户xls
			JSONArray idCardNameList = AccountChecker.makeUploadXls(1,accountfileName);
			HospitalCompany hc = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(2,defhospital.getId());
			AccountChecker.uploadAccount(httpclient, hc.getId(), defhospital.getId(), "auto测试",
					accountfileName,AddAccountTypeEnum.idCard);

			for(int i=0;i<idCardNameList.size();i++){
				JSONObject jo = (JSONObject)idCardNameList.get(i);
				String idCard = jo.getString("idCard");
				String name = jo.getString("name");
				hisAccountId = AccountChecker.getAccountId(idCard, name, "auto测试",defaccountId);
				//预约当天
				String examDate = sdf.format(new Date());
				List<Meal> mealList = ResourceChecker.getOffcialMeal(defhospital.getId(),Arrays.asList(AccountGenderEnum.MALE.getCode(),AccountGenderEnum.Common.getCode()));
				Order hisOrder = OrderChecker.crm_createOrder(httpclient, mealList.get(0).getId(), hisAccountId, hc.getId(),hc.getName(),
						examDate,HospitalChecker.getHospitalById(defhospital.getId()));
				orderIdList.add(hisOrder.getId());

				//3.确认收款
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.clear();
				params.add(new BasicNameValuePair("orderId", hisOrder.getId() + ""));
				HttpResult result = httpclient.post(Order_NEEDLOCALPAY_V2,params);
				Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
				log.info("确认收款返回.."+result.getBody());
				Assert.assertEquals(result.getBody(),"");
				if(checkdb){
					Order nowOrder = OrderChecker.getOrderInfo(hisOrder.getId());
					Assert.assertEquals(nowOrder.getStatus(), OrderStatus.ALREADY_BOOKED.intValue());
					//订单交易按照极速预约线下支付验证
					PayChecker.checkCrmNeedLocalPay(defhospital.getId(),hc,hisOrder,defaccountId);
				}
				if(checkmongo){
					List<Map<String, Object>> list = MongoDBUtils.query("{'id':" + hisOrder.getId() + "}",
							MONGO_COLLECTION);
					Assert.assertNotNull(list);
					Assert.assertEquals(1, list.size());
					Assert.assertEquals(Integer.parseInt(list.get(0).get("status").toString()),OrderStatus.ALREADY_BOOKED.intValue());
				}
			}



		System.out.println("--------------------------普通用户下现场支付订单结束-------------------------------------");
	}


	@AfterClass(description="代撤销订单",groups = {"qa"})
	public void afterTest() throws SqlException {
		/*****撤销订单******/
		OrderChecker.Run_CrmOrderRevokeOrder(httpclient,orderIdList,false,true,true);
	}
}