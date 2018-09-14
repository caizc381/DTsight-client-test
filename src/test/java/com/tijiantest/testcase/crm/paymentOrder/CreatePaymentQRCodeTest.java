package com.tijiantest.testcase.crm.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.HttpResult;
import com.tijiantest.model.paymentOrder.PaymentQRCodeVO;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
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
 * CRM->维护后台->日常管理->客户经理收款二维码
 * 点击链接进入
 * @author huifang
 */
public class CreatePaymentQRCodeTest extends CrmBase {
    @Test(description = "创建收款二维码",dataProvider = "createPaymentQRCode",groups = {"qa"})
    public void test_01_createPayQRCodeTest(String ...args) throws SqlException {
            int hospitalId = defhospital.getId();
            String hosptialName = defhospital.getName();
            int managerId = defaccountId;
            String alipayStr = args[1];
            String weixinStr = args[2];
            boolean isAlipay = Boolean.parseBoolean(alipayStr);
            boolean isWeixin = Boolean.parseBoolean(weixinStr);
            if(isAlipay)
                DBMapper.update("update tb_hospital_settings set ali_pay = 1  where hospital_id = "+hospitalId);
            else
                DBMapper.update("update tb_hospital_settings set ali_pay = 0  where hospital_id = "+hospitalId);
             if(isWeixin)
                DBMapper.update("update tb_hospital_settings set weixin_pay = 1  where hospital_id = "+hospitalId);
            else
                DBMapper.update("update tb_hospital_settings set weixin_pay = 0  where hospital_id = "+hospitalId);

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(new BasicNameValuePair("hospitalId",hospitalId+""));
            nameValuePairList.add(new BasicNameValuePair("managerId",managerId+""));

            HttpResult result = httpclient.get(Payment_CreatePaymentQRCode,nameValuePairList);
            log.info(result.getBody());
            Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
            PaymentQRCodeVO vo = JSON.parseObject(result.getBody(), PaymentQRCodeVO.class);
            if(checkdb){
                Assert.assertEquals(vo.getHospitalName(),hosptialName);
                String sql = "select * from tb_site_resource where hospital_id = "+hospitalId+" and type = 1";
                List<Map<String,Object>> dblist = DBMapper.query(sql);
                Assert.assertEquals(dblist.size(),1);
                Assert.assertEquals(vo.getLogo(),dblist.get(0).get("value"));
                if(isAlipay && isWeixin)//支付宝+微信
                    Assert.assertEquals(vo.getPaymentMethod().intValue(),3);
                else if (isAlipay) //支付宝
                    Assert.assertEquals(vo.getPaymentMethod().intValue(),1);
                else if(isWeixin)//微信
                    Assert.assertEquals(vo.getPaymentMethod().intValue(),2);
                Assert.assertNotNull(vo.getQrCode());


            }





    }

    @DataProvider
    public Iterator<String[]> createPaymentQRCode(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/createPaymentQRCode.csv",18);
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
