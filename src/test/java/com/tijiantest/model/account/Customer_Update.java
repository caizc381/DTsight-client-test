package com.tijiantest.model.account;
/**
 * 
 * @author ChenSijia
 *
 */
public class Customer_Update {
	private int managerId;
	private int customerId;
	private int type;
	private String name;
	private int gender;
	private String idCard;
	private int companyId;
	public Customer_Update(int managerId,int customerId,int type,String name,int gender,String idCard,int companyId){
		this.managerId = managerId;
		this.customerId=customerId;
		this.type=type;
		this.name = name;
		this.gender = gender;
		this.idCard = idCard;
		this.companyId = companyId;
	}
	public int getManagerId() {
		return managerId;
	}
	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
}
