package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;

public class OrderExportExtInfoSnapshot implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2472568115553995139L;
	
	private String genderLabel;
	private String groupName;
	private String retireLabel;
	private String marriageStatusLabel;
	private String payType;
	private Integer exportAdjustPrice;
	private Double exportDiscount;
	private String exportSelfMoney;
	private String hisItemIds;
	private String exportAccountName;
	private Integer exportAccountId;
	private Date exportTime;
	private Boolean isExport;
	private Boolean exportImmediately;
	private String exportExamDate;
	private String birthDate;
	private String exportOrderPrice;
	private String exportFailedMsg;

	public String getExportFailedMsg() {
		return exportFailedMsg;
	}

	public void setExportFailedMsg(String exportFailedMsg) {
		this.exportFailedMsg = exportFailedMsg;
	}

	public String getGenderLabel() {
		return genderLabel;
	}
	public void setGenderLabel(String genderLabel) {
		this.genderLabel = genderLabel;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getRetireLabel() {
		return retireLabel;
	}
	public void setRetireLabel(String retireLabel) {
		this.retireLabel = retireLabel;
	}
	public String getMarriageStatusLabel() {
		return marriageStatusLabel;
	}
	public void setMarriageStatusLabel(String marriageStatusLabel) {
		this.marriageStatusLabel = marriageStatusLabel;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Integer getExportAdjustPrice() {
		return exportAdjustPrice;
	}
	public void setExportAdjustPrice(Integer exportAdjustPrice) {
		this.exportAdjustPrice = exportAdjustPrice;
	}
	public Double getExportDiscount() {
		return exportDiscount;
	}
	public void setExportDiscount(Double exportDiscount) {
		this.exportDiscount = exportDiscount;
	}
	public String getExportSelfMoney() {
		return exportSelfMoney;
	}
	public void setExportSelfMoney(String exportSelfMoney) {
		this.exportSelfMoney = exportSelfMoney;
	}
	public String getHisItemIds() {
		return hisItemIds;
	}
	public void setHisItemIds(String hisItemIds) {
		this.hisItemIds = hisItemIds;
	}
	public String getExportAccountName() {
		return exportAccountName;
	}
	public void setExportAccountName(String exportAccountName) {
		this.exportAccountName = exportAccountName;
	}
	public Integer getExportAccountId() {
		return exportAccountId;
	}
	public void setExportAccountId(Integer exportAccountId) {
		this.exportAccountId = exportAccountId;
	}
	public Date getExportTime() {
		return exportTime;
	}
	public void setExportTime(Date exportTime) {
		this.exportTime = exportTime;
	}
	public Boolean getIsExport() {
		return isExport;
	}
	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}
	public Boolean getExportImmediately() {
		return exportImmediately;
	}
	public void setExportImmediately(Boolean exportImmediately) {
		this.exportImmediately = exportImmediately;
	}
	public String getExportExamDate() {
		return exportExamDate;
	}
	public void setExportExamDate(String exportExamDate) {
		this.exportExamDate = exportExamDate;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getExportOrderPrice() {
		return exportOrderPrice;
	}
	public void setExportOrderPrice(String exportOrderPrice) {
		this.exportOrderPrice = exportOrderPrice;
	}
	
}
