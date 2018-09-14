package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台单位申请日志
 * 类PlatformCompanyApplyLogDO.java的实现描述：TODO 类实现描述 
 * @author yuefengyang 2017年8月2日 下午5:44:01
 */
public class PlatformCompanyApplyLogDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 427901992765831122L;

	private Integer newCompanyId;

	private Integer hospitalId;

	private String settingDetail;

	private Integer status;

	private Date gmtCreated;

	private Date gmtModified;

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getSettingDetail() {
		return settingDetail;
	}

	public void setSettingDetail(String settingDetail) {
		this.settingDetail = settingDetail;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

}
