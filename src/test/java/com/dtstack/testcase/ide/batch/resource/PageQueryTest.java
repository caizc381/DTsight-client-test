package com.dtstack.testcase.ide.batch.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.batch.BatchResourceChecker;
import com.dtstack.base.dbcheck.ide.common.UserChecker;
import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.model.domain.ide.User;
import com.dtstack.model.domain.ide.batch.BatchResource;
import com.dtstack.model.dto.ide.batch.BatchResourceDTO;
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
 * 位置：运维中心-》离线任务发布-》创建发布包-》对象类型：资源
 */
public class PageQueryTest extends IdeBase {

    @Test(description = "资源分页查询", groups = {"qa"}, dataProvider = "pageQuery")
    public void test_01_pageQuery(String... args) throws ParseException, SqlException {
        String endTime = args[1];
        String pageIndex = args[2];
        String pageSize = args[3];
        String startTime = args[4];
        String resourceModifyUserId = args[5];
        String resourceName = args[6];
        BatchResourceDTO batchResourceDTO = new BatchResourceDTO();

        Timestamp stampEndTime;
        Timestamp stampStartTime;
        if (endTime != null && !endTime.equals("")) {
            stampEndTime = DateUtils.dateToStamp(endTime + " 00:00:00");
            batchResourceDTO.setEndTime(stampEndTime);
        }
        if (startTime != null && !startTime.equals("")) {
            stampStartTime = DateUtils.dateToStamp(startTime + " 00:00:00");
            batchResourceDTO.setStartTIme(stampStartTime);
        }


        batchResourceDTO.setPageIndex(Integer.valueOf(pageIndex));
        batchResourceDTO.setPageSize(Integer.valueOf(pageSize));


        if (resourceModifyUserId != null && !resourceModifyUserId.equals("")) {
            batchResourceDTO.setResourceModifyUserId(Long.valueOf(resourceModifyUserId));
        }
        if (resourceName != null && !resourceName.equals("")) {
            batchResourceDTO.setResourceName(resourceName);
        }

        batchResourceDTO.setTenantId(defRdosTenantId);
        batchResourceDTO.setProjectId(defProjectId);

        String json = JSON.toJSONString(batchResourceDTO);
        HttpResult result = httpclient.post(Flag.IDE, BatchResource_PageQuery, json);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        JSONArray array = JSONArray.parseArray(JsonPath.read(body, "$.data.data").toString());

        //totalCount
        Integer totalCount = JSON.parseObject(JsonPath.read(body, "$.data.totalCount").toString(), Integer.class);


        if (checkdb) {
            Integer count = BatchResourceChecker.generalCount(batchResourceDTO);
            Assert.assertEquals(totalCount, count);

            if (count > 0) {
                //说明有资源数据
                PageQuery<BatchResourceDTO> query = new PageQuery<>(batchResourceDTO.getPageIndex(), batchResourceDTO.getPageSize(), "gmt_modified", batchResourceDTO.getSort());
                query.setModel(batchResourceDTO);

                //详细信息
                List<BatchResource> batchResources = BatchResourceChecker.generalQuery(batchResourceDTO, "gmt_modified", null, null, pageSize);
                Assert.assertEquals(array.size(), batchResources.size());

                List<Long> userIds = new ArrayList<>();
                batchResources.forEach(t -> {
                    userIds.add(t.getModifyUserId());
                    userIds.add(t.getCreateUserId());
                });

                List<User> users = UserChecker.listByIds(userIds);
                Map<Long, User> idUserMap = new HashMap<>();
                users.forEach(u -> {
                    idUserMap.put(u.getId(), u);
                });
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    BatchResource batchResource = batchResources.get(i);
                    Map<String, Object> h = JSON.parseObject(jo.toJSONString(), new TypeReference<Map<String, Object>>() {
                    });
                    Assert.assertEquals(h.get("createUserId").toString(), batchResource.getCreateUserId() + "");
                    Assert.assertEquals(h.get("id").toString(), batchResource.getId() + "");
                    Assert.assertEquals(h.get("isDeleted").toString(), batchResource.getIsDeleted() + "");
                    Assert.assertEquals(h.get("modifyUserId").toString(), batchResource.getModifyUserId() + "");
                    Assert.assertEquals(h.get("nodePid").toString(), batchResource.getNodePid() + "");
                    Assert.assertEquals(h.get("originFileName").toString(), batchResource.getOriginFileName());
                    Assert.assertEquals(h.get("projectId").toString(), batchResource.getProjectId() + "");
                    Assert.assertEquals(h.get("resourceDesc").toString(), batchResource.getResourceDesc());
                    Assert.assertEquals(h.get("resourceName").toString(), batchResource.getResourceName());
                    Assert.assertEquals(h.get("resourceType").toString(), batchResource.getResourceType() + "");
                    Assert.assertEquals(h.get("tenantId").toString(), batchResource.getTenantId() + "");
                    Assert.assertEquals(h.get("url").toString(), batchResource.getUrl());

                    //createUser
                    User createUser = JSON.parseObject(h.get("createUser").toString(), new TypeReference<User>() {
                    });
                    User resultCreateUser = idUserMap.get(batchResource.getCreateUserId());
                    Assert.assertEquals(createUser.getId(), resultCreateUser.getId());
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
                    User resultModifyUser = idUserMap.get(batchResource.getModifyUserId());
                    Assert.assertEquals(modifyUser.getId(), resultModifyUser.getId());
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
            return CvsFileUtil.fromCvsFileToIterator("./csv/ide/batch/resource/pageQuery.csv", 6);
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
