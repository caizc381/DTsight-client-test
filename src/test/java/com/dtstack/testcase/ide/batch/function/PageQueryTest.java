package com.dtstack.testcase.ide.batch.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.batch.BatchFunctionChecker;
import com.dtstack.base.dbcheck.ide.common.UserChecker;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.domain.ide.batch.BatchFunction;
import com.dtstack.model.dto.ide.batch.BatchFunctionDTO;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.CvsFileUtil;
import com.dtstack.util.DateUtils;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class PageQueryTest extends IdeBase {
    @Test(description = "自定义函数分页查询", groups = {"qa"}, dataProvider = "pageQuery")
    public void test_01_pageQuery(String... args) throws ParseException, SqlException {
        String endTime = args[1];
        String pageIndex = args[2];
        String pageSize = args[3];
        String startTime = args[4];
        String functionModifyUserId = args[5];
        String name = args[6];
        BatchFunctionDTO batchFunctionDTO = new BatchFunctionDTO();

        Timestamp stampEndTime;
        Timestamp stampStartTime;
        if (endTime != null && !endTime.equals("")) {
            stampEndTime = DateUtils.dateToStamp(endTime + " 00:00:00");
            batchFunctionDTO.setEndTime(stampEndTime);
        }
        if (startTime != null && !startTime.equals("")) {
            stampStartTime = DateUtils.dateToStamp(startTime + " 00:00:00");
            batchFunctionDTO.setStartTIme(stampStartTime);
        }


        batchFunctionDTO.setPageIndex(Integer.valueOf(pageIndex));
        batchFunctionDTO.setPageSize(Integer.valueOf(pageSize));


        if (functionModifyUserId != null && !functionModifyUserId.equals("")) {
            batchFunctionDTO.setFunctionModifyUserId(Long.valueOf(functionModifyUserId));
        }
        if (name != null && !name.equals("")) {
            batchFunctionDTO.setFunctionName(name);
        }

        batchFunctionDTO.setTenantId(defRdosTenantId);
        batchFunctionDTO.setProjectId(defProjectId);

        String json = JSON.toJSONString(batchFunctionDTO);
        HttpResult result = httpclient.post(Flag.IDE, BatchFunction_PageQuery, json);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        JSONArray array = JSONArray.parseArray(JsonPath.read(body, "$.data.data").toString());

        //totalCount
        Integer totalCount = JSON.parseObject(JsonPath.read(body, "$.data.totalCount").toString(), Integer.class);


        if (checkdb) {
            Integer count = BatchFunctionChecker.generalCount(batchFunctionDTO);

            Assert.assertEquals(totalCount, count);

            if (count > 0) {
                //说明有自定义函数
                List<BatchFunction> functions = BatchFunctionChecker.generalQuery(batchFunctionDTO, "gmt_modified", batchFunctionDTO.getSort(), null, pageSize);

                List<Long> userIds = new ArrayList<>();
                functions.forEach(f -> {
                    userIds.add(f.getCreateUserId());
                    userIds.add(f.getModifyUserId());
                });

                List<User> users = UserChecker.listByIds(userIds);
                Map<Long, User> idUserMap = new HashMap<>();
                users.forEach(u -> {
                    idUserMap.put(u.getId(), u);
                });
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    BatchFunction batchFunction = functions.get(i);
                    Map<String, Object> h = JSON.parseObject(jo.toJSONString(), new TypeReference<Map<String, Object>>() {
                    });
                    Assert.assertEquals(h.get("className").toString(), batchFunction.getClassName());
                    Assert.assertEquals(h.get("commandFormate"), batchFunction.getCommandFormate());
                    Assert.assertEquals(h.get("createUserId").toString(), batchFunction.getCreateUserId() + "");
                    Assert.assertEquals(h.get("id").toString(), batchFunction.getId() + "");
                    Assert.assertEquals(h.get("isDeleted").toString(), batchFunction.getIsDeleted() + "");
                    Assert.assertEquals(h.get("modifyUserId").toString(), batchFunction.getModifyUserId() + "");
                    Assert.assertEquals(h.get("name").toString(), batchFunction.getName());
                    Assert.assertEquals(h.get("nodePid").toString(), batchFunction.getNodePid() + "");
                    Assert.assertEquals(h.get("paramDesc"), batchFunction.getParamDesc());
                    Assert.assertEquals(h.get("projectId").toString(), batchFunction.getProjectId() + "");
                    Assert.assertEquals(h.get("purpose"), batchFunction.getPurpose());
                    Assert.assertEquals(h.get("tenantId").toString(), batchFunction.getTenantId() + "");
                    Assert.assertEquals(h.get("type").toString(), batchFunction.getType() + "");

                    //createUser
                    User createUser = JSON.parseObject(h.get("createUser").toString(), new TypeReference<User>() {
                    });
                    User resultCreateUser = idUserMap.get(batchFunction.getCreateUserId());

                    Assert.assertEquals(createUser.getDefaultProjectId(), resultCreateUser.getDefaultProjectId());
                    Assert.assertEquals(createUser.getDtuicUserId(), resultCreateUser.getDtuicUserId());
                    Assert.assertEquals(createUser.getEmail(), resultCreateUser.getEmail());
                    Assert.assertEquals(createUser.getId(), resultCreateUser.getId());
                    Assert.assertEquals(createUser.getIsDeleted(), resultCreateUser.getIsDeleted());
                    Assert.assertEquals(createUser.getPhoneNumber(), resultCreateUser.getPhoneNumber());
                    Assert.assertEquals(createUser.getStatus(), resultCreateUser.getStatus());
                    Assert.assertEquals(createUser.getUserName(), resultCreateUser.getUserName());

                    //modifyUser

                    User modifyUser = JSON.parseObject(h.get("modifyUser").toString(), new TypeReference<User>() {
                    });
                    User resultModifyUser = idUserMap.get(batchFunction.getModifyUserId());

                    Assert.assertEquals(modifyUser.getDefaultProjectId(), resultModifyUser.getDefaultProjectId());
                    Assert.assertEquals(modifyUser.getDtuicUserId(), resultModifyUser.getDtuicUserId());
                    Assert.assertEquals(modifyUser.getEmail(), resultModifyUser.getEmail());
                    Assert.assertEquals(modifyUser.getId(), resultModifyUser.getId());
                    Assert.assertEquals(modifyUser.getIsDeleted(), resultModifyUser.getIsDeleted());
                    Assert.assertEquals(modifyUser.getPhoneNumber(), resultModifyUser.getPhoneNumber());
                    Assert.assertEquals(modifyUser.getStatus(), resultModifyUser.getStatus());
                    Assert.assertEquals(modifyUser.getUserName(), resultModifyUser.getUserName());

                }
            }
        }
    }

    @DataProvider
    public Iterator<String[]> pageQuery() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/batch/function/pageQuery.csv", 6);
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
