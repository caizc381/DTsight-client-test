package com.dtstack.model.dto.ide.batch;

import java.sql.Timestamp;

public class BatchResourceDTO {
    private Long tenantId;
    private Long projectId;
    private Long resourceModifyUserId;

    private Timestamp startTIme;
    private Timestamp endTime;

    private Integer pageSize=10;
    private Integer pageIndex =1;
    private String resourceName;

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

    public Long getResourceModifyUserId() {
        return resourceModifyUserId;
    }

    public void setResourceModifyUserId(Long resourceModifyUserId) {
        this.resourceModifyUserId = resourceModifyUserId;
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
