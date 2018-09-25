package com.dtstack.testcase.ide.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.model.domain.ide.Project;
import com.dtstack.model.vo.ide.ProjectVO;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.ListUtil;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class GetProjectByProjectIdTest extends IdeBase {
    @Test(dependsOnGroups = {"getAllProjects"},description = "获得项目详情",groups = {"qa"})
    public void test_01_getProjectByProjectId(){
        List<Project> projects = GetAllProjectsTest.projects;

        //随机取一个projectid
        int index = ListUtil.getRandomIndexFromList(projects);
        Project project = projects.get(index);
        System.out.println(project.getProjectName()+"-----"+project.getId());

        String json = "{projectId:\""+project.getId()+"\"}";

        HttpResult result = httpclient.post(Flag.IDE,IDE_GetProjectByProjectId,json);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        ProjectVO projectVO =JSON.parseObject(body, ProjectVO.class);

        if (checkdb) {

        }

    }
}
