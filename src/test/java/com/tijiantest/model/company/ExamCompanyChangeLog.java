package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;

public class ExamCompanyChangeLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5424623253634615887L;

	private Integer hospitalId;
	private Integer companyId;
	private String oldName;
	private String name;

	/**
	 * @see com.tijiantest.model.company.CompanyChangeStatusEnum
	 */
	private Integer status;
	private Date createTime;
	private Date updateTime;

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
