package com.tijiantest.model.account;

import java.io.Serializable;

import com.tijiantest.model.resource.Address;

public class AccountInfo extends DomainObjectBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7165576781015737335L;

	private Integer id;
	private Integer accountId;//账号
	private Integer gender;//性别
	private String email;//邮箱
	private Address address;//地址信息
	private Integer marriageStatus;//0-未婚；1-已婚
	private Integer age;
	private Integer birthYear;
	private Integer height;//身高
	private Integer weight;//体重
	private String pinYin;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
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
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public String getPinYin() {
		return pinYin;
	}
	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}
	
	
	
}
