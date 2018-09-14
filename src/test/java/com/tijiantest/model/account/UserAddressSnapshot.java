package com.tijiantest.model.account;

import java.io.Serializable;

public class UserAddressSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8506849682584958340L;
	

	private Integer id;
	
	/**
	 * 客户id
	 */
	private Integer accountId;
	
	/**
	 * 收件人
	 */
	private String addressee;
	
	/**
	 * 地址id
	 */
	private Integer addressId;
	
	/**
	 * 详细地址
	 */
	private String detailedAddress;
	
	/**
	 * 手机
	 */
	private String mobile;
	
	/**
	 * 固定电话
	 */
	private String phoneNumber;
	
	/**
	 * 邮件
	 */
	private String email;
	
	/**
	 * 地址别名
	 */
	private String addressAlias;
	
	/**
	 * 备注
	 */
	private String remark;
	
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
	public String getAddressee() {
		return addressee;
	}
	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public String getDetailedAddress() {
		return detailedAddress;
	}
	public void setDetailedAddress(String detailedAddress) {
		this.detailedAddress = detailedAddress;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddressAlias() {
		return addressAlias;
	}
	public void setAddressAlias(String addressAlias) {
		this.addressAlias = addressAlias;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
