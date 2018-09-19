package com.dtstack.testcase.ide;

import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ProjectChecker;
import com.dtstack.model.domain.ide.Project;
import com.dtstack.util.db.SqlException;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;

public class GetAllProjectsTest extends IdeBase {
    public static List<Project> projects = new ArrayList<>();
    @Test(description = "获取所有项目，在筛选下拉框里使用", groups = {"getAllProjects","qa"})
    public void test_01_getAllProjects() throws SqlException {
        HttpResult result = httpclient.post(Flag.IDE, IDE_GetAllProjects, "");
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        String body = result.getBody();
        System.out.println("body=========  " + body);
        projects = JSON.parseArray(JsonPath.read(body, "$.data").toString(),
                Project.class);

        if (checkdb) {
            List<Project> resultProjects = ProjectChecker.getAllProjectByTenantId(Integer.valueOf(defTenantId));
            Assert.assertEquals(projects.size(), resultProjects.size());

            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);
                Project resultProject = resultProjects.get(i);
                Assert.assertEquals(project.getId(), resultProject.getId());
                Assert.assertEquals(project.getProjectName(), resultProject.getProjectName());
                Assert.assertEquals(project.getProduceProjectId(), resultProject.getProduceProjectId());
                Assert.assertEquals(project.getProjectAlias(), resultProject.getProjectAlias());
                Assert.assertEquals(project.getProjectDesc(), resultProject.getProjectDesc());
                Assert.assertEquals(project.getCreateUserId(), resultProject.getCreateUserId());
                Assert.assertEquals(project.getProjectType(), resultProject.getProjectType());
                Assert.assertEquals(project.getProjectIdentifier(), resultProject.getProjectIdentifier());
                Assert.assertEquals(project.getScheduleStatus(), resultProject.getScheduleStatus());
                Assert.assertEquals(project.getStatus(), resultProject.getStatus());
                Assert.assertEquals(project.getIsDeleted(), resultProject.getIsDeleted());
            }

        }

    }

}
