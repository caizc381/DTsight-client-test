package com.tijiantest.model.account;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AccountImportInfoDto implements Serializable {

    private String id;

    // 新单位ID，改造之后的
    private Integer newComanyId;

    // 客户经理
    private Integer managerId;

    // 体检单位(改造前老单位)
    private Integer companyId;

    // 体检单位
    private Integer newCompanyId;

    // 机构ID
    private Integer organizationId;

    // 机构类型
    private Integer organizationType;

    // 客户id
    private Integer customerId;

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

    // 初始化手机号
    private String initialMobile;

    //是否是标准手机号
    private Boolean isStandardMobile;

    // 身份证
    private String idCard;

    // 职级
    private String position;

    // 退休状态
    private String retire;

    // 保健号
    private String healthNum;

    // 保健级别
    private String healthLevel;

    // 社保号
    private String socialSecurity;

    // 地址
    private String address;

    // 导入操作员
    private String operator;

    // 导入序列
    private Integer sequence;

    private String addAccountType;

    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNewComanyId() {
        return newComanyId;
    }

    public void setNewComanyId(Integer newComanyId) {
        this.newComanyId = newComanyId;
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getRetire() {
        return retire;
    }

    public void setRetire(String retire) {
        this.retire = retire;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getAddAccountType() {
        return addAccountType;
    }

    public void setAddAccountType(String addAccountType) {
        this.addAccountType = addAccountType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getIsStandardMobile() {
        return isStandardMobile;
    }

    public void setIsStandardMobile(Boolean isStandardMobile) {
        this.isStandardMobile = isStandardMobile;
    }
}

