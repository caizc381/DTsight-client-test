package com.tijiantest.model.company;

import java.util.Date;

public class CompanyHisRelationDto {
	private Integer id;
	private Integer hospitalId;
	private Integer crmCompanyId;
	private String crmCompanyName;
	private String hisCompanyCode;
	private String hisCompanyName;
	private Integer isDeleted;
	private Integer isTar;
	private Integer syncStatus;
	private Date insertTime;
	private Date updateTime;
	
	private int companyType;//单位类型
	private Double discount;//折扣
	private String description;//单位描述
    /**
     * 新单位id
     */
    private Integer newCompanyId;
    
    /**
     * 平台单位id
     */
    private Integer platformCompanyId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getCrmCompanyId() {
		return crmCompanyId;
	}
	public void setCrmCompanyId(Integer crmCompanyId) {
		this.crmCompanyId = crmCompanyId;
	}
	public String getCrmCompanyName() {
		return crmCompanyName;
	}
	public void setCrmCompanyName(String crmCompanyName) {
		this.crmCompanyName = crmCompanyName;
	}
	public String getHisCompanyCode() {
		return hisCompanyCode;
	}
	public void setHisCompanyCode(String hisCompanyCode) {
		this.hisCompanyCode = hisCompanyCode;
	}
	public String getHisCompanyName() {
		return hisCompanyName;
	}
	public void setHisCompanyName(String hisCompanyName) {
		this.hisCompanyName = hisCompanyName;
	}
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Integer getIsTar() {
		return isTar;
	}
	public void setIsTar(Integer isTar) {
		this.isTar = isTar;
	}
	public Integer getSyncStatus() {
		return syncStatus;
	}
	public void setSyncStatus(Integer syncStatus) {
		this.syncStatus = syncStatus;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getCompanyType() {
		return companyType;
	}
	public void setCompanyType(int companyType) {
		this.companyType = companyType;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNewCompanyId() {
		return newCompanyId;
	}
	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}
	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}
	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}
	
}
