package com.tijiantest.model.company;

import java.io.Serializable;
/**
 * 客户经理单位关系
 * 类ManagerExamCompanyRelation.java的实现描述：TODO 类实现描述 
 * @author yuefengyang 2017年5月16日 上午10:38:50
 */
public class ManagerExamCompanyRelation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户经理id
	 */
	private Integer managerId;
	
	/**
	 * 客户经理姓名
	 */
	private String managerName;
	/**
	 * 体检中心
	 */
	private Integer hospitalId;
	/**
	 * 单位
	 */
	private Integer companyId;
	/**
	 * 是否作为挂账单位
	 */
	private boolean asAccountCompany;
	/**
	 * 创建者
	 */
	private Integer createManagerId;

	/**
	 * 单位是否生效 1:生效 0:无效
	 */
	private Boolean status = true;

	/**
	 * 联系人
	 */
	private String contactName;

	/**
	 * 联系方式
	 */
	private String contactTel;

	/**
	 * 新单位id
	 */
	private Integer newCompanyId;

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

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

	public boolean isAsAccountCompany() {
		return asAccountCompany;
	}

	public void setAsAccountCompany(boolean asAccountCompany) {
		this.asAccountCompany = asAccountCompany;
	}

	public Integer getCreateManagerId() {
		return createManagerId;
	}

	public void setCreateManagerId(Integer createManagerId) {
		this.createManagerId = createManagerId;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	
}
