package com.tijiantest.model.order;

import java.util.Date;

public class OrderManageDto {

	private Integer id;
	private String name;
	private String idCard;
	private Date insertTime;
	private Integer hospitalId;
	private String hospitalName;
	private String mealName;
	private String hosPhone;
	private Date examDate;
	private String examTimeIntervalName;
	private Integer status;
	private Integer price;
	private String mealDetail;
	private Integer addPrice;
	private Integer companyId;
	private boolean export;
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
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getMealName() {
		return mealName;
	}
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	public String getHosPhone() {
		return hosPhone;
	}
	public void setHosPhone(String hosPhone) {
		this.hosPhone = hosPhone;
	}
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}
	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getMealDetail() {
		return mealDetail;
	}
	public void setMealDetail(String mealDetail) {
		this.mealDetail = mealDetail;
	}
	public Integer getAddPrice() {
		return addPrice;
	}
	public void setAddPrice(Integer addPrice) {
		this.addPrice = addPrice;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public boolean isExport() {
		return export;
	}
	public void setExport(boolean export) {
		this.export = export;
	}	
	
}
