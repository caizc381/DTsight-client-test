package com.tijiantest.model.card;

public class CardPreviewVo {
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 性别
	 */
	private Integer gender;
	
	/**
	 * 年龄
	 */
	private Integer age;
	
	/**
	 * 婚姻状态
	 */
	private Integer marriageStatus;
	
	/**
	 * 联系方式
	 */
	private String mobile;
	
	/**
	 * 身份证号
	 */
	private String idCard;
	
	/**
	 * 单位名称
	 */
	private String companyName;
	
	/**
	 * 部门
	 */
	private String department;
	
	/**
	 * 体检须知
	 */
	private String examNote;
	
	/**
	 * 登录账号
	 */
	private String loginName;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 体检地址
	 */
	private String examAddress;
	
	/**
	 * 体检中心二维码
	 */
	private String hospitalQRCode;
	
	/**
	 * 身份证二维码
	 */
	private byte[] idCardQRCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getMarriageStatus() {
		return marriageStatus;
	}

	public void setMarriageStatus(Integer marriageStatus) {
		this.marriageStatus = marriageStatus;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getExamNote() {
		return examNote;
	}

	public void setExamNote(String examNote) {
		this.examNote = examNote;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getExamAddress() {
		return examAddress;
	}

	public void setExamAddress(String examAddress) {
		this.examAddress = examAddress;
	}

	public String getHospitalQRCode() {
		return hospitalQRCode;
	}

	public void setHospitalQRCode(String hospitalQRCode) {
		this.hospitalQRCode = hospitalQRCode;
	}

	public byte[] getIdCardQRCode() {
		return idCardQRCode;
	}

	public void setIdCardQRCode(byte[] idCardQRCode) {
		this.idCardQRCode = idCardQRCode;
	}
	
	
}
