package com.tijiantest.model.card;

import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.company.HospitalCompany;

public class CardManageDto extends Card{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4708477916079538625L;
		
	/**
	 * 医院体检单位
	 */
	private HospitalCompany hospitalCompany;
	
	/**
	 * 渠道商体检单位
	 */
	private ChannelCompany channelCompay;
	
	/**
	 * 机构名称
	 */
	private String organizationName;
	/**
	 * 单位名称
	 */
	private String companyName;
	
	/**
	 * 单位类型：0-普通单位，1-P单位，2-M单位
	 */
	private Integer companyType;
	
	/**
	 * 客户经理名称
	 */
	private String managerName;
	
	/**
	 * 客户经理角色
	 */
	private String managerRole;

	public HospitalCompany getHospitalCompany() {
		return hospitalCompany;
	}

	public void setHospitalCompany(HospitalCompany hospitalCompany) {
		this.hospitalCompany = hospitalCompany;
	}

	public ChannelCompany getChannelCompay() {
		return channelCompay;
	}

	public void setChannelCompay(ChannelCompany channelCompay) {
		this.channelCompay = channelCompay;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerRole() {
		return managerRole;
	}

	public void setManagerRole(String managerRole) {
		this.managerRole = managerRole;
	}
	
	
	
}
