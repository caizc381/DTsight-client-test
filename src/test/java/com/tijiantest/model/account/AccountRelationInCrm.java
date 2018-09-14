/**
 * 
 */
package com.tijiantest.model.account;

import java.util.Date;




/**
 * 客户经理与账户关系表
 * 
 * @author ren
 *
 */
public class AccountRelationInCrm extends AccountRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	// 所在组
	private String group;

	// 部门
	private String department;
	
	// 来源表
	private String sheetName;
	
	// 最近发卡记录
	private Integer recentCard;
	
	// 最近预约
	private String recentMeal;
	
	// 员工号
	private String employeeId;
	
	// 职级
	private String position;
	
	// 退休, 0:未退休 1:退休
	private Integer isRetire;
	
	// 最近预约时间
	private Date recentOrderDate;
	
	private Integer sequence;

	// 操作员
	private String operator;
	
	private String failReason;
	
	private String loginName;
	private String addAccountType;
	
	public AccountRelationInCrm(Integer customerId,Integer recentCard, String recentMeal, Date recentOrderDate) {
		// TODO Auto-generated constructor stub
		this.id = customerId;
		this.recentCard = recentCard;
		this.recentMeal = recentMeal;
		this.recentOrderDate = recentOrderDate;
	}

	public AccountRelationInCrm() {
		// TODO Auto-generated constructor stub
	}

	public String getFailReason() {
		if(null == failReason) {
			return null;
		}
		return failReason.toString();
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
	public Integer getId() {
		return sequence;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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

	public Integer getRecentCard() {
		return recentCard;
	}

	public void setRecentCard(Integer recentCard) {
		this.recentCard = recentCard;
	}

	public String getRecentMeal() {
		return recentMeal;
	}

	public void setRecentMeal(String recentMeal) {
		this.recentMeal = recentMeal;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
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

	public Date getRecentOrderDate() {
		return recentOrderDate;
	}

	public void setRecentOrderDate(Date recentOrderDate) {
		this.recentOrderDate = recentOrderDate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getAddAccountType() {
		return addAccountType;
	}

	public void setAddAccountType(String addAccountType) {
		this.addAccountType = addAccountType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountRelationInCrm [id=");
		builder.append(id);
		builder.append(", companyId=");
		builder.append(this.getCompanyId());
		builder.append(", group=");
		builder.append(group);
		builder.append(", department=");
		builder.append(department);
		builder.append(", sheetName=");
		builder.append(sheetName);
		builder.append(", recentCard=");
		builder.append(recentCard);
		builder.append(", recentMeal=");
		builder.append(recentMeal);
		builder.append(", employeeId=");
		builder.append(employeeId);
		builder.append(", position=");
		builder.append(position);
		builder.append(", isRetire=");
		builder.append(isRetire);
		builder.append(", recentOrderDate=");
		builder.append(recentOrderDate);
		builder.append(", sequence=");
		builder.append(sequence);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", failReason=");
		builder.append(failReason);
		builder.append("]");
		return builder.toString();
	}
}
