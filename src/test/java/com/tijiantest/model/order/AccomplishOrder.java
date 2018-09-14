package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.item.ExamItem;

public class AccomplishOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2607073485640245857L;

	private Integer id;

	private Integer orderId;

	private String orderNum;

	private String hisItemIds;

	private List<ExamItem> examItemList;

	/**
	 * 订单状态，是否读取
	 * 关联tbl_order的cancel_order_status无则为NULL
	 */
	private String status;

	/**
	 * 	是否到检，his间会有差异。
	 */
	private String absent;

	private String name;

	private Date examDate;

	private String idcard;

	private String mobile;

	private String age;

	/**
	 * 1-男性；0-女性
	 */
	private String gender;

	/**
	 * 已婚/未婚
	 */
	private String marriageStatus;

	private String examCompany;

	private String department;

	private String workno;

	/**
	 * 现场加项目付款	精确到分
	 */
	private Double offlinePayAmount;

	private Date createTime;

	private Date updateTime;
	
	private String hisBm;
	
	private Map<String, Object> dynamicAttributes;
	
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getAbsent() {
		return absent;
	}

	public void setAbsent(String absent) {
		this.absent = absent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(String marriageStatus) {
		this.marriageStatus = marriageStatus;
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

	public String getWorkno() {
		return workno;
	}

	public void setWorkno(String workno) {
		this.workno = workno;
	}

	public Double getOfflinePayAmount() {
		return offlinePayAmount;
	}

	public void setOfflinePayAmount(Double offlinePayAmount) {
		this.offlinePayAmount = offlinePayAmount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ExamItem> getExamItemList() {
		return examItemList;
	}

	public void setExamItemList(List<ExamItem> examItemList) {
		this.examItemList = examItemList;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getHisItemIds() {
		return hisItemIds;
	}

	public void setHisItemIds(String hisItemIds) {
		this.hisItemIds = hisItemIds;
	}

	public Map<String, Object> getDynamicAttributes() {
		return dynamicAttributes;
	}

	public void setDynamicAttributes(Map<String, Object> dynamicAttributes) {
		this.dynamicAttributes = dynamicAttributes;
	}

	public String getHisBm() {
		return hisBm;
	}

	public void setHisBm(String hisBm) {
		this.hisBm = hisBm;
	}
}
