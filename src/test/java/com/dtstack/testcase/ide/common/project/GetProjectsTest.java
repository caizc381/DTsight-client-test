package com.dtstack.testcase.ide.common.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.common.ProjectChecker;
import com.dtstack.base.dbcheck.ide.common.RoleUserChecker;
import com.dtstack.model.domain.ide.common.Project;
import com.dtstack.model.domain.ide.RoleUser;
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

public class GetProjectsTest extends IdeBase {
    @Test(description = "1.首页显示内容-不做权限设置;2.控制台顶端-项目下拉列表", groups = {"qa"})
    public void test_01_getProjects() throws SqlException {
        HttpResult result = httpclient.post(Flag.IDE, Project_GetProjects, "");
        String body = result.getBody();
        System.out.println(body);
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        List<Project> projectList = JSON.parseArray(JsonPath.read(body, "$.data").toString(), Project.class);

        if (checkdb) {
            List<RoleUser> roleUsers = new ArrayList<>();
            if (isAdmin.equals("1")) {
                roleUsers = RoleUserChecker.getRoleUserIsAdmin(defRdosUserId, defRdosTenantId);
            } else {
                roleUsers = RoleUserChecker.getRoleUserByUserId(defRdosUserId, defRdosTenantId);
            }

            List<Long> projectIds = new ArrayList<>();
            roleUsers.forEach(item -> {
                projectIds.add(item.getProjectId());
            });
            List<Project> projects = ProjectChecker.listByIds(projectIds);
            Assert.assertEquals(projectList.size(), projects.size());
            for (int i = 0; i < projectList.size(); i++) {
                Project project = projectList.get(i);
                Project resultProject = projects.get(i);
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
