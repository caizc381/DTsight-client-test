package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;

public class OrderBatch implements Serializable{

	private static final long serialVersionUID = -6191300397145966322L;

	/**
	 *批次标识 
	 */
	private Integer id;
	
	/**
	 * 客户现场付款，1：是，0：否
	 */	
	private Boolean isSitePay;
	
	/**
	 * 隐藏价格，1：是，0：否
	 */
	private Boolean isHidePrice;
	
	/**
	 * 允许减项，1：是，0：否
	 */
	private Boolean isReduceItem;
	
	/**
	 * 可改期，1：是，0：否
	 */
	private Boolean isChangeDate;
	/**
	 * 是否卡待预约
	 */
	private Boolean isProxyCard;
	
	private String mealName;
	private Integer mealPrice;
	/**
	 * 批量下单时的导出价格，不会因用户改项而改变，用于按人数结算
	 */
	private Integer bookExportPrice;
	private Integer mealId;
	private Integer mealGender;
	private Integer hospitalId;
	private String hospitalName;
	private Integer amount;
	private Date examDate;
	private Date bookTime;
	private String operator;
	private Integer managerId;
	private Integer companyId;
	private Integer examTimeIntervalId;
	private String examTimeIntervalName;
	/**
	 * 客户筛选条件
	 */
	private String queryCondition;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	public Integer getMealPrice() {
		return mealPrice;
	}

	public void setMealPrice(Integer mealPrice) {
		this.mealPrice = mealPrice;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
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
	
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Date getBookTime() {
		return bookTime;
	}

	public void setBookTime(Date bookTime) {
		this.bookTime = bookTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getMealGender() {
		return mealGender;
	}

	public void setMealGender(Integer mealGender) {
		this.mealGender = mealGender;
	}

	public Integer getExamTimeIntervalId() {
		return examTimeIntervalId;
	}

	public void setExamTimeIntervalId(Integer examTimeIntervalId) {
		this.examTimeIntervalId = examTimeIntervalId;
	}

	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}

	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}

	public Integer getBookExportPrice() {
		return bookExportPrice;
	}

	public void setBookExportPrice(Integer bookExportPrice) {
		this.bookExportPrice = bookExportPrice;
	}

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}
	public Boolean getIsProxyCard() {
		return this.isProxyCard;
	}

	public void setIsProxyCard(Boolean proxyCard) {
		this.isProxyCard = proxyCard;
	}

	public Boolean getIsSitePay() {
		return this.isSitePay;
	}

	public void setIsSitePay(Boolean sitePay) {
		this.isSitePay = sitePay;
	}

	public Boolean getIsHidePrice() {
		return this.isHidePrice;
	}

	public void setIsHidePrice(Boolean hidePrice) {
		this.isHidePrice = hidePrice;
	}

	public Boolean getIsChangeDate() {
		return this.isChangeDate;
	}

	public void setIsChangeDate(Boolean changeDate) {
		this.isChangeDate = changeDate;
	}

	public Boolean getIsReduceItem() {
		return this.isReduceItem;
	}

	public void setIsReduceItem(Boolean isReduceItem) {
		this.isReduceItem = isReduceItem;
	}

}
