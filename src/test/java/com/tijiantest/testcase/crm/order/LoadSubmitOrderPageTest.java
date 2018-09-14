package com.tijiantest.testcase.crm.order;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.ResourceChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 极速预约，选择套餐进行直接预约/改项预约
 * @author huifang
 */
public class LoadSubmitOrderPageTest extends CrmBase {
    @Test(description = "极速预约点击直接预约/改项预约时获取套餐价格/折扣/提示文案",groups = {"qa"})
    public void test_01_loadSubmitOrderPage() throws SqlException {
        List<Meal> mealLists  = ResourceChecker.getOffcialMeal(defhospital.getId());
        int mealId = mealLists.get(0).getId();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("mealId",mealId+""));
        params.add(new BasicNameValuePair("hospitalId",defhospital.getId()+""));

        HttpResult result = httpclient.get(Order_LoadSubmitOrderPage,params);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        int originalPrice = JsonPath.read(body,"$.price.originPrice");
        int price = JsonPath.read(body,"$.price.price");
        double discount = JsonPath.read(body,"$.price.mealDiscount");
        String fastBookPayTipText = JsonPath.read(body,"$.fastBookPayTipText");

        if(checkdb){
            Meal meal = ResourceChecker.getMealInfo(mealId);
            Assert.assertEquals(meal.getPrice().intValue(),price);
            Assert.assertEquals(meal.getInitPrice().intValue(),originalPrice);
            Assert.assertEquals(meal.getDiscount().doubleValue(),discount);

            Map<String,Object> dbMap = HospitalChecker.getHospitalSetting(defhospital.getId(), HospitalParam.FastBookPayTipText);
            Object fastTip = dbMap.get(HospitalParam.FastBookPayTipText);
            if(fastBookPayTipText == null || fastBookPayTipText.trim().equals(""))
                Assert.assertNull(fastTip);
            else
                Assert.assertEquals(fastBookPayTipText,fastTip.toString());
        }


    }
}
