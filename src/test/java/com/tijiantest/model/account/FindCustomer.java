package com.tijiantest.model.account;

import java.util.Calendar;
import java.util.Date;

import com.tijiantest.util.pagination.Page;

/**
 * 
 * @author ChenSijia
 *
 */
public class FindCustomer {
	// 体检中心id
		private Integer companyId;

		// 体检单位
		private Integer newCompanyId;

		// 机构ID
		private Integer organizationId;

		// 机构类型
		private Integer organizationType;
		
		private String name;
		
		private String mobile;
		
		private String idCard;
		
		// 用户导入天数，默认查询导入180天内的用户信息
		private Integer beforeDays;
		
		// 是否预约过
		private boolean hasOrdered = true;
	    
		// 性别
		private Integer gender;
		
		// 年龄范围-最小年龄
		private Integer minAge;
		
		// 年龄范围-最大年龄
	    private Integer maxAge;
	    
	    private String recentCard;
	    
	    private String recentMeal;
		
		// 婚姻状况
		private Integer marriageStatus;
		
		// 所在组
		private String group;
		
		// 部门
		private String department;
		
		// 来源表
		private String sheetName;
		
		// 职级
		private String position;
		
		// 退休
		private Integer isRetire;
		
		// 分页信息
		private Page page;
		
		/**
		 * 检索参数
		 */
		private String searchKey;


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
		public Integer getOrganizationId() {
			return organizationId;
		}
		public void setOrganizationId(Integer organizationId) {
			this.organizationId = organizationId;
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
		public String getIdCard() {
			return idCard;
		}
		public void setIdCard(String idCard) {
			this.idCard = idCard;
		}
		public Integer getBeforeDays() {
			return beforeDays;
		}
		public void setBeforeDays(Integer beforeDays) {
			this.beforeDays = beforeDays;
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
		public boolean isHasOrdered() {
			return hasOrdered;
		}
		public void setHasOrdered(boolean hasOrdered) {
			this.hasOrdered = hasOrdered;
		}
		public Date getDateBeforeDays(Integer days) {
			Calendar calendar = Calendar.getInstance();
			if (null == days) {
				return null;
			}

			if (days == 0) {
				return calendar.getTime();
			}
			calendar.add(Calendar.DATE, -days);
			return calendar.getTime();
		}
}
