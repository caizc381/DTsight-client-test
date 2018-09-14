package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;

import com.tijiantest.base.HttpResult;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.db.MongoDBUtils;
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
/*
* author:qfm
* 位置：C端--个人中心--医院页面--成就
* 统计预约人次和订单金额*/

public class countOrderTest extends MainBase{
    @Test(description = "统计订单金额和预约人次",groups = "qa",dataProvider = "countOrder")
    public void test_01_countOrder(String...args){
        String hospitalstr = args[1];
        String orderStr = args[2];
        List<Integer> managerIds = new ArrayList<>();
        managerIds.add(55844);

        List<Integer> orderStatuses = new ArrayList<Integer>();
        int hospitalId = -1;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("_site","xixi"));
        params.add(new BasicNameValuePair("_p",""));
        params.add(new BasicNameValuePair("_siteType","mobile"));

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        if(!IsArgsNull(hospitalstr)){
            hospitalId = Integer.parseInt(hospitalstr);
            orderQueryParams.setHospitalId(hospitalId);
        }
        if(!IsArgsNull(orderStr)){
            String orders[] = orderStr.split("#");
            orderStatuses = ListUtil.StringArraysToIntegerList(orders);
            orderQueryParams.setOrderStatuses(orderStatuses);
        }
        orderQueryParams.setManagerIds(managerIds);
        String jsonStr = JSON.toJSONString(orderQueryParams);
        HttpResult result = httpclient.post(Flag.MAIN,MainCountOrder,params,jsonStr);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        log.info("立即返回"+body);
       // double totalPerson = Double.parseDouble(JsonPath.read(body,"$.totalPerson").toString());
        double totalPrice = Double.parseDouble(JsonPath.read(body,"$totalPrice").toString());
        double personCount = Double.parseDouble(JsonPath.read(body,"$.personCount").toString());


        int dbTotalPerson = 0;
        double dbTotalPrice = 0;

        if(checkmongo){
            String querySql = "{";
            if(hospitalId != -1){
                querySql += "\"orderHospital._id\":"+hospitalId+"";
            }
            if(orderStatuses !=null && orderStatuses.size()>0){
                querySql += ",\"status\":{$in:["+ListUtil.IntegerlistToString(orderStatuses)+"]}";
            }
            querySql+="}";
            log.info("querySql"+querySql);
            List<Map<String,Object>> mongolist = null;
            if(hospitalstr!=null){
                mongolist = MongoDBUtils.query(querySql,MONGO_COLLECTION);
            }

            for(int i = 0;i<mongolist.size();i++){
                Map<String,Object> mogoMap = mongolist.get(i);
//                int person = Integer.parseInt(mogoMap.get("person").toString());
                double orderPriceInt = Double.parseDouble(mogoMap.get("orderPrice").toString());
                dbTotalPerson = mongolist.size();

                dbTotalPrice += orderPriceInt;
            }
            log.info("返回，预约人次"+personCount);
            log.info("返回，订单金额"+totalPrice);
            Assert.assertEquals(personCount,Double.parseDouble(String.valueOf(dbTotalPerson)));
            Assert.assertEquals((long)(totalPrice*100),(long)dbTotalPrice);

        }
    }
    @DataProvider
    public Iterator<String[]> countOrder(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/order/countOrder.csv",18);
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

