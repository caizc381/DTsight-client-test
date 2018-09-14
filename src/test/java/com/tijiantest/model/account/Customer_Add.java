package com.tijiantest.model.account;
/**
 * 
 * @author ChenSijia
 *
 */
public class Customer_Add {
	private String group;
	private String name;
	private int gender;
	private String idCard;
	private int companyId;
	public Customer_Add(String group,String name,int gender,String idCard,int companyId){
		this.group = group;
		this.name = name;
		this.gender = gender;
		this.idCard = idCard;
		this.companyId = companyId;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
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
