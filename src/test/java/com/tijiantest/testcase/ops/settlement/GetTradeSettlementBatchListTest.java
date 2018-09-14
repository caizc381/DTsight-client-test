package com.tijiantest.testcase.ops.settlement;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.tijiantest.base.Flag;
import com.tijiantest.base.HttpResult;
import com.tijiantest.base.dbcheck.CompanyChecker;
import com.tijiantest.base.dbcheck.SettleChecker;
import com.tijiantest.model.settlement.SettlementBatchQueryDTO;
import com.tijiantest.model.settlement.TradeSettlementBatch;
import com.tijiantest.model.settlement.TradeSettlementBatchVo;
import com.tijiantest.testcase.ops.OpsBase;
import com.tijiantest.util.CvsFileUtil;
import com.tijiantest.util.pagination.Page;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*
*
*
* 位置：ops--账务--结算管理--结算批次*/
public class GetTradeSettlementBatchListTest extends OpsBase {
    @Test(groups = {"qa"}, description = "结算批次列表查询", dataProvider = "OPS_SettlementBatchList")
    public void test01_getTradeSettlementBatchList(int channelCompanyId, String orderColumn, boolean isAsc, String pageSize, String... args) {
        String ChannelIdstr = args[1];
        String ChannelCompanyIdstr = args[2];
        String StartTime = args[3];
        String EndTime = args[4];
        String Status = args[5];
        String PageSize = args[6];
        int channelId = -1;
//        int channelCompanyIds = -1;

        int status = -1;
        String start_time = null;
        String end_time = null;
        SettlementBatchQueryDTO dto = new SettlementBatchQueryDTO();
        if (!IsArgsNull(ChannelIdstr)) {
            channelId = Integer.parseInt(ChannelIdstr);
            dto.setChannelId(Integer.valueOf(ChannelIdstr));
        }

        if (!IsArgsNull(ChannelCompanyIdstr)) {
            List<Integer> channelCompanyIds = Arrays.asList(CompanyChecker.getChannelCompanyByOrganizationId(channelId, "id", true).get(0).getId());
            dto.setChannelCompanyIds(ChannelCompanyIdstr);
        }
        if (!IsArgsNull(StartTime)) {
            start_time = StartTime;
            dto.setStartTime(StartTime);
        }
        if (!IsArgsNull(EndTime)) {
            end_time = EndTime;
            dto.setEndTime(EndTime);
        }
        if (!IsArgsNull(Status)) {
            status = Integer.parseInt(Status);
            dto.setStatus(status);
        }
        if (!IsArgsNull(PageSize)) {
            Page page = new Page();
            page.setCurrentPage(1);
            page.setOffset(0);
            page.setPageSize(Integer.parseInt(PageSize));
            dto.setPage(page);
        }
        HttpResult result = httpclient.post(Flag.OPS, OPS_GetTradeSettlementBatchListTest, JSON.toJSONString(dto));
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        System.out.println(body);
        String records = JsonPath.read(result.getBody(), "$.records").toString();
        List<TradeSettlementBatchVo> retList = JSON.parseArray(records, TradeSettlementBatchVo.class);
        log.info("返回结果..." + result.getBody());

        if (checkdb) {
            log.info("channelId" + channelId + "...start_time" + start_time + "...end_time" + end_time + "...status" + status);
            List<TradeSettlementBatch> dbList = SettleChecker.getTradeSettlementBatch1(channelId, start_time, end_time, status, channelCompanyId, orderColumn, isAsc, pageSize);
            Assert.assertEquals(retList.size(), dbList.size());
            for (int i = 0; i < dbList.size(); i++) {
//				log.info("比较批次号.."+dbList.get(i).getSn()+"SS"+retList.get(i).getSn());
                Assert.assertEquals(retList.get(i).getChannelId(), dbList.get(i).getCompanyId());
                Assert.assertEquals(retList.get(i).getChannelCompanyId(), dbList.get(i).getChannelCompanyId());
                Assert.assertEquals(retList.get(i).getId(), dbList.get(i).getId());

                Assert.assertEquals(retList.get(i).getOperatorName(), dbList.get(i).getOperatorName());

                Assert.assertEquals(retList.get(i).getTotalOrderPrice(), dbList.get(i).getChannelCompanyPayAmount());

                Assert.assertEquals(retList.get(i).getSn(), dbList.get(i).getSn());


            }

        }
    }

    @DataProvider
    public Iterator<String[]> OPS_SettlementBatchList() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/settlement/OPS_SettlementBatchList.csv", 18);
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