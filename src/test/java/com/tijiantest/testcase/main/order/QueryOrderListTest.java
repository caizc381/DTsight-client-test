package com.tijiantest.testcase.main.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.account.ManagerChannelRelDO;
import com.tijiantest.model.order.Order;
import com.tijiantest.model.order.OrderMarketingVo;
import com.tijiantest.model.order.OrderQueryParams;
import com.tijiantest.model.order.OrderStatusEnum;
import com.tijiantest.model.order.snapshot.MealSnapshot;
import com.tijiantest.testcase.main.MainBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * author:qfm
 * 位置：C端--个人中心--医院页面--成就
 * 根据下单时间和订单查看订单列表*/

public class QueryOrderListTest extends MainBase{
//    public static List<Integer> orderList = new ArrayList<Integer>();
    @Test(groups ="qa",description = "医生页面查看营销客户经理成就页面订单",dataProvider = "queryOrder")
    public void test_01_queryOrderList( String... args) throws ParseException {
        String examStartDateStr=args[1];
        String examEndDateStr=args[2];
        String insertStartDateStr=args[3];
        String insertEndDateStr=args[4];
        String OrderStatus = args[6];
        String name = args[8];
        String price =args[9];
        String hospital = args[5];
        String pagesizestr = args[10];
        String currentpagestr = args[11];
        String manageridlist = args[12];
        int rowCount = -1;
        int pageSize = -1;
        int currentPage = -1;


        Date examStartDate = null;
        Date examEndDate = null;
        Date insertStartDate = null;
        Date insertEndDate = null;
        List<Integer> marketingManagerId= new ArrayList<>();
        marketingManagerId.add(Integer.parseInt(manageridlist));
//        int type = 2;
        Integer defHospitalId = 5;

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        if(!IsArgsNull(examStartDateStr)){
            examStartDate = simplehms.parse(examStartDateStr);
            orderQueryParams.setExamStartDate(examStartDate);
        }
        if(!IsArgsNull(examEndDateStr)){
            examEndDate = simplehms.parse(examEndDateStr);
            orderQueryParams.setExamEndDate(examEndDate);
        }
        if(!IsArgsNull(insertStartDateStr)){
            insertStartDate = simplehms.parse(insertStartDateStr);
            System.out.println(insertStartDate);
            orderQueryParams.setInsertStartDate(insertStartDate);
        }
        if(!IsArgsNull(insertEndDateStr)){
            insertEndDate =simplehms.parse(insertEndDateStr);
            orderQueryParams.setInsertEndDate(insertEndDate);
        }
//        orderQueryParams.setManagerIds(manageridlist);
         /*

        分页*/
        Page page= new Page();
        if(!IsArgsNull(pagesizestr)){
            pageSize = Integer.parseInt(pagesizestr);
            page.setPageSize(Integer.parseInt(pagesizestr));
        }

        if(!IsArgsNull(currentpagestr)){
            currentPage = Integer.parseInt(currentpagestr);
            page.setCurrentPage(Integer.parseInt(currentpagestr));
        }
        orderQueryParams.setPage(page);
        orderQueryParams.setManagerIds(marketingManagerId);
        String jsonStr = JSON.toJSONString(orderQueryParams);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("_site","xixi"));
        params.add(new BasicNameValuePair("_p",""));
        params.add(new BasicNameValuePair("_siteType","mobile"));
        HttpResult result = httpclient.post(Flag.MAIN,MainQueryOrder,params,jsonStr);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        log.info(body);
        Page retPage = JSONObject.parseObject(JsonPath.read(result.getBody(),"$.page").toString(),Page.class);
        List<OrderMarketingVo> retOrderList = JSONArray.parseArray(JsonPath.read(body,"$.records").toString(),OrderMarketingVo.class);
        //List<Order> retOrderList
        if(checkdb){
            String sql = "select * from tb_order where hospital_id= "+defHospitalId ;
            if(insertStartDate!=null &&insertEndDate!=null){
                sql +=" and insert_time between  '"+insertStartDateStr+"'  and  '"+insertEndDateStr+"' order by insert_time desc limit "+pageSize;
                System.out.println(sql);
            }
           if(examStartDate!=null && examEndDate!=null){
               sql +=" and exam_date between  '"+examStartDateStr+"'  and  '"+examEndDateStr+"' order by insert_time desc limit "+pageSize;
                System.out.println(sql);
            }

            List<Order> orderCheckerList = OrderChecker.getordersList(sql);

            /*
            * 比较数据库数据和接口数据的一致性*/
//            Assert.assertEquals(orderCheckerList.size(),retPage.getRowCount());
            if(pageSize != -1)
                pageSize +=1;
//            Assert.assertEquals(orderCheckerList.size(),retPage.getRowCount());
            Assert.assertEquals(orderCheckerList.size(),retOrderList.size());

            if(orderCheckerList!=null && orderCheckerList.size()>0){
                Order order = orderCheckerList.get(0);
                double tempPrice = orderCheckerList.get(0).getOrderPrice()/100.0;
                BigDecimal bPrice = BigDecimal.valueOf(tempPrice);
                bPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
//                for(int i=0;i<retOrderList.size();i++){
                    Assert.assertEquals(retOrderList.get(0).getOrderPrice(),bPrice.toString());
                    Assert.assertEquals(retOrderList.get(0).getStatus(), OrderStatusEnum.getByCode(orderCheckerList.get(0).getStatus()).getValue());
                    Assert.assertEquals(retOrderList.get(0).getExamTime().split(" ")[0],new SimpleDateFormat("yyyy-MM-dd").format(orderCheckerList.get(0).getInsertTime()));
//                }

            }


        }

    }

    @DataProvider
    public Iterator<String[]> queryOrder(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/order/queryOrder.csv",18);
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
