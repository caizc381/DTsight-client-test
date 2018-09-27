package com.dtstack.testcase.ide.project;

import com.alibaba.fastjson.JSON;
import com.dtstack.base.Flag;
import com.dtstack.base.HttpResult;
import com.dtstack.base.dbcheck.ide.common.ProjectChecker;
import com.dtstack.model.domain.ide.common.project.Project;
import com.dtstack.model.enums.ide.ProjectType;
import com.dtstack.model.vo.ide.ProjectVO;
import com.dtstack.testcase.ide.IdeBase;
import com.dtstack.util.db.SqlException;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 位置：开发套件->项目管理
 */
public class GetProjectByProjectIdTest extends IdeBase {
    @Test(description = "获得项目详情", groups = {"qa"})
    public void test_01_getProjectByProjectId() throws SqlException {

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", defProjectId);

        String json = JSON.toJSONString(params);

        HttpResult result = httpclient.post(Flag.IDE, Project_GetProjectByProjectId, json);

        String body = result.getBody();
        Assert.assertEquals(result.getCode(), HttpStatus.SC_OK);
        ProjectVO projectVO = JSON.parseObject(JsonPath.read(body, "$.data").toString(), ProjectVO.class);

        if (checkdb) {
            //1.先获取项目project
            Project project = ProjectChecker.getOne(defProjectId);

            //2.把project转化成projectVO
            ProjectVO resultProjectVO = ProjectChecker.projectMap2ProjectVO(project);

            //3.判断是测试项目，还是生产项目
            if (!ProjectType.GENERAL.getType().equals(project.getProjectType())) {
                //如果不是普通项目，则为生产项目，或者测试项目，获取produceProject
                Project produceProject = ProjectChecker.getOne(project.getProduceProjectId());
                if (ProjectType.TEST.getType().equals(project.getProjectType())) {
                    //生产项目
                    resultProjectVO.setProduceProject(produceProject.getProjectAlias());
                } else {
                    //测试项目
                    resultProjectVO.setTestProject(produceProject.getProjectAlias());
                    resultProjectVO.setTestProjectId(produceProject.getId());
                }
            }

            //验证
            Assert.assertEquals(projectVO.getCreateUserId(), resultProjectVO.getCreateUserId());
            Assert.assertEquals(projectVO.getId(), resultProjectVO.getId());
            Assert.assertEquals(projectVO.getIsDeleted(), resultProjectVO.getIsDeleted());
            Assert.assertEquals(projectVO.getMemberUsers(), resultProjectVO.getMemberUsers());
            Assert.assertEquals(projectVO.getProduceProject(), resultProjectVO.getProduceProject());
            Assert.assertEquals(projectVO.getProduceProjectId(), resultProjectVO.getProduceProjectId());
            Assert.assertEquals(projectVO.getProjectAlias(), resultProjectVO.getProjectAlias());
            Assert.assertEquals(projectVO.getProjectDesc(), resultProjectVO.getProjectDesc());
            Assert.assertEquals(projectVO.getProjectIdentifier(), resultProjectVO.getProjectIdentifier());
            Assert.assertEquals(projectVO.getProjectName(), resultProjectVO.getProjectName());
            Assert.assertEquals(projectVO.getProjectType(), resultProjectVO.getProjectType());
            Assert.assertEquals(projectVO.getScheduleStatus(), resultProjectVO.getScheduleStatus());
            Assert.assertEquals(projectVO.getStatus(), resultProjectVO.getStatus());
            Assert.assertEquals(projectVO.getTenantId(), resultProjectVO.getTenantId());
            Assert.assertEquals(projectVO.getTestProject(), resultProjectVO.getTestProject());
            Assert.assertEquals(projectVO.getTestProjectId(), resultProjectVO.getTestProjectId());
        }
    }
}
