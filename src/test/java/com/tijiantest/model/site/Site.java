package com.tijiantest.model.site;

import java.io.Serializable;

public class Site implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7376943743708520952L;

	private int id;
	private String url;
	private int managerId;
	private int hospitalId;
	private String css;
	private String weixinName;
	/**
	 * 二级站点类型，1：体检中心，2：健康管理公司
	 */
	private int siteType;
	
	/**
	 * pc端模板
	 */
	private Integer templateId;
	
	/**
	 * 手机端模板
	 */
	private Integer mobileTemplateId;
	
	public String getWeixinName() {
		return weixinName;
	}

	public void setWeixinName(String weixinName) {
		this.weixinName = weixinName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getManagerId() {
		return managerId;
	}

	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}

	public int getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public int getSiteType() {
		return siteType;
	}

	public void setSiteType(int siteType) {
		this.siteType = siteType;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getMobileTemplateId() {
		return mobileTemplateId;
	}

	public void setMobileTemplateId(Integer mobileTemplateId) {
		this.mobileTemplateId = mobileTemplateId;
	}
	
}
