package com.tijiantest.model.account;

import java.util.Calendar;

public class AccountManageDto extends Account {

	private Integer age;
	private Integer balance;
	private Integer gender;
	private Integer birthYear;
	private Integer marriageStatus;// 0-未婚；1-已婚
	private boolean isUser;// 是否是登录用户
	private Integer mainType;// 主要类型：2-account数据；1-relation数据
	
	

	public AccountManageDto() {
		super();
	}	

	public Integer getAge() {
		return (this.getBirthYear() == null) ? null : Calendar.getInstance().get(Calendar.YEAR) - this.getBirthYear();
	}

	public void setAge(Integer age) {
		this.age = age;
		this.birthYear = (this.age == null) ? null : (Calendar.getInstance().get(Calendar.YEAR) - this.age);
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
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

	public boolean isUser() {
		return isUser;
	}

	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}

	public Integer getMainType() {
		return mainType;
	}

	public void setMainType(Integer mainType) {
		this.mainType = mainType;
	}

}
