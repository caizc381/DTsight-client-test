package com.tijiantest.testcase.main.order;

import com.tijiantest.base.Flag;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.account.AccountGenderEnum;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.util.DateUtils;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.testng.annotations.Test;

import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.main.MainBase;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PrepareMainOrderTest extends MainBase {
	
	public static Order crmOrder;
	@Test(description = "crm创建订单",groups = {"qa","main_mainUseCrmOrder"})
	public void test_01_crmCreateOrder(){
		try{
			MyHttpClient crmClient = new MyHttpClient();
			onceLoginInSystem(crmClient, Flag.CRM,defCrmUsername,defCrmPasswd);
			com.tijiantest.model.resource.meal.Meal meal = ResourceChecker.getOffcialMeal(defHospitalId, Arrays.asList(AccountGenderEnum.Common.getCode(),AccountGenderEnum.FEMALE.getCode())).get(0);
			crmOrder = OrderChecker.crm_createOrder(crmClient,meal.getId(),defaccountId,defHospitalCompany.getId(),defHospitalCompany.getName(), sdf.format(DateUtils.offDate(20)),defHospital);
		}catch (Exception e){
			//crm未部署时
			try {
				List<Map<String,Object>> orderList = DBMapper.query("select * from tb_order where account_id = "+defaccountId+" and status in (2,0)");
				if(orderList == null || orderList.size()==0){
					log.error("无法获取crmOrder...请手动创建订单");
					return;
				}
				int crmOrderId = Integer.parseInt(orderList.get(0).get("id").toString());
				crmOrder = OrderChecker.getOrderInfo(crmOrderId);
			} catch (SqlException e1) {
				e1.printStackTrace();
			}
		}
	}
}
