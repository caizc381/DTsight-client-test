package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.List;

public class HospitalReceiptDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6161109205075465508L;

	private List<String> snList;

    private Integer organizationId;

    private Integer companyId;

    private Long totalpayableAmount;

    private Long totalRevenueAmount;

    private String remark;

    private int type;

    public List<String> getSnList() {
        return snList;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }

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


    public Long getTotalpayableAmount() {
        return totalpayableAmount;
    }

    public void setTotalpayableAmount(Long totalpayableAmount) {
        this.totalpayableAmount = totalpayableAmount;
    }

    public Long getTotalRevenueAmount() {
        return totalRevenueAmount;
    }

    public void setTotalRevenueAmount(Long totalRevenueAmount) {
        this.totalRevenueAmount = totalRevenueAmount;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
