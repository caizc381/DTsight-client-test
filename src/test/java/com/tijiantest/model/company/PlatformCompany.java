package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台单位模型
 * @author yuefengyang
 *
 */
public class PlatformCompany implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3264445980218472129L;
	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 修改时间
	 */
	private Date gmtModified;
	
	/**
	 * 名称
	 * 
	 */
	private String name;

	/**
	 * 员工号导入
	 */
	@Deprecated
	private Boolean employeeImport=false;

	/**
	 * 拼音
	 */
	private String pinyin;
	/**
	 * 初始化类型,hospital/channel
	 */
	private String init;
	/**
	 * 删除标记
	 */
	private Boolean deleted;

	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * tb_exam_company.id，老表主键
	 */
	private Integer tbExamCompanyId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getEmployeeImport() {
		return employeeImport;
	}

	public void setEmployeeImport(Boolean employeeImport) {
		this.employeeImport = employeeImport;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}
	
}
