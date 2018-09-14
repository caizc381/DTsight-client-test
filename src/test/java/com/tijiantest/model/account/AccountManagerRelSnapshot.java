package com.tijiantest.model.account;

import java.io.Serializable;

public class AccountManagerRelSnapshot implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1663962604378258243L;
	/**
	 * 客户经理
	 */
	private Integer managerId;
	/**
	 * 账号
	 */
	private Integer customerId;
	/**
	 * 1:CRM客户经理与预约人 2:主站点预约人与体检人
	 */
	private Integer type;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private Integer gender;
	/**
	 * 手机
	 */
	private String mobile;
	/**
	 * 身份证
	 */
	private String idCard;
	/**
	 * 证件类型
	 */
	private Integer idType;
	/**
	 * email
	 */
	private String email;
	/**
	 * 年龄
	 */
	private Integer age;
	/**
	 * 出生年
	 */
	private Integer birthYear;
	/**
	 * 婚姻状况
	 */
	private Integer marriageStatus;
	/**
	 * 保健号
	 */
	private String healthNum;
	/**
	 * 保健级别
	 */
	private String healthLevel;
	/**
	 * 社保号
	 */
	private String socialSecurity;

	/**
	 * 所在组
	 */
	private String group;

	/**
	 * 部门
	 */
	private String department;

	/**
	 * 来源表
	 */
	private String sheetName;

	/**
	 * 职级
	 */
	private String position;

	/**
	 * 退休, 0:未退休 1:退休
	 */
	private Integer isRetire;
	/**
	 * 导入序号
	 */
	private Integer sequence;
	/**
	 * 员工号
	 */
	private String employeeId;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public Integer getIdType() {
		return idType;
	}
	public void setIdType(Integer idType) {
		this.idType = idType;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getIsRetire() {
		return isRetire;
	}
	public void setIsRetire(Integer isRetire) {
		this.isRetire = isRetire;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	
}
