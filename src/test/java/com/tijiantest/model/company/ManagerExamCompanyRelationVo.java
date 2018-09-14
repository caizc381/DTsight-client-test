package com.tijiantest.model.company;

public class ManagerExamCompanyRelationVo extends ManagerExamCompanyRelation{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6444553009693608721L;
	private String managerName;
	private String accountCompanyName;
	private String owner;
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getAccountCompanyName() {
		return accountCompanyName;
	}
	public void setAccountCompanyName(String accountCompanyName) {
		this.accountCompanyName = accountCompanyName;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
}
