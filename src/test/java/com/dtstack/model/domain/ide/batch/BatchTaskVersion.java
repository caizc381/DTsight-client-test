package com.dtstack.model.domain.ide.batch;

import com.dtstack.model.domain.ide.TenantProjectEntity;

public class BatchTaskVersion extends TenantProjectEntity {
    private Long taskId;

    private String sqlText;//sql文本

    private String publishDesc;//

    private Long createUserId;//新建task的用户

    private Integer version;//task版本

    private String taskParams;//环境参数
    private String scheduleConf;//调度信息

    private Integer scheduleStatus;//
    private String dependencyTaskIds;//依赖的任务id

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getDependencyTaskIds() {
        return dependencyTaskIds;
    }

    public void setDependencyTaskIds(String dependencyTaskIds) {
        this.dependencyTaskIds = dependencyTaskIds;
    }
}
