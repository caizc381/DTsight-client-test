package com.tijiantest.model.account;

import com.tijiantest.model.hospital.Address;
import com.tijiantest.util.PinYinUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
public class Examiner extends DomainObjectBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2593046628211207313L;

    private Integer id;

    private Integer managerId;

    private Integer customerId;

    private Integer type;

    private String name;

    private Integer gender;

    private String mobile;

    //原始手机号
    private String initialMobile;

    //是否是标准手机号
    private Boolean isStandardMobile;

    private String idCard;

    private Integer idType;

    private Integer status;

    private String email;

    private Address address;

    private Integer age;

    private Integer birthYear;

    private Integer marriageStatus;

    // 体检单位
    private Integer newCompanyId;

    // 机构ID
    private Integer organizationId;

    // 机构类型
    private Integer organizationType;

    private String healthNum;

    private String healthLevel;

    private String socialSecurity;

    private String pinYin;

    private Integer relationship;

    private String companyName;

    private String group;

    private String department;

    private String sheetName;

    private String position;

    private String operator;

    private Integer isSelf;

    private Integer relationId;

    private Integer isOper;

    private Integer isRetire;

    private String addAccountType;

    private Integer recentCard;

    private String recentMeal;

    private Date recentOrderDate;

    private Integer sequence;

    private String employeeId;

    private Integer height;

    private Integer weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStandardMobile() {
        return isStandardMobile;
    }

    public void setStandardMobile(Boolean standardMobile) {
        isStandardMobile = standardMobile;
    }

    public Integer getRelationship() {
        return relationship;
    }

    public void setRelationship(Integer relationship) {
        this.relationship = relationship;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
        this.age = (this.birthYear == null) ? null : Calendar.getInstance().get(Calendar.YEAR) - this.birthYear;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
        this.birthYear = (this.age == null) ? null : (Calendar.getInstance()
                .get(Calendar.YEAR) - this.age);
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
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

    public String getName() {
        return PinYinUtil.fullWidth2halfWidth(name);
    }

    public void setName(String name) {
        this.name = PinYinUtil.fullWidth2halfWidth(name);
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(Integer marriageStatus) {
        this.marriageStatus = marriageStatus;
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

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(Integer isSelf) {
        this.isSelf = isSelf;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Integer getIsOper() {
        return isOper;
    }

    public void setIsOper(Integer isOper) {
        this.isOper = isOper;
    }

    public Integer getIsRetire() {
        return isRetire;
    }

    public void setIsRetire(Integer isRetire) {
        this.isRetire = isRetire;
    }

    public String getAddAccountType() {
        return addAccountType;
    }

    public void setAddAccountType(String addAccountType) {
        this.addAccountType = addAccountType;
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

    public Date getRecentOrderDate() {
        return recentOrderDate;
    }

    public void setRecentOrderDate(Date recentOrderDate) {
        this.recentOrderDate = recentOrderDate;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccountRelation [managerId=");
        builder.append(managerId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", type=");
        builder.append(type);
        builder.append(", name=");
        builder.append(name);
        builder.append(", gender=");
        builder.append(gender);
        builder.append(", mobile=");
        builder.append(mobile);
        builder.append(", initialMobile=");
        builder.append(initialMobile);
        builder.append(", isStandardMobile=");
        builder.append(isStandardMobile);
        builder.append(", idCard=");
        builder.append(idCard);
        builder.append(", email=");
        builder.append(email);
        builder.append(", address=");
        builder.append(address);
        builder.append(", age=");
        builder.append(age);
        builder.append(", group=");
        builder.append(group);
        builder.append(", department=");
        builder.append(department);
        builder.append(", birthYear=");
        builder.append(birthYear);
        builder.append(", operator=");
        builder.append(operator);
        builder.append(", position=");
        builder.append(position);
        builder.append(", sheetName=");
        builder.append(sheetName);
        builder.append(", marriageStatus=");
        builder.append(marriageStatus);
        builder.append(", idType=");
        builder.append(idType);
        builder.append(", status=");
        builder.append(status);
        builder.append(", companyName=");
        builder.append(companyName);
        builder.append(", isSelf=");
        builder.append(isSelf);
        builder.append(", relationId=");
        builder.append(relationId);
        builder.append(", isOper=");
        builder.append(isOper);
        builder.append("]");
        return builder.toString();
    }

}