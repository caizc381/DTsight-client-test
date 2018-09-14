package com.tijiantest.testcase.main.nologinbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.db.SqlException;
/**
 * 检查是否登陆
 * 获取当前的地理位置
 * @author huifang
 *
 */
public class NologinPrepareTest extends MainBaseNoLogin {

	@Test(description = "验证是否登陆",groups = {"qa","main_validateNologin"})
	public void test_01_validateLoginAddToken(){
		//STEP0：确保免登陆环境
		checkNoLoginEnv(hc1,defSite);
	}
	
	@Test(description = "加载免登陆预约界面",groups = {"qa"},dependsOnMethods = "test_01_validateLoginAddToken")
	public void test_02_loadSubmitOrderPageNoLogin() throws SqlException{
		 int mealId = ResourceChecker.getOfficialMealList(defHospitalId, MealGenderEnum.FEMALE.getCode()).get(0).getId();
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 NameValuePair _site = new BasicNameValuePair("_site",defSite);
		 NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
		 NameValuePair meal = new BasicNameValuePair("mealId",mealId+"");
		 params.add(_site);
		 params.add(_siteType);
		 params.add(meal);
		 HttpResult result = hc1.post(Flag.MAIN,LoadSubmitOrderPageNoLogin,params);
		 Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
		 String body = result.getBody();
		 log.info("body...."+body);
		 String latitude = JsonPath.read(body, "$.address.latitude").toString();
		 String longitude = JsonPath.read(body, "$.address.longitude").toString();

		 if(checkdb){
			Assert.assertEquals(latitude,defHospital.getLatitude());
			Assert.assertEquals(longitude,defHospital.getLongitude());
		 }
	}
	
}
