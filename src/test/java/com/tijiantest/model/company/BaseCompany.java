package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;
/**
 * 单位基础模型
 * 类BaseCompany.java的实现描述：TODO 类实现描述 
 * @author yuefengyang 2017年5月19日 上午11:36:08
 */
public class BaseCompany implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4279162953984019636L;

	private Integer id;
	/**
	 * 单位名称
	 */
	private String name;

	/**
	 * 机构id
	 */
	private Integer organizationId;

	/**
	 * 机构名称
	 */
	private String organizationName;

	/**
	 * 老单位id
	 */
	private Integer tbExamCompanyId;

	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 更新时间
	 */
	private Date gmtModified;
	
	/**
	 * 平台单位id
	 */
	private Integer platformCompanyId;
	
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
	 * 检前短息提前发送天数
	 */
	private Integer sendExamSmsDays;
	
	/**
	 * 拼音
	 */
	private String pinyin;
	/**
	 * 删除标记，status映射到该字段
	 */
	private Boolean deleted;

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

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public Date getGmtCreated() {
		return gmtCreated;
	}

	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
