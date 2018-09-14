package com.tijiantest.model.order;

import java.io.Serializable;

public class ExamCompanySnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -456464793358837688L;
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 平台单位id
	 */
	private Integer platformCompanyId;
	/**
	 * 机构id
	 */
	private Integer organizationId;
	/**
	 * 折扣
	 */
	private Double discount;
	/**
	 * 结算方式
	 */
	private Integer settlementMode;
	/**
	 * 是否发生检前短信
	 */
	private Boolean sendExamSms;
	/**
	 * 拼音
	 */
	private String pinyin;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}
	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}
	public Integer getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Integer getSettlementMode() {
		return settlementMode;
	}
	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}
	public Boolean getSendExamSms() {
		return sendExamSms;
	}
	public void setSendExamSms(Boolean sendExamSms) {
		this.sendExamSms = sendExamSms;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

}
