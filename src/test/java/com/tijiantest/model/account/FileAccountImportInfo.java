package com.tijiantest.model.account;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.tijiantest.util.PinYinUtil;

/**
 * 账户批量导入文件记录信息.
 * 
 * @author ren
 *
 */
public class FileAccountImportInfo extends DomainObjectBase {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 5842982306930585539L;
	
	/**
	 * 多个错误原因划界符
	 */
	public static final String FAIL_REASON_DELIMIT = "-";

	private String id;
	
	// 客户经理
	private Integer managerId;
	
	// 客户id
	private Integer customerId;
	
	// 体检单位(改造前老单位)
	private Integer companyId;

	// 体检单位
	private Integer newCompanyId;

	// 机构ID
	private Integer organizationId;

	// 机构类型
	private Integer organizationType;

	// 账户姓名(不要修改属性名称)
	private String name;
	
	// 员工号
	private String employeeId;
	
	// 账户年龄
	private String age;
	
	// 性别
	private String gender;
	
	// 婚姻状态
	private String marriageStatus;
	
	// 所在组
	private String group;
	
	// 部门
	private String department;
	
	// 来源表
	private String sheetName;
	
	// 手机号
	private String mobile;
	
	//原始手机号
	private String initialMobile;
	
	//是否是标准手机号
	private Boolean isStandardMobile;
	
	// 身份证
	private String idCard;
	
	// 职级
	private String position;
	
	// 退休状态
	private String retire;
	
	private String healthNum;
	
	private String healthLevel;
	
	private String socialSecurity;
	
	// 消息不合法原因
    private StringBuilder failReason;
    
    // 导入操作员
    private String operator;
	
	// 导入序列
	private Integer sequence;
	
	// 如果所有字段为空，则为true，否则false
	private boolean isEmpty = true;
	
	/**
	 * 证件类型
	 */
	private Integer idType = IdTypeEnum.IDCARD.getCode();
	
	private Integer type;
	
	private Date createTime;
	/**
	 * 证件状态 
	 * @see com.mytijian.account.enums.AccountStatusEnum
	 */
	private Integer idStatus;

	private String address;

	private String addAccountType;

	public String getAddAccountType() {
		return addAccountType;
	}

	public void setAddAccountType(String addAccountType) {
		this.addAccountType = addAccountType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
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

	public String getName() {
		return PinYinUtil.fullWidth2halfWidth(name);
	}

	public void setName(String name) {
		this.name = PinYinUtil.fullWidth2halfWidth(name);
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getInitialMobile() {
		return initialMobile;
	}

	public void setInitialMobile(String initialMobile) {
		this.initialMobile = initialMobile;
	}

	public Boolean getIsStandardMobile() {
		return isStandardMobile;
	}

	public void setIsStandardMobile(Boolean isStandardMobile) {
		this.isStandardMobile = isStandardMobile;
	}

	public void setFailReason(StringBuilder failReason) {
		this.failReason = failReason;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getHealthNum() {
		return healthNum;
	}

	public void setHealthNum(String healthNum) {
		this.healthNum = healthNum;
	}

	public String getHealthLevel() {
		return healthLevel;
	}

	public void setHealthLevel(String healthLevel) {
		this.healthLevel = healthLevel;
	}

	public String getSocialSecurity() {
		return socialSecurity;
	}

	public void setSocialSecurity(String socialSecurity) {
		this.socialSecurity = socialSecurity;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	public String getFailReason() {
		if(null == failReason) {
			return null;
		}
		
		String str = failReason.toString();
		if ((FAIL_REASON_DELIMIT + Validator.NULL_IDCARD_NAME.getDescription() + FAIL_REASON_DELIMIT + Validator.NULL_NAME_GENDER_AGE.getDescription()).equals(str)) {
			str = Validator.NULL_IDCARD_NAME.getDescription() + "/" + Validator.NULL_NAME_GENDER_AGE.getDescription();
		}
		return str;
	}

	public void setFailReason(String failReason) {
		if(StringUtils.isNotEmpty(failReason)) {
			this.failReason = new StringBuilder(failReason);
		} else {
			this.failReason = new StringBuilder();
		}
		
	}
	
	public String getMarriageStatus() {
		return marriageStatus;
	}
	
	public void setMarriageStatus(String marriageStatus) {
		if (null == marriageStatus) {
			return;
		}
		
		if ("0".equals(marriageStatus)) {
		   this.marriageStatus = "未婚";
		   return ;
		} 
		
		if ("1".equals(marriageStatus)) {
			this.marriageStatus = "已婚";
			return ;
		}
		this.marriageStatus = marriageStatus;
	}
	
	public String getRetire() {
		return retire;
	}

	public void setRetire(String retire) {
		if (null == retire) {
			return;
		}
		
		if ("0".equals(retire)) {
		   this.retire = "在职";
		   return ;
		} 
		
		if ("1".equals(retire)) {
			this.retire = "退休";
			return ;
		}
		this.retire = retire;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public void appendFailMsg(String msg) {
		if(null == this.failReason) {
			this.failReason = new StringBuilder();
		}
		failReason.append("-").append(msg);
	}
	
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getIdType() {
		return idType;
	}

	public void setIdType(Integer idType) {
		this.idType = idType;
	}

	public Integer getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
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
		builder.append("FileAccountImportInfo [id=");
		builder.append(id);
		builder.append(",managerId=");
		builder.append(managerId);
		builder.append(", companyId=");
		builder.append(companyId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", employeeId=");
		builder.append(employeeId);
		builder.append(", newCompanyId=");
		builder.append(newCompanyId);
		builder.append(", organizationId=");
		builder.append(organizationId);
		builder.append(", organizationType=");
		builder.append(organizationType);
		builder.append(", age=");
		builder.append(age);
		builder.append(", gender=");
		builder.append(gender);
		builder.append(", marriageStatus=");
		builder.append(marriageStatus);
		builder.append(", group=");
		builder.append(group);
		builder.append(", department=");
		builder.append(department);
		builder.append(", sheetName=");
		builder.append(sheetName);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", idCard=");
		builder.append(idCard);
		builder.append(", position=");
		builder.append(position);
		builder.append(", retire=");
		builder.append(retire);
		builder.append(", healthNum=");
		builder.append(healthNum);
		builder.append(", healthLevel=");
		builder.append(healthLevel);
		builder.append(", socialSecurity=");
		builder.append(socialSecurity);
		builder.append(", failReason=");
		builder.append(failReason);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", sequence=");
		builder.append(sequence);
		builder.append(", isEmpty=");
		builder.append(isEmpty);
		builder.append(", idType=");
		builder.append(idType);
		builder.append(", address=");
		builder.append(address);
		builder.append("]");
		return builder.toString();
	}

}
