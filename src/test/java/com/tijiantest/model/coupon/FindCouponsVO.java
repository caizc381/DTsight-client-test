package com.tijiantest.model.coupon;

import com.tijiantest.model.hospital.HospitalQueryDto;
import com.tijiantest.util.pagination.Page;

import java.io.Serializable;

public class FindCouponsVO implements Serializable {

    //机构ID
    private String organizationId;
    //搜索关键字（模板批次号/发行人/模板名称）
    private String searchKey;
    //分页
    private Page page;
    //地址对象
    private HospitalQueryDto hospitalQueryDto;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public HospitalQueryDto getHospitalQueryDto() {
        return hospitalQueryDto;
    }

    public void setHospitalQueryDto(HospitalQueryDto hospitalQueryDto) {
        this.hospitalQueryDto = hospitalQueryDto;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
