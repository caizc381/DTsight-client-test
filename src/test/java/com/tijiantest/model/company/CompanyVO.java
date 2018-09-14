package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 单位VO
 * @author admin
 *
 */
public class CompanyVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3072598571044388120L;


	private Integer id;//为兼容老版本，暂时赋值为老单位id
	private Integer newCompanyId;//新单位id
	private Date gmtCreated;//创建时间
	private Date gmtModified;//更新时间
	private String name;//名称
	private Integer type;//单位类型
	private List<ExamCompanyHospital> companyHospitalList;//设置
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNewCompanyId() {
		return newCompanyId;
	}
	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
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
	public List<ExamCompanyHospital> getCompanyHospitalList() {
		return companyHospitalList;
	}
	public void setCompanyHospitalList(List<ExamCompanyHospital> companyHospitalList) {
		this.companyHospitalList = companyHospitalList;
	}	
}
