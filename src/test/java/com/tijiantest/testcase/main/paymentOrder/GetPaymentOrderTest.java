package com.tijiantest.testcase.main.paymentOrder;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.HospitalChecker;
import com.tijiantest.model.hospital.HospitalParam;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.CvsFileUtil;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 普通用户扫客户经理的收款二维码，会显示该体检中心的支付提示备注
 * @author huifang
 */
public class GetPaymentOrderTest extends MainBaseNoLogin {
    @Test(description = "获取收款订单的支付提示备注",groups = {"qa"},dataProvider = "getPaymentOrder")
    public void test_01_getPaymentOrder(String ...args){
        String hosptialStr = args[1];
        //STEP1:入参
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("hospitalId",hosptialStr));
        //STEP2:调用接口
        HttpResult result = hc3.get(Flag.MAIN,Payment_GetPaymentOrder,params);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String payTipTest = JsonPath.read(result.getBody(),"$.payTipText");

        //STEP3：验证提示与库中一致
        if(checkdb){
            int hospitalId = Integer.parseInt(hosptialStr);
            Map<String, Object> dbMap = HospitalChecker.getHospitalSetting(hospitalId, HospitalParam.PayTipText);
            if(payTipTest!=null && !payTipTest.trim().equals("")){//医院有C端提示文字
                String dbPayTipText = dbMap.get(HospitalParam.PayTipText).toString();
                Assert.assertEquals(payTipTest,dbPayTipText);
            }
            else//医院没有C端提示文字
                Assert.assertNull(dbMap.get(HospitalParam.PayTipText));

        }


    }

    @DataProvider
    public Iterator<String[]> getPaymentOrder(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/getPaymentOrder.csv",10);
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
