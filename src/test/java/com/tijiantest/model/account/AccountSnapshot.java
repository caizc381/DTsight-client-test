package com.tijiantest.model.account;

import java.io.Serializable;

/**
 * 详情参考用户模块Account、AccountInfo信息
 * @author Administrator
 */
public class AccountSnapshot implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5346757107170247357L;
	
	private Integer id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private Integer gender;
	/**
	 * 年龄
	 */
	private Integer age;
	/**
	 * 出生年
	 */
	private Integer birthYear;
	/**
	 * 0:未婚 1：已婚
	 */
	private Integer marriageStatus;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 证件号
	 */
	private String idCard;
	/**
	 * 员工号
	 */
	private String employeeId;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 账户类型
	 */
	private Integer type;
	/**
	 * 证件类型 
	 * @see com.mytijian.account.enums.IdTypeEnum
	 */
	private Integer idType;
	
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
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getMarriageStatus() {
		return marriageStatus;
	}
	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIdType() {
		return idType;
	}
	public void setIdType(Integer idType) {
		this.idType = idType;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}
	
}
