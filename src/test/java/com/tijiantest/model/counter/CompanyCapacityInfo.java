package com.tijiantest.model.counter;

import java.io.Serializable;

public class CompanyCapacityInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8293843568987085179L;
	private Integer id;
	
	private Integer companyId;//体检单位
	
	private Integer oldCompanyId;//老的单位ID
	
	private Integer hospitalId;//体检中心
	
	private String promptText;//预留日提示文字
	private boolean canOrder;//true：非预留日可约；false：仅预留日可约
	
	private Integer aheadDays;//预留名额提前释放天数
	
	
	public Integer getOldCompanyId() {
		return oldCompanyId;
	}

	public void setOldCompanyId(Integer oldCompanyId) {
		this.oldCompanyId = oldCompanyId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public boolean isCanOrder() {
		return canOrder;
	}

	public void setCanOrder(boolean canOrder) {
		this.canOrder = canOrder;
	}

	public Integer getAheadDays() {
		return aheadDays;
	}

	public void setAheadDays(Integer aheadDays) {
		this.aheadDays = aheadDays;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyCapacityInfo [id=");
		builder.append(id);
		builder.append(", companyId=");
		builder.append(companyId);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", promptText=");
		builder.append(promptText);
		builder.append(", canOrder=");
		builder.append(canOrder);
		builder.append(", aheadDays=");
		builder.append(aheadDays);
		builder.append("]");
		return builder.toString();
	}

}
