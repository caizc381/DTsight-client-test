package com.tijiantest.model.company;

import java.io.Serializable;

/**
 * 渠道商单位
 * @author yuefengyang
 *
 */
/**
 * @author admin
 *
 */
/**
 * @author admin
 *
 */
public class ChannelCompany extends BaseCompany implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6915291515794804579L;
	/**
	 * 平台单位id
	 */
	private Integer platformCompanyId;

	/**
	 * 折扣
	 */
	private Double discount;

	/**
	 * 结算模式
	 */
	private Integer settlementMode;

	/**
	 * 是否发生检前短信
	 */
	private Boolean sendExamSms;

	/**
	 * 检前短信提前天数
	 */
	private Integer sendExamSmsDays;

	/**
	 * 拼音
	 */
	private String pinyin;

	/**
	 * 删除标记，原来的status映射到该字段
	 */
	private Boolean deleted;

	/**
	 * 地区描述
	 */
	private String description;

	/**
	 * 联系人
	 */
	private String contactName;

	/**
	 * 联系方式
	 */
	private String contactTel;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}

	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
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

	public Integer getSendExamSmsDays() {
		return sendExamSmsDays;
	}

	public void setSendExamSmsDays(Integer sendExamSmsDays) {
		this.sendExamSmsDays = sendExamSmsDays;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

}
