package com.dtstack.testcase.ide;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.ide.Project;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

public class GetAllProjectsTest extends IdeBase {
    @Test(description = "获取所有项目，在筛选下拉框里使用",groups = {"qa"})
    public void test_01_getAllProjects(){
        HttpResult result = httpclient.post(Flag.IDE, IDE_GetAllProjects,"");
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();

        List<Project> projects = JSON.parseArray(JsonPath.read(body, "$.data").toString(),
                Project.class);

        if (checkdb){

        }

    }

}
