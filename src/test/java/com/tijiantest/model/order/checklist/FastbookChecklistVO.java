package com.tijiantest.model.order.checklist;

import java.util.Date;
import java.util.Map;

/**
 * 极速预约打印预检单，返回对象
 * @author admin
 *
 */
public class FastbookChecklistVO extends ChecklistVO{
	@SuppressWarnings("rawtypes")
	private Map order;
	
	private String name;//体检人姓名
	private String examCompany;//单位
	private String department;//部门
	private String mealName;//套餐名称
	private Date formatExamDate;//体检日期
	private Integer gender;// 性别
	private Integer age;//年龄
	private String marriageStatus;//婚姻状况
	private String mobile;//联系方式
	private String idCard;//身份证
	@SuppressWarnings("rawtypes")
	public Map getOrder() {
		return order;
	}
	@SuppressWarnings("rawtypes")
	public void setOrder(Map order) {
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExamCompany() {
		return examCompany;
	}
	public void setExamCompany(String examCompany) {
		this.examCompany = examCompany;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getMealName() {
		return mealName;
	}
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	public Date getFormatExamDate() {
		return formatExamDate;
	}
	public void setFormatExamDate(Date formatExamDate) {
		this.formatExamDate = formatExamDate;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getMarriageStatus() {
		return marriageStatus;
	}
	public void setMarriageStatus(String marriageStatus) {
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
	
	
}
