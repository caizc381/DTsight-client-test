package com.dtstack.model.dto.ide.batch;

import java.sql.Timestamp;

public class BatchFunctionDTO {

    private Long tenantId;
    private Long projectId;
    private Long functionModifyUserId;
    private Timestamp startTIme;
    private Timestamp endTime;
    private Integer pageSize=10;
    private Integer pageIndex = 1;
    private String functionName;
    private String sort = "desc";

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getFunctionModifyUserId() {
        return functionModifyUserId;
    }

    public void setFunctionModifyUserId(Long functionModifyUserId) {
        this.functionModifyUserId = functionModifyUserId;
    }

    public Timestamp getStartTIme() {
        return startTIme;
    }

    public void setStartTIme(Timestamp startTIme) {
        this.startTIme = startTIme;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
