package com.dtstack.testcase.ide.batch.taskShade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.batch.BatchTaskShadeChecker;
import com.dtstack.base.dbcheck.ide.batch.BatchTaskVersionChecker;
import com.dtstack.base.dbcheck.ide.common.UserChecker;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.domain.ide.batch.BatchTaskShade;
import com.dtstack.model.dto.ide.batch.BatchTaskShadeDTO;
import com.dtstack.model.vo.ide.batch.BatchTaskVersionVO;
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

/**
 * 位置：开发套件-》运维中心-》离线任务发布-》创建发布包-》对象类型：任务
 */

public class PageQueryTest extends IdeBase {

    @Test(description = "分页查询已提交的任务", groups = {"qa"}, dataProvider = "pageQuery")
    public void test_01_pageQuery(String... args) throws SqlException, ParseException {
        String endTime = args[1];
        String pageIndex = args[2];
        String pageSize = args[3];
        String startTime = args[4];
        String taskModifyUserId = args[5];
        String taskName = args[6];

        BatchTaskShadeDTO batchTaskShadeDTO = new BatchTaskShadeDTO();

        Timestamp stampEndTime;
        Timestamp stampStartTime;
        if (endTime != null && !endTime.equals("")) {
            stampEndTime = DateUtils.dateToStamp(endTime + " 00:00:00");
            batchTaskShadeDTO.setEndTime(stampEndTime);
        }
        if (startTime != null && !startTime.equals("")) {
            stampStartTime = DateUtils.dateToStamp(startTime + " 00:00:00");
            batchTaskShadeDTO.setStartTime(stampStartTime);
        }

        batchTaskShadeDTO.setPageIndex(Integer.valueOf(pageIndex));
        batchTaskShadeDTO.setPageSize(Integer.valueOf(pageSize));
        batchTaskShadeDTO.setTenantId(defRdosTenantId);
        batchTaskShadeDTO.setProjectId(defProjectId);
        if (taskModifyUserId != null && !taskModifyUserId.equals("")) {
            batchTaskShadeDTO.setTaskModifyUserId(Long.valueOf(taskModifyUserId));
        }
        if (taskName != null && !taskName.equals("")) {
            batchTaskShadeDTO.setTaskName(taskName);
        }

        String json = JSON.toJSONString(batchTaskShadeDTO);

        HttpResult result = httpclient.post(Flag.IDE, BatchTaskShade_PageQuery, json);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        JSONArray array = JSONArray.parseArray(JsonPath.read(body, "$.data.data").toString());

        //totalCount
        Integer totalCount = JSON.parseObject(JsonPath.read(body, "$.data.totalCount").toString(), Integer.class);

        if (checkdb) {

            Integer count = BatchTaskShadeChecker.simpleCount(batchTaskShadeDTO);
            Assert.assertEquals(totalCount, count);

            if (count > 0) {
                //说明有任务数据
                PageQuery<BatchTaskShadeDTO> query = new PageQuery<>(batchTaskShadeDTO.getPageIndex(), batchTaskShadeDTO.getPageSize(), "gmt_modified", batchTaskShadeDTO.getSort());
                query.setModel(batchTaskShadeDTO);

                //详细信息
                List<BatchTaskShade> taskShades = BatchTaskShadeChecker.simpleQuery(batchTaskShadeDTO, "gmt_modified", null, null, pageSize);
                Assert.assertEquals(array.size(), taskShades.size());

                List<Long> userIds = new ArrayList<>();
                List<Long> taskIds = new ArrayList<>();
                taskShades.forEach(t -> {
                    userIds.add(t.getModifyUserId());
                    userIds.add(t.getCreateUserId());
                });

                taskShades.forEach(t -> taskIds.add(t.getId()));
                Map<Long, User> userMap = getUserMap(userIds);

                List<BatchTaskVersionVO> versions = BatchTaskVersionChecker.getLastestTaskVersionByTaskIds(taskIds);
                Map<Long, String> desc = new HashMap<>();
                versions.forEach(v -> desc.put(v.getTaskId(), v.getPublishDesc()));

                for (int i = 0; i < array.size(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    BatchTaskShade batchTaskShade = taskShades.get(i);
                    Map<String, Object> h = JSON.parseObject(jo.toJSONString(), new TypeReference<Map<String, Object>>() {
                    });
                    Assert.assertEquals(h.get("id").toString(), batchTaskShade.getId() + "");
                    Assert.assertEquals(h.get("chargeUser").toString(), userMap.get(batchTaskShade.getOwnerUserId()).getUserName());
                    Assert.assertEquals(h.get("createUser").toString(), userMap.get(batchTaskShade.getCreateUserId()).getUserName(), "-----" + batchTaskShade.getId() + "----");
                    Assert.assertEquals(h.get("isDeleted").toString(), batchTaskShade.getIsDeleted() + "");
                    Assert.assertEquals(h.get("taskDesc").toString(), desc.get(batchTaskShade.getId()));
                    Assert.assertEquals(h.get("taskName").toString(), batchTaskShade.getName());
                    Assert.assertEquals(h.get("taskType").toString(), batchTaskShade.getTaskType() + "");
                }
            }
        }
    }

    public Map<Long, User> getUserMap(List<Long> userIds) throws SqlException {
        Map<Long, User> idUserMap = new HashMap<>();
        List<User> users = UserChecker.listByIds(userIds);

        for (User user : users
        ) {
            idUserMap.put(user.getId(), user);
        }
        return idUserMap;
    }


    @DataProvider
    public Iterator<String[]> pageQuery() {
        try {
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/batch/taskShade/pageQuery.csv", 4);
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
