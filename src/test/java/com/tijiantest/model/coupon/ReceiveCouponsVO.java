package com.tijiantest.model.coupon;

import com.tijiantest.util.pagination.Page;

import java.io.Serializable;

public class ReceiveCouponsVO implements Serializable {

    //机构ID
    private int organizationId;
    //搜索关键字（模板批次号/发行人/模板名称）
    private String searchKey;
    //模板批次号
    private String batchNum;
    //状态（未使用|已使用|可用）
    private String status;
    //分页
    private Page page;

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
