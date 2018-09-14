package com.tijiantest.model.account;

import com.tijiantest.util.pagination.Page;

public class ListCustomerVo {

	private Integer companyId;//体检中心ID
	private Integer newCompanyId;//体检单位
	private Integer organizaitonId;//机构ID
	private Integer organizationType;//机构类型
	private String name;
	private String mobile;
	private Integer serachMobile;
	private Integer beforeDays;//用户导入天数，默认查询导入180天内的用户信息
	private boolean hasOrdered = true;//是否预约过
	private Integer gender;//性别
	private Integer minAge;//年龄范围 - 最小年龄
	private Integer maxAge;//年龄范围 - 最大年龄
	private String recentCard;
	private String recentMeal;
	private Integer marriageStatus;//婚姻状况
	private String group;//所在组
	private String department;//部门
	private String sheetName;//来源表
	private String position;//职级
	private Integer isRetire;//退休
	private Page page;//分页信息
	private String searchKey;//检索参数
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Integer getNewCompanyId() {
		return newCompanyId;
	}
	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}
	public Integer getOrganizaitonId() {
		return organizaitonId;
	}
	public void setOrganizaitonId(Integer organizaitonId) {
		this.organizaitonId = organizaitonId;
	}
	public Integer getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getSerachMobile() {
		return serachMobile;
	}
	public void setSerachMobile(Integer serachMobile) {
		this.serachMobile = serachMobile;
	}
	public Integer getBeforeDays() {
		return beforeDays;
	}
	public void setBeforeDays(Integer beforeDays) {
		this.beforeDays = beforeDays;
	}
	public boolean isHasOrdered() {
		return hasOrdered;
	}
	public void setHasOrdered(boolean hasOrdered) {
		this.hasOrdered = hasOrdered;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getMinAge() {
		return minAge;
	}
	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	public Integer getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	public String getRecentCard() {
		return recentCard;
	}
	public void setRecentCard(String recentCard) {
		this.recentCard = recentCard;
	}
	public String getRecentMeal() {
		return recentMeal;
	}
	public void setRecentMeal(String recentMeal) {
		this.recentMeal = recentMeal;
	}
	public Integer getMarriageStatus() {
		return marriageStatus;
	}
	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getIsRetire() {
		return isRetire;
	}
	public void setIsRetire(Integer isRetire) {
		this.isRetire = isRetire;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	
	
}
