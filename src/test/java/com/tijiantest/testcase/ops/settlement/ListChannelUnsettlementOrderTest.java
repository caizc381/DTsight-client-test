package com.tijiantest.testcase.ops.settlement;

import com.alibaba.fastjson.JSON;
//import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.OrderChecker;
import com.tijiantest.model.order.*;
import com.tijiantest.model.settlement.SettlementHospitalConfirmEnum;
import com.tijiantest.model.settlement.SettlementPageDTO;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.ListUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.*;

/*
*
* author:qfm
*
* update by huifang
* 位置：ops--账务--渠道结算--订单结算的订单列表
*
* */
public class ListChannelUnsettlementOrderTest extends OpsBase{
    @Test(description = "订单列表",groups = {"qa"},dataProvider = "ops_create_sett")
    public void test01_listChannelUnsettlementOrderTest(String... args) throws ParseException {
        String channelCompanyIdStr = args[1];
        String fromSiteOrgTypeStr = args[2];
        String organizationIdStr = args[3];
        String placeOrderStartTimeStr = args[4];
        String placeOrderEndTimeStr = args[5];
        String examStartTimeStr = args[6];
        String examEndTimeStr = args[7];
        String orderStatusesStr = args[8];
        String pageSizeStr = args[9];
        int organizationId = -1;
        int channelCompanyId = -1;
        int pageSize = -1;
        int cureentPage = 1;
        int fromSiteOrgType = Integer.parseInt(fromSiteOrgTypeStr);
        int isExport = 1;
        String examStartTime = null;
        String examEndTime = null;
        String placeOrderStartTime = null;
        String placeOrderEndTime = null;
        ChannelUnsettlementOrderQueryDTO channelOrderQueryParams = new ChannelUnsettlementOrderQueryDTO();
        List<Integer> orderStatuses = new ArrayList<Integer>();
        channelOrderQueryParams.setFromSiteOrgType(fromSiteOrgType);
        if(!IsArgsNull(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
            channelOrderQueryParams.setPage(new Page(cureentPage,pageSize));
        }
        if(!IsArgsNull(examStartTimeStr)){
            examStartTime = examStartTimeStr;
            channelOrderQueryParams.setExamStartTime(examStartTime);

        }
        if(!IsArgsNull(examEndTimeStr)){
            examEndTime = examEndTimeStr;
            channelOrderQueryParams.setExamEndTime(examEndTime);
        }
        if(!IsArgsNull(placeOrderStartTimeStr)){
            placeOrderStartTime = placeOrderStartTimeStr;
            channelOrderQueryParams.setPlaceOrderStartTime(placeOrderStartTime);

        }
        if(!IsArgsNull(placeOrderEndTimeStr)){
            placeOrderEndTime = placeOrderEndTimeStr;
            channelOrderQueryParams.setPlaceOrderEndTime(placeOrderEndTime);
        }
        if(!IsArgsNull(organizationIdStr)){
            organizationId = Integer.parseInt(organizationIdStr);
            channelOrderQueryParams.setOrganizationId(organizationId);
        }
        if(!IsArgsNull(channelCompanyIdStr)){
            channelCompanyId = Integer.parseInt(channelCompanyIdStr);
            channelOrderQueryParams.setChannelCompanyId(channelCompanyId);
        }

        if(!IsArgsNull(orderStatusesStr)){
            String orderStatusStr[] = orderStatusesStr.split("#");
            List<Integer> orderStatus = ListUtil.StringArraysToIntegerList(orderStatusStr);
            channelOrderQueryParams.setStatus(orderStatus);
        }else
            channelOrderQueryParams.setStatus(new ArrayList<>());

        HttpResult result = httpclient.post(Flag.OPS,OPS_ListChannelUnsettlementOrder, JSON.toJSONString(channelOrderQueryParams));
        String body = result.getBody();
        System.out.println(body);

        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

//        String records = JsonPath.read(result.getBody(),"$records".toString());
        String records = JsonPath.read(result.getBody(), "$.records").toString();
        List<SettlementPageDTO> chanelist = JSON.parseArray(records,SettlementPageDTO.class);

/*
* String records = JsonPath.read(response.getBody(), "$.records").toString();
		String body = response.getBody();
		System.out.println(body);
		List<TradeSettlementPayRecord> retList = JSON.parseArray(records, TradeSettlementPayRecord.class);*/


        if(checkdb){
            String sql =
                    "SELECT * FROM tb_order WHERE order_num in (SELECT order_num FROM tb_exam_order_settlement WHERE channel_settlement_status in ("+ SettlementHospitalConfirmEnum.UNSETTLEMENT.getCode()+","+SettlementHospitalConfirmEnum.SETTLEMENT_CANCELED.getCode()+")) " +
                    "and  from_site_org_type= " +fromSiteOrgType+" and is_export= " +isExport+" ";

            if(examStartTime != null)
                sql += " and exam_date >= '"+examStartTime+"'";
            if(examEndTime != null)
                sql += " and exam_date <= '"+examEndTime+"'";
            if(placeOrderStartTime != null)
                sql += " and insert_time >= '"+placeOrderStartTime+"'";
            if(placeOrderEndTime != null)
                sql += " and insert_time <= '"+placeOrderEndTime+"'";

            if(organizationId != -1)
                sql += " and from_site = "+organizationId;
            if(channelCompanyId != -1)
                sql += " and channel_company_id = "+channelCompanyId;
            if(channelOrderQueryParams.getStatus() == null || channelOrderQueryParams.getStatus().size() == 0)
                sql += " and  status in ("+OrderStatus.ALREADY_BOOKED.intValue()+","+OrderStatus.EXAM_FINISHED.intValue()+","+OrderStatus.PART_BACK.intValue()+")";
            else
                sql += " and status in ("+ListUtil.IntegerlistToString(channelOrderQueryParams.getStatus())+")";
            System.out.println(sql);
            List<Order> orderCheckerList = OrderChecker.getordersList(sql);
            Assert.assertEquals(chanelist.size(),orderCheckerList.size());


        }

    }

    @DataProvider
    public Iterator<String[]> ops_create_sett(){
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/ops_create_sett.csv",7);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
