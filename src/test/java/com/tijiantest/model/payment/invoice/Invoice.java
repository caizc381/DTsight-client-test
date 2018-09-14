package com.tijiantest.model.payment.invoice;

import java.io.Serializable;

import com.tijiantest.model.resource.Address;

public class Invoice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6890782215389488230L;
	
	private Integer id;
	
	/**
	 * 抬头
	 */
	private String title;
	/**
	 * 收件人
	 */
	private String name;
	/**
	 * 获取方式 2：自取 1：寄送
	 */
	private Integer deliveryType;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 地址
	 */
	private Address address;
	/**
	 * 寄送费
	 */
	private int amount;
	
	/**
	 * 发票是否有效,未支付完成,发票为无效
	 */
	private boolean valid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Integer getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(Integer deliveryType) {
		this.deliveryType = deliveryType;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
}
