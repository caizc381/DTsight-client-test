package com.tijiantest.model.account;

import java.util.Calendar;
import java.util.Date;

import com.tijiantest.util.PinYinUtil;

/**
 * 账号批量跟新参数内部使用model
 * 
 * @author ren
 *
 */
public class AcctRelationQueryDto {
	
	// 客户经理id
	private Integer managerId;
	
	// 体检中心id
	private Integer companyId;
	
	private Integer customerId;

	// 体检单位
	private Integer newCompanyId;

	// 机构ID
	private Integer organizationId;

	// 机构类型
	private Integer organizationType;

	private Integer type;
	
	// 姓名
	private String name;
	
	// 手机
	private String mobile;
	
	private Integer serachMobile;
	
	// 身份证id
	private String idCard;
	
	// 是否预约过
	private boolean hasOrdered;
	//是否发过卡
	private Boolean hasCarded;
    
	// 性别
	private Integer gender;
	
	// 年龄范围-最小年龄
	private Integer startBirthYear;
	
	// 年龄范围-最大年龄
    private Integer endBirthYear;
    
    // 最近发卡
    private String recentCard;
    
    // 最近预约
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
	
	// 员工号
	private String employeeId;
	
	// 用户导入天数，默认查询导入180天内的用户信息
	private Integer beforeDays;

	// 用户导入最久时间
	private Date dateBeforeDays;
	
	// 已预约过期时间
	private Date recentOrderDate;
	
	private Boolean isDelete;
	
	/**
	 * 检索参数
	 */
	private String searchKey;

	public AcctRelationQueryDto() {
	}


	public Boolean isHasCarded() {
		return hasCarded;
	}

	public void setHasCarded(Boolean hasCarded) {
		this.hasCarded = hasCarded;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public boolean getHasOrdered() {
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

	public String getName() {
		return PinYinUtil.fullWidth2halfWidth(name);
	}

	public void setName(String name) {
		this.name = PinYinUtil.fullWidth2halfWidth(name);
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

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
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

	public Integer getStartBirthYear() {
		return startBirthYear;
	}

	public void setStartBirthYear(Integer minAge) {
		this.startBirthYear = (minAge == null) ? null : (Calendar.getInstance()
				.get(Calendar.YEAR) - minAge);
	}

	public Integer getEndBirthYear() {
		return endBirthYear;
	}

	public void setEndBirthYear(Integer maxAge) {
		this.endBirthYear = (maxAge == null) ? null : (Calendar.getInstance()
				.get(Calendar.YEAR) - maxAge);
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getBeforeDays() {
		return beforeDays;
	}

	public void setBeforeDays(Integer beforeDays) {
		this.beforeDays = beforeDays;
	}

	public Date getDateBeforeDays(Integer beforeDays) {
		Calendar calendar = Calendar.getInstance();
		if (null == beforeDays) {
			return null;
		}

		if (beforeDays == 0) {
			return calendar.getTime();
		}
		calendar.add(Calendar.DATE, -beforeDays);
		return calendar.getTime();
	}

	public Date getRecentOrderDate() {
		return recentOrderDate;
	}

	public void setRecentOrderDate(Date recentOrderDate) {
		this.recentOrderDate = recentOrderDate;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AcctRelationQueryDto [managerId=");
		builder.append(managerId);
		builder.append(", companyId=");
		builder.append(companyId);
		builder.append(", newCompanyId=");
		builder.append(newCompanyId);
		builder.append(", organizationId=");
		builder.append(organizationId);
		builder.append(", organizationType=");
		builder.append(organizationType);
		builder.append(", customerId=");
		builder.append(customerId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", idCard=");
		builder.append(idCard);
		builder.append(", hasOrdered=");
		builder.append(hasOrdered);
		builder.append(", gender=");
		builder.append(gender);
		builder.append(", startBirthYear=");
		builder.append(startBirthYear);
		builder.append(", endBirthYear=");
		builder.append(endBirthYear);
		builder.append(", recentCard=");
		builder.append(recentCard);
		builder.append(", recentMeal=");
		builder.append(recentMeal);
		builder.append(", marriageStatus=");
		builder.append(marriageStatus);
		builder.append(", group=");
		builder.append(group);
		builder.append(", department=");
		builder.append(department);
		builder.append(", sheetName=");
		builder.append(sheetName);
		builder.append(", position=");
		builder.append(position);
		builder.append(", isRetire=");
		builder.append(isRetire);
		builder.append(", employeeId=");
		builder.append(employeeId);
		builder.append(", dateBeforeDays=");
		builder.append(dateBeforeDays);
		builder.append(", recentOrderDate=");
		builder.append(recentOrderDate);
		builder.append("]");
		return builder.toString();
	}

}
