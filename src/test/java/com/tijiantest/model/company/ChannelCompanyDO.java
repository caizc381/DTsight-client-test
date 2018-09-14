package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;

/**
 * 渠道商单位DO
 * @author admin
 *
 */
public class ChannelCompanyDO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 268097424400169865L;

	private Integer id;//主键
	private Date gmtCreated;//创建时间
	private Date gmtModified;//更新时间
	private String name;//名称
	private Integer platformCompanyId;//平台单位id
	private Integer organizationId;//机构id
	private String organizationName;//机构名称
	private Double discount;//折扣
	private Integer settlementMode;//结算模式
	private Boolean sendExamSms;//是否发送检前短信
	private Integer sendExamSmsDays;//检前短信提前天数
	private String pinyin;//拼音
	private Boolean deleted;//删除标记，原来的status映射到该字段
	private String description;//地区描述
	private Integer tbExamCompanyId;//关联tb_exam_company.id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}
	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}
	
}
