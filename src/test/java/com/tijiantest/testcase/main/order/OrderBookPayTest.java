package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.ConfDefine;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.*;
import com.tijiantest.model.hospital.HospitalPeriodSetting;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderMarketingParam;
import com.tijiantest.model.order.OrderStatus;
import com.tijiantest.model.payment.trade.PayConstants;
import com.tijiantest.model.payment.trade.TradeAccountDetail;
import com.tijiantest.model.payment.trade.TradeOrder;
import com.tijiantest.model.payment.trade.TradePayRecord;
import com.tijiantest.model.resource.meal.Meal;
import com.tijiantest.model.resource.meal.MealGenderEnum;
import com.tijiantest.testcase.main.MainBaseNoLogin;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.IdCardGeneric;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.DBMapper;
import com.tijiantest.util.db.MongoDBUtils;
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
 * 全民营销用户预约下单支付一体化操作
 * @author huifang
 */
public class OrderBookPayTest extends MainBaseNoLogin {

    @Test(description = "全民营销预约下单线下支付一体操作,现场支付方式",groups = {"qa"},dataProvider = "orderBookPay")
    public void test_01_orderBookPay(String ...args) throws SqlException {
//        MyHttpClient myHttpClient = new MyHttpClient();
        //入参构造
        String name = args[1];
        String mobile = args[3];
        String marriageStatusStr = args[4];
        String examDate = args[5];
        String payTypeStr = args[6];
        int payType = Integer.parseInt(payTypeStr);
        int gender = MealGenderEnum.FEMALE.getCode();
        int examTimeIntervalId = -1;
        String idcard = new IdCardGeneric().generateGender(gender);
        List<NameValuePair> codeParams = new ArrayList<NameValuePair>();
        NameValuePair _site = new BasicNameValuePair("_site",defSite);
        NameValuePair _siteType = new BasicNameValuePair("_siteType", "mobile");
        NameValuePair umobile = new BasicNameValuePair("mobile", mobile);
        String verifyCode = null;
        codeParams.add(_site);
        codeParams.add(_siteType);
        codeParams.add(umobile);
        
        //STEP1:获取token
        HttpResult result = hc4.get(Flag.MAIN,ValidateLoginAddToken,codeParams);
        log.info("登陆验证 ..."+result.getBody());
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        if(result.getBody().contains("true")){
            onceLogOutSystem(hc4,Flag.MAIN);
            result = hc4.get(Flag.MAIN,ValidateLoginAddToken,codeParams);
            log.info("退出登录,再次登陆验证 ..."+result.getBody());
            Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
            Assert.assertTrue(result.getBody().contains("false"));
        }


        //step2:获取手机验证码
       
        result = hc4.post(Flag.MAIN,Account_MobileValidationCode,codeParams);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        Assert.assertEquals(result.getBody(), "{}");
        if(checkdb){
            String sql = "select * from tb_sms_send_record  where mobile = '"+mobile+"' order by id desc limit 1";
            List<Map<String,Object>> smslist = DBMapper.query(sql);
            String sms = smslist.get(0).get("content").toString();
            verifyCode = sms.split("：")[1].split("，")[0];
            log.info("verifyCode..."+verifyCode);
        }
        
        //STEP3:构造全民营销支付参数
        List<NameValuePair> bookParams = new ArrayList<>();

        //官方套餐项目
        List<com.tijiantest.model.resource.meal.Meal> mealList = ResourceChecker.getOfficialMealList(defHospitalId,gender);
        Meal meal = mealList.get(0);
        List<Integer> itemIds = ResourceChecker.getMealExamItemIdList(meal.getId());
        bookParams.add(new BasicNameValuePair("_site",defSite));
        bookParams.add(new BasicNameValuePair("_siteType","mobile"));
        bookParams.add(new BasicNameValuePair("_p",""));

        OrderMarketingParam marketingParam = new OrderMarketingParam();
        List<Integer> managerList = AccountChecker.getMarketingManagerId(defHospitalId);
        if(managerList.size()>0)
            marketingParam.setMarketingManagerId(managerList.get(0));//全民营销客户经理账号id
        String openid = envConf.getValue(ConfDefine.WX, ConfDefine.OPENID);
        marketingParam.setWxOpenId(openid);
        marketingParam.setPayType(payType);
        marketingParam.setUseBalance(false);
        marketingParam.setNeedPaperReport(false);
        marketingParam.setInLocation(false);
        marketingParam.setExamDate(examDate);
        List<HospitalPeriodSetting>  periodSettings = HospitalChecker.getHospitalPeriodSettings(defHospitalId);
        if(periodSettings != null ){
            examTimeIntervalId = periodSettings.get(0).getId();
            marketingParam.setExamTimeIntervalId(examTimeIntervalId);
        }
        marketingParam.setItemIds(itemIds);
        marketingParam.setMealId(meal.getId());
        marketingParam.setMealPrice(meal.getPrice());
        marketingParam.setIdCard(idcard);
        marketingParam.setMarriageStatus(Integer.parseInt(marriageStatusStr));
        marketingParam.setClient("wap");
        marketingParam.setName(name);
        marketingParam.setMobile(mobile);
        marketingParam.setValidationCode(verifyCode);
        marketingParam.setHospitalId(defHospitalId);
        marketingParam.setSubSite("/"+defSite);

        String beforeDate = simplehms.format(DBMapper.query("select now()").get(0).get("now()"));
        waitto(1);

        result = hc4.post(Flag.MAIN,Main_OrderBookPay, bookParams, JSON.toJSONString(marketingParam));
        log.info(result.getBody());
        Assert.assertEquals(result.getCode(),HttpStatus.SC_OK);
        //检查下单返回值，异常处理
        OrderChecker.checkMainOrderBookPayResponse(hc4,result,bookParams,defaccountId,-1,defHospitalId);

        boolean success = JsonPath.read(result.getBody(),"$.success");
        int orderId = JsonPath.read(result.getBody(),"$.orderId");
        int requireAmount = JsonPath.read(result.getBody(),"$.requireAmount");
        int successAmount = JsonPath.read(result.getBody(),"$.successAmount");
        int payingAmount = JsonPath.read(result.getBody(),"$.payingAmount");


        if(checkdb) {

            Order order = OrderChecker.getOrderInfo(orderId);
            int accountId = order.getOrderAccount().getId();
            //1.验证账号添加正确

                //tb_user
                String sql = "select * from tb_user where username = \'"+idcard +"\'";
                List<Map<String,Object>> list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                int examinerAccountId = Integer.parseInt(list.get(0).get("account_id").toString());//体检人accountId

                sql = "select * from tb_user where username = \'"+mobile +"\'";
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                int selfAccountId = Integer.parseInt(list.get(0).get("account_id").toString());
                Assert.assertNotEquals(examinerAccountId,selfAccountId);
                //tb_account
                sql = "select * from tb_account where id = "+examinerAccountId; //体检人账户
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                Map<String,Object> map = list.get(0);
                Assert.assertEquals(map.get("name").toString(),name);
                Assert.assertEquals(map.get("idcard").toString(),idcard);
                Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);
                Assert.assertEquals(Integer.parseInt(map.get("type").toString()),3);

                sql = "select * from tb_account where id = "+selfAccountId; //本人账户
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                map = list.get(0);
                Assert.assertEquals(map.get("mobile").toString(),mobile);
                Assert.assertEquals(Integer.parseInt(map.get("status").toString()),0);

                //tb_account_role
                sql = "select * from tb_account_role where account_id = "+examinerAccountId;
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                //tb_accounting
                sql = "select * from tb_accounting where account_id = "+examinerAccountId;
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                int trade_account_id = Integer.parseInt(list.get(0).get("trade_account_id").toString());
                map = list.get(0);
                Assert.assertEquals(Integer.parseInt(map.get("balance").toString()),0);
                //tb_examiner
                sql = "select * from tb_examiner where customer_id = "+examinerAccountId + " order by update_time desc ";
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                map = list.get(0);
                Assert.assertEquals(map.get("mobile").toString(),mobile);
                Assert.assertEquals(Integer.parseInt(map.get("is_self").toString()),0);
                Assert.assertEquals(Integer.parseInt(map.get("relation_id").toString()),selfAccountId);

                //tb_nologin_account_info
                sql = "select * from tb_nologin_account_info where account_id = "+selfAccountId + " and gmt_created >= '"+beforeDate+"'";
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                map = list.get(0);
                Assert.assertEquals(map.get("name").toString(),name);
                Assert.assertEquals(map.get("mobile").toString(),mobile);
                Assert.assertEquals(Integer.parseInt(map.get("marriagestatus").toString()),Integer.parseInt(marriageStatusStr));

                //tb_trade_account
                sql = "select * from tb_trade_account where ref_id = "+examinerAccountId;
                list = DBMapper.query(sql);
                Assert.assertEquals(list.size(),1);
                Assert.assertEquals(trade_account_id,Integer.parseInt(list.get(0).get("id").toString()));



               //2.验证订单&交易
              Assert.assertEquals(order.getOrderPrice().longValue(), requireAmount);//订单金额
              Assert.assertEquals(sdf.format(order.getExamDate()),examDate);//日期
              Assert.assertEquals(order.getExamTimeIntervalId().intValue(),examTimeIntervalId);//时段
              Assert.assertEquals(order.getHospital().getId(),defHospitalId);//体检中心
              Assert.assertEquals(order.getSource(),5); //免登陆订单
              int orderMealId = order.getOrderMealSnapshot().getMealSnapshot().getOriginMeal().getId();
              Assert.assertEquals(orderMealId,meal.getId().intValue());//订单套餐ID
             if(checkmongo){
                waitto(mongoWaitTime);
                List<Map<String,Object>> mlist = MongoDBUtils.query("{'id':"+orderId+"}", MONGO_COLLECTION);
                Assert.assertNotNull(mlist);
                Assert.assertEquals(1, mlist.size());
                if(payType == 7){
                    if (successAmount < requireAmount)
                        Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.SITE_PAY.intValue());
                    else
                        Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.ALREADY_BOOKED.intValue());
                }else if(payType == 4)
                    Assert.assertEquals(Integer.parseInt(mlist.get(0).get("status").toString()),OrderStatus.PAYING.intValue());
                Assert.assertEquals(Integer.parseInt(mlist.get(0).get("source").toString()),5); //免登陆订单
            }

            if (successAmount < requireAmount) {
                //订单状态 /价格
                if(payType == 7)
                    Assert.assertEquals(order.getStatus(), OrderStatus.SITE_PAY.intValue());
                else if(payType == 4)
                        Assert.assertEquals(order.getStatus(), OrderStatus.PAYING.intValue());

                    //交易记录表
                List<TradeOrder> tradeOrder = PayChecker.getTradeOrderByOrderNum(order.getOrderNum(), PayConstants.TradeType.pay);
                Assert.assertEquals(tradeOrder.size(), 1);
                TradeOrder tradeOrder1 = tradeOrder.get(0);
                Assert.assertEquals(tradeOrder1.getTradeStatus().intValue(), PayConstants.TradeStatus.Paying);
                Assert.assertEquals(tradeOrder1.getAmount().intValue(), requireAmount);
                Assert.assertEquals(tradeOrder1.getSuccAmount().intValue(), successAmount);
                //交易支付表
                List<TradePayRecord> payRecordList = PayChecker.getTradePayRecordByOrderNum(order.getOrderNum(), tradeOrder1.getTradeOrderNum(), PayConstants.OrderType.MytijianOrder);
                Assert.assertEquals(payRecordList.size(), 1);
                TradePayRecord payRecord = payRecordList.get(0);
                if(payType == 7)
                 Assert.assertEquals(payRecord.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
                else if(payType == 4)
                    Assert.assertEquals(payRecord.getTradeMethodType().intValue(), PayConstants.PayMethod.Wxpay);
                Assert.assertEquals(payRecord.getPayAmount().intValue(), requireAmount);
                Assert.assertEquals(payRecord.getPayStatus().intValue(), PayConstants.TradeStatus.Paying);
                //交易流水表
                List<TradeAccountDetail> tradeAccountDetails = PayChecker.getTradeAccountDetail(tradeOrder1.getTradeOrderNum(), 0);
                Assert.assertEquals(tradeAccountDetails.size(), 0);
                tradeAccountDetails = PayChecker.getTradeAccountDetail(tradeOrder1.getTradeOrderNum(), 1);
                Assert.assertEquals(tradeAccountDetails.size(), 0);

            } else {
                //订单状态 /价格
                Assert.assertEquals(order.getStatus(), OrderStatus.ALREADY_BOOKED.intValue());
                //交易记录表
                List<TradeOrder> tradeOrder = PayChecker.getTradeOrderByOrderNum(order.getOrderNum(), PayConstants.TradeType.pay);
                Assert.assertEquals(tradeOrder.size(), 1);
                TradeOrder tradeOrder1 = tradeOrder.get(0);
                Assert.assertEquals(tradeOrder1.getTradeStatus().intValue(), PayConstants.TradeStatus.Successful);
                Assert.assertEquals(tradeOrder1.getAmount().longValue(), requireAmount);
                Assert.assertEquals(tradeOrder1.getSuccAmount().longValue(), successAmount);
                //交易支付表
                List<TradePayRecord> payRecordList = PayChecker.getTradePayRecordByOrderNum(order.getOrderNum(), tradeOrder1.getTradeOrderNum(), PayConstants.OrderType.MytijianOrder);
                Assert.assertEquals(payRecordList.size(), 1);
                TradePayRecord payRecord = payRecordList.get(0);
                Assert.assertEquals(payRecord.getTradeMethodType().intValue(), PayConstants.PayMethod.OfflinePay);
                Assert.assertEquals(payRecord.getPayAmount().longValue(), requireAmount);
                Assert.assertEquals(payRecord.getPayStatus().intValue(), PayConstants.TradeStatus.Successful);
                //交易流水表(线下支付无交易流水)
                List<TradeAccountDetail> tradeAccountDetails = PayChecker.getTradeAccountDetail(tradeOrder1.getTradeOrderNum(), 0);
                Assert.assertEquals(tradeAccountDetails.size(), 0);
                tradeAccountDetails = PayChecker.getTradeAccountDetail(tradeOrder1.getTradeOrderNum(), 1);
                Assert.assertEquals(tradeAccountDetails.size(), 0);
            }

            //3.验证结算
            OrderChecker.check_Book_ExamOrderSettlement(order);
            onceLogOutSystem(hc4,Flag.MAIN);
        }

    }


    @DataProvider
    public Iterator<String[]> orderBookPay() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/order/orderBookPay.csv", 8);
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
