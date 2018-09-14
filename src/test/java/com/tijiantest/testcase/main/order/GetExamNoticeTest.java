package com.tijiantest.testcase.main.order;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CardChecker;
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
 * 查看订单的体检通知
 * 位置：C端订单详情
 */
public class GetExamNoticeTest extends MainBase {

    @Test(description = "查看订单的体检通知",groups = {"qa"},dependsOnGroups = "main_mainBook")
    public void test_01_getExamNotice(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("_site","mt"));
        params.add(new BasicNameValuePair("_siteType","mobile"));
        int orderId = BookTest.commOrderId;
        HttpResult response = httpclient.post(Flag.MAIN, MainOrder_GetExamNotice+"?orderId="+orderId,params);
        Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
        String hosNotice = JsonPath.read(response.getBody(),"$.hosNotice");
        Hospital hos = OrderChecker.getOrderInfo(orderId).getHospital();
        Assert.assertEquals(hosNotice,hos.getExamNotice());
    }

    @Test(description = "查看订单的体检通知",groups = {"qa"},dependsOnGroups = "main_bookWithEntryCard")
    public void test_02_getExamNotice(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("_site","mt"));
        params.add(new BasicNameValuePair("_siteType","mobile"));
        int orderId = BookTest.comm_entryCard_OrderId;
        HttpResult response = httpclient.post(Flag.MAIN, MainOrder_GetExamNotice+"?orderId="+orderId,params);
        Assert.assertEquals(response.getCode(), HttpStatus.SC_OK);
        String hosNotice = JsonPath.read(response.getBody(),"$.hosNotice");
        Order order  = OrderChecker.getOrderInfo(orderId);
        Hospital hos = order.getHospital();
        Assert.assertEquals(hosNotice,hos.getExamNotice());
        String cardNotice = JsonPath.read(response.getBody(),"$.cardNotice");
        try {
            Card card = CardChecker.getCardInfo(order.getEntryCardId());
            if(card.getExamNoteId()!=null){
                Assert.assertEquals(CardChecker.getCardExamNotes(card.getExamNoteId()).getNote(),cardNotice);
            }

        } catch (SqlException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
