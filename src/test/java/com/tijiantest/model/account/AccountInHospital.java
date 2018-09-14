package com.tijiantest.model.account;

import java.util.Calendar;

public class AccountInHospital {

	private Integer accountId;

	private String name;

	private Integer gender;

	private Integer age;

	private Integer birthYear;

	private String idcard;

	private String mobile;

	private String initialMobile;
	
	private Boolean isStandardMobile;
	
	private Integer marriageStatus;

	private Integer managerId;

	private Integer companyId;


	private String manager;

	private String role;

	private String employeeId;

	private Integer type;
	
	private int newCompanyId;
	
	private int organizationId;
	
	private int organizationType;

	private String pinYin;

	private Integer status;

	private String address;

	private String loginName;
	private String addAccountType;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getAddAccountType() {
		return addAccountType;
	}

	public void setAddAccountType(String addAccountType) {
		this.addAccountType = addAccountType;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return (this.getBirthYear() == null) ? null : Calendar.getInstance().get(Calendar.YEAR) - this.getBirthYear();
	}

	public void setAge(Integer age) {
		this.age = age;
		this.birthYear = (this.age == null) ? null : (Calendar.getInstance().get(Calendar.YEAR) - this.age);
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	public String getInitialMobile() {
		return initialMobile;
	}

	public void setInitialMobile(String initialMobile) {
		this.initialMobile = initialMobile;
	}

	public Boolean getIsStandardMobile() {
		return isStandardMobile;
	}

	public void setIsStandardMobile(Boolean isStandardMobile) {
		this.isStandardMobile = isStandardMobile;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AccountInHospital) {
			if (this.managerId != null) {
				if (this.accountId.equals(((AccountInHospital) other).getAccountId())
						&& this.managerId.equals(((AccountInHospital) other).getManagerId())
						&& this.companyId.equals(((AccountInHospital) other).getCompanyId())) {
					return true;

				} else {
					return false;
				}
			} else {
				if (this.accountId.equals(((AccountInHospital) other).getAccountId())) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (this.managerId != null) {
			return 1;
		} else {
			return 0;
		}
	}
	}
