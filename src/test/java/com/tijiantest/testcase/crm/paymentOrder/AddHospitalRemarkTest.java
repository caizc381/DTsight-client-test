package com.tijiantest.testcase.crm.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.AccountChecker;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.common.LogTypeEnum;
import com.tijiantest.model.settlement.PaymentOrder;
import com.tijiantest.model.settlement.TradeCommonLogResultDTO;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

/**
 * CRM->订单&用户->收款订单页面
 * 操作：新增/修改医院备注
 * @author huifang
 */
public class AddHospitalRemarkTest extends SettleBase {

    String orderNum = null;
    @Test(description = "收款订单增加医院备注",groups = {"qa"},dataProvider = "addHospitalRemark")
    public void test_01_addHospitalRemark(String ...args) throws SqlException, ParseException {
        waitto(1);
        int hospitalId = defSettHospitalId;
        String hospitalRemark = args[1];
        List<PaymentOrder> paymentOrderList = OrderChecker.getPaymentOrderListBySql("select * from tb_payment_order where organization_id="+hospitalId);
        int maxSize = paymentOrderList.size();
        int index = (int)(Math.random() * (maxSize-1));
        orderNum = paymentOrderList.get(index).getOrderNum();
        String beforeTime = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
        //STEP1:入参准备
        JSONObject jo = new JSONObject();
        jo.put("hosptialId",hospitalId);
        jo.put("hospitalRemark",hospitalRemark);
        jo.put("orderNum",orderNum);
        //STEP2：调用接口
        HttpResult result = httpclient.post(Payment_AddHospitalRemark, JSON.toJSONString(jo));
        log.info("body.."+result.getBody());
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        boolean successR = JsonPath.read(body,"$.success");
        boolean resultR = JsonPath.read(body,"$.result");
        Assert.assertTrue(successR);
        Assert.assertTrue(resultR);
        //STEP3:验证医院备注更新，流转日志更新
        if(checkdb){
            //1.收款订单表
            log.info("orderNum"+orderNum);
            PaymentOrder paymentOrder =  OrderChecker.getPaymentOrderInfo(orderNum);
           if(hospitalRemark.equals(""))
                Assert.assertEquals(paymentOrder.getHospitalRemark(),"");
            else
                Assert.assertEquals(paymentOrder.getHospitalRemark(),hospitalRemark);//验证医院备注
            //2.流转日志表
            List<TradeCommonLogResultDTO>  tradeCommonLogResultDTOS = SettleChecker.getTradeCommonLogList(orderNum+"", LogTypeEnum.LOG_TYPE_HOSITALREMARK.getValue(),null);
            TradeCommonLogResultDTO common = tradeCommonLogResultDTOS.get(0);
            Assert.assertEquals(common.getOperatorType().intValue(),1);//CRM操作员
            Assert.assertEquals(common.getOperatorId().intValue(),defSettAccountId);
            Assert.assertEquals(common.getRefSn(),orderNum);
            Assert.assertEquals(common.getOperation(),"添加医院备注");
            Assert.assertEquals(common.getOperatorName(), AccountChecker.getAccountById(defSettAccountId).getName());


        }

    }

    @DataProvider
    public Iterator<String[]> addHospitalRemark(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/addHospitalRemark.csv",10);
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
