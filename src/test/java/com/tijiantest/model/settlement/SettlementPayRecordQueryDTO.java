package com.tijiantest.model.settlement;



import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.tijiantest.util.pagination.Page;

public class SettlementPayRecordQueryDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7635881192900427223L;

	/**
     * 机构ID
     */
    private Integer organizationId;

    private List<Integer> organizationIds;

    /**
     * 单位ID
     */
    private Integer companyId;

    /**
     * 付款时间
     */
    private Date paymentTime;

    private String startTime;

    private String endTime;

    private Page page;

    /**
     * 类型：0 单位收款，1 平台付款，-1 全部
     */
    private Integer type;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }



    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Integer> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Integer> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
