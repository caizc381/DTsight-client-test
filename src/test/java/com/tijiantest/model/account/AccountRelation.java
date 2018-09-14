package com.tijiantest.model.account;

import java.io.Serializable;
import java.util.Calendar;

import com.tijiantest.base.DomainObjectBase;
import com.tijiantest.model.resource.Address;
import com.tijiantest.util.PinYinUtil;

public class AccountRelation extends DomainObjectBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2593046628211207313L;

	private Integer managerId;

	private Integer customerId;

	private Integer type;

	private String name;

	private Integer gender;

	private String mobile;
	
	//原始手机号
	private String initialMobile;
	
	//是否是标准手机号
	private Boolean isStandardMobile;

	private String idCard;
	
	private Integer idType;
	
	private Integer status;

	private String email;

	private Address address;

	private Integer age;

	private Integer birthYear;

	private Integer marriageStatus;
	
	private Integer companyId;
	
	// 体检单位
	private Integer newCompanyId;
	
	// 机构ID
	private Integer organizationId;
	
	// 机构类型
	private Integer organizationType;
	
	private String healthNum;
	
	private String healthLevel;
	
	private String socialSecurity;
	
	private String pinYin;
	
	private Integer relationship;

	public Integer getRelationship() {
		return relationship;
	}

	public void setRelationship(Integer relationship) {
		this.relationship = relationship;
	}

	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public Integer getAge() {
		return (this.getBirthYear() == null) ? null : Calendar.getInstance().get(
				Calendar.YEAR)
				- this.getBirthYear();
	}

	public void setAge(Integer age) {
		this.age = age;
		this.birthYear = (this.age == null) ? null : (Calendar.getInstance()
				.get(Calendar.YEAR) - this.age);
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return PinYinUtil.fullWidth2halfWidth(name);
	}

	public void setName(String name) {
		this.name = PinYinUtil.fullWidth2halfWidth(name);
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
	}

	public String getHealthNum() {
		return healthNum;
	}

	public void setHealthNum(String healthNum) {
		this.healthNum = healthNum;
	}

	public String getHealthLevel() {
		return healthLevel;
	}

	public void setHealthLevel(String healthLevel) {
		this.healthLevel = healthLevel;
	}

	public String getSocialSecurity() {
		return socialSecurity;
	}

	public void setSocialSecurity(String socialSecurity) {
		this.socialSecurity = socialSecurity;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getIdType() {
		return idType;
	}

	public void setIdType(Integer idType) {
		this.idType = idType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountRelation [managerId=");
		builder.append(managerId);
		builder.append(", customerId=");
		builder.append(customerId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", gender=");
		builder.append(gender);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", idCard=");
		builder.append(idCard);
		builder.append(", email=");
		builder.append(email);
		builder.append(", address=");
		builder.append(address);
		builder.append(", age=");
		builder.append(age);
		builder.append(", birthYear=");
		builder.append(birthYear);
		builder.append(", marriageStatus=");
		builder.append(marriageStatus);
		builder.append(", idType=");
		builder.append(idType);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}

}
