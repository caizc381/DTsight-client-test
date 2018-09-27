package com.dtstack.model.vo.ide;

import com.dtstack.model.domain.ide.common.project.Project;
import com.dtstack.model.domain.ide.User;

import java.util.List;


/**
 * 项目列表展示实体
 */
public class ProjectVO extends Project {
        private User createUser;
        private List<User> adminUser;
        private List<User> memberUsers;
        private  String produceProject;
        private String testProject;
        private Long testProjectId;

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public List<User> getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(List<User> adminUser) {
        this.adminUser = adminUser;
    }

    public List<User> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<User> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public String getProduceProject() {
        return produceProject;
    }

    public void setProduceProject(String produceProject) {
        this.produceProject = produceProject;
    }

    public String getTestProject() {
        return testProject;
    }

    public void setTestProject(String testProject) {
        this.testProject = testProject;
    }

    public Long getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(Long testProjectId) {
        this.testProjectId = testProjectId;
    }
}
