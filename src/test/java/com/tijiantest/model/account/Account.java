package com.tijiantest.model.account;

public class Account {

	private Integer id ;
	private String name;
	private String mobile;
	private String idCard;
	
	private String employeeId;//员工号
	private Integer status; //0:可用；-1：不可用；1 ： 异常
	private Integer type;//账户类型：1-mytijian注册生成;2-crm生成；3-体检人；4-演示账户；5-客户经理
	private Integer idType;//证件类型
	private Integer system;
	
	
	public Account() {
		super();
	}
	
	public Account(int id, String mobile, String idCard, String employeeId, Integer status) {
		super();
		this.id = id;
		this.mobile = mobile;
		this.idCard = idCard;
		this.employeeId = employeeId;
		this.status = status;
	}

	public Account(String name, String mobile, Integer status) {
		super();
		this.name = name;
		this.mobile = mobile;
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
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
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIdType() {
		return idType;
	}
	public void setIdType(Integer idType) {
		this.idType = idType;
	}
	public Integer getSystem() {
		return system;
	}
	public void setSystem(Integer system) {
		this.system = system;
	}


}
