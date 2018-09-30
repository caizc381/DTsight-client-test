package com.dtstack.testcase.ide.common.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.domain.ide.common.Project;
import com.dtstack.testcase.ide.IdeBase;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 位置：开发套件-》项目管理
 */

public class GetBindingProjectsTest extends IdeBase {
    @Test(description = "获取待绑定的项目列表", groups = {"qa"})
    public void test_01_getBindingProjects() {
        Map<String, Object> params = new HashMap<>();
        params.put("projectAlias", defProjectName);

        String json = JSON.toJSONString(params);

        HttpResult result = httpclient.post(Flag.IDE, Project_GetBindingProjects, json);
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);

        List<Project> projectList = JSON.parseArray(JsonPath.read(body, "$.data").toString(), Project.class);

        if (checkdb) {


        }
    }
}
