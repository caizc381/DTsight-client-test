package com.dtstack.testcase.ide.common.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.common.ProjectChecker;
import com.dtstack.model.domain.ide.common.Project;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置：点击开发套件的请求
 */

public class GetAllProjectsTest extends IdeBase {

    public static List<Project> projects = new ArrayList<>();

    @Test(description = "获取所有项目，在筛选下拉框里使用", groups = {"qa"})
    public void test_01_getAllProject() throws SqlException {
        HttpResult result = httpclient.post(Flag.IDE, Project_GetAllProjects, "");
        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        List<Project> projectList = JSON.parseArray(JsonPath.read(body, "$.data").toString(), Project.class);

        if (checkdb) {
            List<Project> resultProjectList = ProjectChecker.listByIdsAndTenantId(new ArrayList<>(), defRdosTenantId);
            Assert.assertEquals(projectList.size(), resultProjectList.size());
            for (int i = 0; i < projectList.size(); i++) {
                Project project = projectList.get(i);
                Project resultProject = resultProjectList.get(i);

                Assert.assertEquals(project.getId(), resultProject.getId());
                Assert.assertEquals(project.getTenantId(), resultProject.getTenantId());
                Assert.assertEquals(project.getCreateUserId(), resultProject.getCreateUserId());
                Assert.assertEquals(project.getIsDeleted(), resultProject.getIsDeleted());
                Assert.assertEquals(project.getProduceProjectId(), resultProject.getProduceProjectId());
                Assert.assertEquals(project.getProjectAlias(), resultProject.getProjectAlias());
                Assert.assertEquals(project.getProjectName(), resultProject.getProjectName());
                Assert.assertEquals(project.getProjectIdentifier(), resultProject.getProjectIdentifier());
                Assert.assertEquals(project.getProjectType(), resultProject.getProjectType());
                Assert.assertEquals(project.getScheduleStatus(), resultProject.getScheduleStatus());
                Assert.assertEquals(project.getStatus(), resultProject.getStatus());
            }
        }
    }
}
