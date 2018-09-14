package com.tijiantest.model.order;

import com.tijiantest.util.pagination.Page;

import java.io.Serializable;
import java.util.List;

public class ChannelUnsettlementOrderQueryDTO implements Serializable {
    private static final long serialVersionUID = 8626375874983477802L;
    private String examStartTime;
    private String examEndTime;
    private String placeOrderStartTime;
    private String placeOrderEndTime;
    private Integer organizationId;
    private Integer channelCompanyId;
    private List<Integer> status;
    private Integer isExport;
    private Integer fromSiteOrgType;
    private Page page;

    public ChannelUnsettlementOrderQueryDTO() {
    }

    public Page getPage() {
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getExamStartTime() {
        return this.examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamEndTime() {
        return this.examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getPlaceOrderStartTime() {
        return this.placeOrderStartTime;
    }

    public void setPlaceOrderStartTime(String placeOrderStartTime) {
        this.placeOrderStartTime = placeOrderStartTime;
    }

    public String getPlaceOrderEndTime() {
        return this.placeOrderEndTime;
    }

    public void setPlaceOrderEndTime(String placeOrderEndTime) {
        this.placeOrderEndTime = placeOrderEndTime;
    }

    public Integer getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getChannelCompanyId() {
        return this.channelCompanyId;
    }

    public void setChannelCompanyId(Integer channelCompanyId) {
        this.channelCompanyId = channelCompanyId;
    }

    public List<Integer> getStatus() {
        return this.status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public Integer getIsExport() {
        return this.isExport;
    }

    public void setIsExport(Integer isExport) {
        this.isExport = isExport;
    }

    public Integer getFromSiteOrgType() {
        return this.fromSiteOrgType;
    }

    public void setFromSiteOrgType(Integer fromSiteOrgType) {
        this.fromSiteOrgType = fromSiteOrgType;
    }
}
