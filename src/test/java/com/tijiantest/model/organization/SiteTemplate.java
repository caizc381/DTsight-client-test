package com.tijiantest.model.organization;

import java.util.List;

//二级站点模板
public class SiteTemplate {
	private Integer id;
	private String name;
	private Integer type;
	private Integer defaultCssId;//默认色调
	private Integer hospitalId;//体检中心id
	private Boolean isCustom;//是否是定制化模板
	private SiteTemplate parent;
	List<Module> modules;
	
	private Boolean isDefault;
	private Integer forOrganization;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getDefaultCssId() {
		return defaultCssId;
	}
	public void setDefaultCssId(Integer defaultCssId) {
		this.defaultCssId = defaultCssId;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Boolean getIsCustom() {
		return isCustom;
	}
	public void setIsCustom(Boolean isCustom) {
		this.isCustom = isCustom;
	}
	public SiteTemplate getParent() {
		return parent;
	}
	public void setParent(SiteTemplate parent) {
		this.parent = parent;
	}
	public List<Module> getModules() {
		return modules;
	}
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Integer getForOrganization() {
		return forOrganization;
	}
	public void setForOrganization(Integer forOrganization) {
		this.forOrganization = forOrganization;
	}
	
	
}
