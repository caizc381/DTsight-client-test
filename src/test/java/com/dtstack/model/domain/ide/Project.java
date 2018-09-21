package com.dtstack.model.domain.ide;

public class Project extends BaseEntity{

    private String projectIdentifier;//项目标识

    private  String projectName;//项目名称

    private String projectAlias;//项目别名

    private String projectDesc;//项目描述

    private Integer status;//项目状态：0-初始化；1-正常；2-禁用；3-失败

    private Long createUserId;//新建项目的用户id

    private Long tenantId;//租户id

    private Integer projectType;//项目类型：0-普通项目；1-测试项目；2-生产项目

    private Long produceProjectId;//绑定的生产项目id

    private Integer scheduleStatus;//调度状态：0-开启；1-关闭

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAlias() {
        return projectAlias;
    }

    public void setProjectAlias(String projectAlias) {
        this.projectAlias = projectAlias;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Long getProduceProjectId() {
        return produceProjectId;
    }

    public void setProduceProjectId(Long produceProjectId) {
        this.produceProjectId = produceProjectId;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectIdentifier='" + projectIdentifier + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectAlias='" + projectAlias + '\'' +
                ", projectDesc='" + projectDesc + '\'' +
                ", status=" + status +
                ", createUserId=" + createUserId +
                ", tenantId=" + tenantId +
                ", projectType=" + projectType +
                ", produceProjectId=" + produceProjectId +
                ", scheduleStatus=" + scheduleStatus +
                '}';
    }
}
