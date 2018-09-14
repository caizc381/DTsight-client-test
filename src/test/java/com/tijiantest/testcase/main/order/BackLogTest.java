package com.tijiantest.testcase.main.order;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.card.Card;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.Order;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项
 * 位置：C端首页
 */
public class BackLogTest extends MainBase {

    @Test(description = "查看用户的待办事项(医院站点)",groups = {"qa"})
    public void test_01_backlog_hospital(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("hospital",defHospitalId+""));
        String site = HospitalChecker.getSiteByOrganizationId(defHospitalId);
        params.add(new BasicNameValuePair("_site",site));
        params.add(new BasicNameValuePair("_siteType","mobile"));
        HttpResult response = httpclient.get(Flag.MAIN, BackLog,params);
        Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
        String exam = JsonPath.read(response.getBody(),"$.exam").toString();
        String unExam = JsonPath.read(response.getBody(),"$.unExam").toString();
        String unPayOrderList = JsonPath.read(response.getBody(),"$.unPayOrderList").toString();
        if(checkdb){
            //...toCheck
        }
    }


    @Test(description = "查看用户的待办事项（渠道站点）",groups = {"qa"})
    public void test_02_backlog_channel(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("hospital",196+""));
        String site = HospitalChecker.getSiteByOrganizationId(196);
        params.add(new BasicNameValuePair("_site",site));
        params.add(new BasicNameValuePair("_siteType","mobile"));
        HttpResult response = httpclient.get(Flag.MAIN, BackLog,params);
        Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
        String exam = JsonPath.read(response.getBody(),"$.exam").toString();
        String unExam = JsonPath.read(response.getBody(),"$.unExam").toString();
        String unPayOrderList = JsonPath.read(response.getBody(),"$.unPayOrderList").toString();
        Assert.assertEquals(exam,"[]");
        Assert.assertEquals(unExam,"[]");
        Assert.assertEquals(unPayOrderList,"[]");
    }

}
