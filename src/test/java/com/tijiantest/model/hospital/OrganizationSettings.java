package com.tijiantest.model.hospital;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class OrganizationSettings implements Serializable {

	/**
	 * 
	 */
	protected static final long serialVersionUID = -6946440316227720419L;

	public boolean isHospitalSetting() {
		return getClass() == HospitalSettings.class;
	}

	public boolean isChannelSetting() {
		return getClass() == ChannelSettings.class;
	}
	
	protected int hospitalId;

	protected Integer deliveryPrice;

	protected String serviceTel;

	protected String technicalTel;

	/**
	 * 是否允许调整套餐价格
	 */
	protected Boolean allowAdjustPrice;

	/**
	 * 体检报告二级站点查看功能开关,默认true
	 */
	protected Boolean secondSiteSwitch;

	/**
	 * 团检电话
	 */
	protected String groupExamTel;
	
	/**
	 * 账户支付
	 */
	protected Boolean accountPay;

	/**
	 * 支付宝支付
	 */
	protected Boolean aliPay;
	/**
	 * 微信支付
	 */
	protected Boolean weiXinPay;

	/**
	 * 现场支付
	 */
	protected Boolean acceptOfflinePay;
	

	public Boolean getAccountPay() {
		return accountPay;
	}

	public void setAccountPay(Boolean accountPay) {
		this.accountPay = accountPay;
	}

	public Boolean getAliPay() {
		return aliPay;
	}

	public void setAliPay(Boolean aliPay) {
		this.aliPay = aliPay;
	}

	public Boolean getWeiXinPay() {
		return weiXinPay;
	}

	public void setWeiXinPay(Boolean weiXinPay) {
		this.weiXinPay = weiXinPay;
	}

	public Boolean getAcceptOfflinePay() {
		return acceptOfflinePay;
	}

	public void setAcceptOfflinePay(Boolean acceptOfflinePay) {
		this.acceptOfflinePay = acceptOfflinePay;
	}

	public Boolean getAllowAdjustPrice() {
		return allowAdjustPrice;
	}

	public void setAllowAdjustPrice(Boolean allowAdjustPrice) {
		this.allowAdjustPrice = allowAdjustPrice;
	}

	public Boolean getSecondSiteSwitch() {
		return secondSiteSwitch;
	}

	public void setSecondSiteSwitch(Boolean secondSiteSwitch) {
		this.secondSiteSwitch = secondSiteSwitch;
	}

	public Integer getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(Integer deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}

	public String getServiceTel() {
		return serviceTel;
	}

	public void setServiceTel(String serviceTel) {
		this.serviceTel = serviceTel;
	}

	public String getTechnicalTel() {
		return technicalTel;
	}

	public void setTechnicalTel(String technicalTel) {
		this.technicalTel = technicalTel;
	}

	public String getGroupExamTel() {
		return groupExamTel;
	}

	public void setGroupExamTel(String groupExamTel) {
		this.groupExamTel = groupExamTel;
	}
	
	public int getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
