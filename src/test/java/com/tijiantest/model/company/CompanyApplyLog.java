package com.tijiantest.model.company;


import java.io.Serializable;


public class CompanyApplyLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1341285026532707910L;

	private Integer companyId;

	private Integer newCompanyId;
	
	private String companyName;

	private Integer hospitalId;

	private String accountCompanyName;

	private String accountCompanyOwner;

	private String settingDetail;

	private int status = CompanyApplyStatusEnum.applying.getCode();

	private Double discount;

	public String getSettingDetail() {
		return settingDetail;
	}

	public void setSettingDetail(String settingDetail) {
		this.settingDetail = settingDetail;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getAccountCompanyName() {
		return accountCompanyName;
	}

	public void setAccountCompanyName(String accountCompanyName) {
		this.accountCompanyName = accountCompanyName;
	}

	public String getAccountCompanyOwner() {
		return accountCompanyOwner;
	}

	public void setAccountCompanyOwner(String accountCompanyOwner) {
		this.accountCompanyOwner = accountCompanyOwner;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
