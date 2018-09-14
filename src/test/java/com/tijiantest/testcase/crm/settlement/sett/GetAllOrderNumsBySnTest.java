package com.tijiantest.testcase.crm.settlement.sett;

import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.*;
import com.tijiantest.testcase.crm.settlement.SettleBase;
import net.minidev.json.JSONArray;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 结算管理->结算批次
 * 选择待确认的批次，点击“修改”按钮，请求此接口
 */
public class GetAllOrderNumsBySnTest extends SettleBase {
    @Test(description = "修改结算批次，查询该批次包括的订单/卡/收款订单",groups = {"qa"})
    public void test_01_getAllOrderNumsBySn(){
        String batchSn = null;
        //随意取任意单位的1个待确认的批次号
        List<TradeSettlementBatch> batchList = SettleChecker.getTradeSettlementBatch(defSettHospitalId,-1,null,null, SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode(),null,false,"1");
        if(batchList!=null && batchList.size()>0){
            batchSn = batchList.get(0).getSn();
        }else{
            log.error("没有待确认的批次号，无法修改批次并进入未结算卡页面");
            return;
        }
//        batchSn = "20180425154956056340265";
        //根据批次号，查询该批次的信息
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("batchSn",batchSn));
        HttpResult result = httpclient.get(GetAllOrderNumsBySn,params);
        String body = result.getBody();
        log.info(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        JSONArray orderNums = JsonPath.read(body,"$.examOrderNums");
        JSONArray paymentOrderNums = JsonPath.read(body,"$.paymentOrderNums");
        JSONArray cardNums = JsonPath.read(body,"$.cardIds");

        if(checkdb){
            //1.比较批次内的体检订单
            List<TradeSettlementOrder> settlementOrders = SettleChecker.getTradeSettleOrderByColumn("batch_sn",batchSn,"is_deleted","0","hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
            Assert.assertEquals(settlementOrders.size(),orderNums.size());
            for(int i=0;i<orderNums.size();i++)
                Assert.assertEquals(orderNums.get(i).toString(),settlementOrders.get(i).getRefOrderNum());

            //2.比较批次内的收款订单
            List<TradeSettlementPaymentOrder> settlementPaymentOrders = SettleChecker.getTradeSettlePaymentOrderByColumn("batch_sn",batchSn,"is_deleted","0","hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
            Assert.assertEquals(paymentOrderNums.size(),settlementPaymentOrders.size());
            for(int i=0;i<paymentOrderNums.size();i++)
                Assert.assertEquals(paymentOrderNums.get(i).toString(),settlementPaymentOrders.get(i).getRefOrderNum());

            //3.比较批次内的卡
            List<TradeSettlementCard> settlementCards = SettleChecker.getTradeSettleCardByColumn("batch_sn",batchSn,"is_deleted","0","hospital_settlement_status",SettlementHospitalConfirmEnum.SETTLEMENT_TO_BE_CONFIRM.getCode()+"");
            Assert.assertEquals(cardNums.size(),settlementCards.size());
            for(int i=0;i<cardNums.size();i++)
                Assert.assertEquals(cardNums.get(i).toString(),settlementCards.get(i).getRefCardId().toString());
        }


    }
}
