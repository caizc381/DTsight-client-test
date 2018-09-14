package com.tijiantest.testcase.main.paymentOrder;

import com.alibaba.fastjson.JSON;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.MyHttpClient;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.account.Account;
import com.tijiantest.model.payment.trade.*;
import com.tijiantest.model.paymentOrder.CreatePaymentOrderDTO;
import com.tijiantest.model.settlement.ExamOrderRefundSettleEnum;
import com.tijiantest.model.settlement.PaymentOrderSettlementDO;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.testcase.crm.CrmBase;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.testcase.main.nologinbook.NoLoginBookTest;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 科室付款
 * 扫客户经理二维码并付款
 * @author huifang
 */
public class CreatePaymentOrderTest extends MainBaseNoLogin {

    @Test(description = "扫描客户经理二维码，并提交科室付款订单",groups = {"qa"},dataProvider = "create_paymentOrder")
    public void test_01_createPaymentOrder(String ...args) throws Exception {
        waitto(1);
        String hospitalStr = args[1];
        String crmUsername = args[2];
        String name = args[3];
        String amountStr = args[4];
        String isWxPayStr = args[5];
        String remark = args[6];
        int hospitalId = Integer.parseInt(hospitalStr);
        int managerId = AccountChecker.getUserInfo(crmUsername,2).getAccount_id();
        //确保微信支付开启
        try {
            if(isWxPayStr.equals("true"))
                DBMapper.update("update tb_hospital_settings set weixin_pay = 1 where hospital_id = "+hospitalId);
            else
                DBMapper.update("update tb_hospital_settings set ali_pay = 1 where hospital_id = "+hospitalId);
        } catch (SqlException e) {
            e.printStackTrace();
        }
        String site = HospitalChecker.getSiteByOrganizationId(hospitalId);
        CreatePaymentOrderDTO order = new CreatePaymentOrderDTO();
        order.setSubSite("/"+site);
        order.setManagerId(managerId);
        order.setHospitalId(hospitalId);
        order.setName(name);
        order.setAmount(Long.parseLong(amountStr));
        order.setRemark(remark);
        order.setIsWxPay(Boolean.parseBoolean(isWxPayStr));
        HttpResult result = null;
        if(isWxPayStr.equals("true")) //微信需要传入openid
            //"openid=oButAv1OzV6XnDUmACp8s2wjngO0"
            //o33PT0l819kPQW0lyhxDU9Yw5irQ
        {
            String openid = envConf.getValue(ConfDefine.WX, ConfDefine.OPENID);
            order.setOpenid(openid);
            result = hc3.post(Flag.MAIN,Payment_CreatePaymentOrder,JSON.toJSONString(order));
        }
        else
            result = hc3.post(Flag.MAIN,Payment_CreatePaymentOrder,JSON.toJSONString(order));
        log.info("body"+result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        Assert.assertTrue(result.getBody().contains("success\":true"));
        
        if(checkdb){
            //1.付款订单表
            String sql = "select * from tb_payment_order order by id desc limit 1";
            List<Map<String,Object>> dblist = DBMapper.query(sql);
            Assert.assertEquals(dblist.size(),1);
            Map<String,Object> map = dblist.get(0);
            String orderNum = map.get("order_num").toString();
            Assert.assertEquals(map.get("payment_name").toString(),name);
            Assert.assertEquals(Integer.parseInt(map.get("manager_id").toString()),managerId);
            Assert.assertEquals(Integer.parseInt(map.get("organization_id").toString()),hospitalId);
            int gr = CompanyChecker.getHospitalCompanyByPlatCompanyIdANDOrganizationId(2,hospitalId).getId();
            Assert.assertEquals(Integer.parseInt(map.get("hospital_company_id").toString()),gr);
            Assert.assertEquals(map.get("amount").toString(),amountStr);
            Assert.assertEquals(map.get("amount").toString(),amountStr);
            Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0); //未支付
            Assert.assertEquals(map.get("remark").toString(),remark);
            Assert.assertEquals(Integer.parseInt(map.get("is_delete").toString()),0);
            Assert.assertEquals(sdf.format(sdf.parse(map.get("gmt_created").toString())),sdf.format(new Date()));
            Assert.assertEquals(sdf.format(sdf.parse(map.get("gmt_modified").toString())),sdf.format(new Date()));
            //2.付款订单结算表
            List<PaymentOrderSettlementDO> orderSettlist= SettleChecker.getPaymentOrderSettleByColumn("order_num","'"+orderNum+"'");
            Assert.assertEquals(orderSettlist.size(),1);
            PaymentOrderSettlementDO orderSettDO = orderSettlist.get(0);
            Assert.assertEquals(orderSettDO.getOrganizationId().intValue(),hospitalId);
            Assert.assertEquals(orderSettDO.getHospitalCompanyId().intValue(),gr);
            Assert.assertEquals(orderSettDO.getHospitalSettlementStatus().intValue(), SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode().intValue());
            Assert.assertEquals(orderSettDO.getPaymentName(),name);
            Assert.assertEquals(orderSettDO.getRefundSettlement().intValue(), ExamOrderRefundSettleEnum.NOT_NEED_REFUND.getCode().intValue());
            Assert.assertNull(orderSettDO.getSettlementBatchSn());
            Assert.assertEquals(orderSettDO.getIsDeleted().intValue(),0);
            Assert.assertEquals(orderSettDO.getOrderType().intValue(), PayConstants.OrderType.PaymentOrder);
            Assert.assertEquals(sdf.format(orderSettDO.getGmtCreated()),sdf.format(new Date()));
            Assert.assertEquals(sdf.format(orderSettDO.getGmtModified()),sdf.format(new Date()));

            //3.交易订单表
            TradeOrder tradeOrder =  PayChecker.getTradeOrderByOrderNum(orderNum,PayConstants.TradeType.pay).get(0);
            Assert.assertEquals(tradeOrder.getRefOrderType().intValue(),PayConstants.OrderType.PaymentOrder); //付款订单类型
            Assert.assertEquals(tradeOrder.getAmount().longValue(),Long.parseLong(amountStr)); //金额相等
            Assert.assertEquals(tradeOrder.getSuccAmount().longValue(),0l);//成功支付金额为0
            Assert.assertEquals(tradeOrder.getTradeStatus().intValue(),PayConstants.TradeStatus.Paying); //支付中
            if(isWxPayStr.equals("true"))
                Assert.assertEquals(tradeOrder.getPayMethodType().intValue(),PayConstants.PayMethodBit.WxpayBit);//微信
            else
                Assert.assertEquals(tradeOrder.getPayMethodType().intValue(),PayConstants.PayMethodBit.AlipayBit);//支付寶


            //4.交易支付表
            List<TradePayRecord>  payRecords = PayChecker.getTradePayRecordByOrderNum(orderNum,null,PayConstants.OrderType.PaymentOrder);
            Assert.assertEquals(payRecords.size(),1);
            TradePayRecord payRecord = payRecords.get(0);
            Assert.assertEquals(payRecord.getRefOrderType().intValue(),PayConstants.OrderType.PaymentOrder);
            if(isWxPayStr.equals("true")){
                Assert.assertEquals(payRecord.getTradeMethodType().intValue(),PayConstants.PayMethod.Wxpay);
                int dbReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(hospitalId,
                        PayConstants.PayMethodBit.WxpayBit);
                Assert.assertEquals(payRecord.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
                Assert.assertEquals(payRecord.getReceiveTradeSubaccountId().intValue(),
                        PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
            }
            else{
                Assert.assertEquals(payRecord.getTradeMethodType().intValue(),PayConstants.PayMethod.Alipay);
                int dbReceiveTradeAccountId = PayChecker.getSuitableReceiveMethodId(hospitalId,
                        PayConstants.PayMethodBit.AlipayBit);
                Assert.assertEquals(payRecord.getReceiveTradeAccountId().intValue(),dbReceiveTradeAccountId);
                Assert.assertEquals(payRecord.getReceiveTradeSubaccountId().intValue(),
                        PayChecker.getSubAccounttingId(dbReceiveTradeAccountId));
            }
            Assert.assertEquals(payRecord.getReceiveTradeSubaccountType().intValue(),TradeSubAccountType.TRADE_BALANCE_ACCOUNT);
            Assert.assertEquals(payRecord.getPayAmount().longValue(),Long.parseLong(amountStr)); //金额相等
            Assert.assertEquals(payRecord.getPayStatus().intValue(),PayConstants.TradeStatus.Paying); //支付中
            Assert.assertNull(payRecord.getPayTradeSubaccountType());//三方子账号类型为空

        }
    }

    @DataProvider
    public Iterator<String[]> create_paymentOrder(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/paymentOrder/create_paymentOrder.csv",10);
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
