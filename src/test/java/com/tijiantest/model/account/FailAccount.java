package com.tijiantest.model.account;

import java.util.Date;

public class FailAccount {
	//用户创建时间
	private Date createTime;
	//account_id
	private int id;
	//客户经理id
	private int managerId;
	
	private int hospitalId;
	
	private int companyId;
	
	private String name;
	
	private String employeeId;
	
	private String age;
	
	private String gender;
	
	private String marriageStatus;
	//所在组
	private String group;
	//部门
	private String department;
	//来源表
	private String sheetName;
	
	private String mobile;
	
	private String idCard;
	
	private String position;
	
	private String retire;
	
	private String failReason;
	//导入员
	private String operator;
	//在导入文件中顺序
	private int sequence;
	//证件类型
	private int idType = IdTypeEnum.IDCARD.getCode();;
	//联系人姓名
	private String contactName;
	//联系人电话号码
	private String contactTel;
	
	private boolean empty = true;	
	
	public FailAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FailAccount(int id, int companyId, String name, String employeeId, String age, String gender,
			String marriageStatus, String group, String department, String sheetName, String mobile, String idCard,
			String position, String retire, String failReason, String operator, int sequence, int idType,
			boolean empty,Date createTime) {
		super();
		this.id = id;
		this.companyId = companyId;
		this.name = name;
		this.employeeId = employeeId;
		this.age = age;
		this.gender = gender;
		this.marriageStatus = marriageStatus;
		this.group = group;
		this.department = department;
		this.sheetName = sheetName;
		this.mobile = mobile;
		this.idCard = idCard;
		this.position = position;
		this.retire = retire;
		this.failReason = failReason;
		this.operator = operator;
		this.sequence = sequence;
		this.idType = idType;
		this.empty = empty;
		this.createTime = createTime;
	}

	public FailAccount( Date createTime,int companyId,int managerId,String name,int sequence,String gender,String age,String idCard,String employeeId){
		super();
		this.createTime = createTime;
		this.companyId = companyId;
		this.managerId = managerId;
		this.name = name;
		this.sequence = sequence;
		this.gender = gender;
		this.age = age;
		this.idCard = idCard;
		this.employeeId = employeeId;
	}
	
	public FailAccount(int companyId,String name,String idCard,String mobile,String gender){
		super();
		this.companyId = companyId;
		this.name = name;
		this.idCard = idCard;
		this.mobile = mobile;
		this.gender = gender;
	}
	
	public FailAccount(int companyId,int sequence,int managerId){
		super();
		this.companyId = companyId;
		this.sequence = sequence;
		this.managerId = managerId;
	}
	
	public FailAccount(int hospitalId,int companyId,String contactName,String contactTel){
		super();
		this.hospitalId = hospitalId;
		this.companyId = companyId;
		this.contactName = contactName;
		this.contactTel = contactTel;
	}
	
	public int getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getManagerId() {
		return managerId;
	}


	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}


	public int getCompanyId() {
		return companyId;
	}


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
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


	public String getFailReason() {
		return failReason;
	}


	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}	
	
}