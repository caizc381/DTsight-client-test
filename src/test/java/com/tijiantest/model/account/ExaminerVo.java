package com.tijiantest.model.account;

public class ExaminerVo extends Account{
	/**
	 * 性别
	 */
	private Integer gender;
	
	/**
	 * 邮箱
	 */
	private String email;

	private String addType;

	private int age;

	private int birthYear;

	private int isSelf;

	private int customerId;

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getAddType() {
		return addType;
	}

	public void setAddType(String addType) {
		this.addType = addType;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public int getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(int isSelf) {
		this.isSelf = isSelf;
	}

	/**
	 * 婚姻状况
	 */
	private Integer marriageStatus;

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
	}
	
	
}
