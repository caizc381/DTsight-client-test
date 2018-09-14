package com.tijiantest.model.organization;

import com.tijiantest.model.resource.Address;

public class OrganizationHospitalRalationDto {

	private int id;
	/**
	 * 医院名称
	 */
	private String name;
	/**
	 * 是否可用
	 */
	private Integer enable;
	/**
	 * 是否在列表中展示
	 */
	private int showInList;
	/**
	 * 是否需要导出为xls(对接方式[0:深对接,1:浅对接,2:不限])
	 */
	private Integer exportWithXls;
	/**
	 * 地址信息
	 */
	private Address address;
	/**
	 * 渠道商id
	 */
	private int organizationId;
	/**
	 * 体检中心id
	 */
	private int hospitalId;
	/**
	 * 关联状态(1:可用,2:不可用)
	 */
	private int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public int getShowInList() {
		return showInList;
	}
	public void setShowInList(int showInList) {
		this.showInList = showInList;
	}
	public Integer getExportWithXls() {
		return exportWithXls;
	}
	public void setExportWithXls(Integer exportWithXls) {
		this.exportWithXls = exportWithXls;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public int getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	public int getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}	
	
}
