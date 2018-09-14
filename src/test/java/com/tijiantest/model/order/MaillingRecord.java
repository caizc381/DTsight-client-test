package com.tijiantest.model.order;

import java.io.Serializable;

import com.tijiantest.model.hospital.Address;

public class MaillingRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7586699242739249784L;

	private Integer id;
	private String receiver;
	private String mobile;
	
	private Integer deliveryType;//寄送方式：1-寄送；2-自取
	private Address address;
	private String addressDetail;
	private Integer amount;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(Integer deliveryType) {
		this.deliveryType = deliveryType;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	
}
