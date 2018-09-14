package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;

public class HisCompanyDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8784269170825002518L;

	private Integer id;
	private String hisCompanyName;
	private String hisCompanyCode;
	private Integer hospitalId;
	private Date createTime;
	private Date modifyTime;
	private Integer isDeleted;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHisCompanyName() {
		return hisCompanyName;
	}
	public void setHisCompanyName(String hisCompanyName) {
		this.hisCompanyName = hisCompanyName;
	}
	public String getHisCompanyCode() {
		return hisCompanyCode;
	}
	public void setHisCompanyCode(String hisCompanyCode) {
		this.hisCompanyCode = hisCompanyCode;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
