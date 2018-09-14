package com.tijiantest.model.company;

public class CompanyManagerVo {
	private Integer id;
	private String managerName;
	private boolean asAccountCompany;
	private String owner;
	private boolean selected;
	private String acctCompanyName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public boolean isAsAccountCompany() {
		return asAccountCompany;
	}

	public void setAsAccountCompany(boolean asAccountCompany) {
		this.asAccountCompany = asAccountCompany;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getAcctCompanyName() {
		return acctCompanyName;
	}

	public void setAcctCompanyName(String acctCompanyName) {
		this.acctCompanyName = acctCompanyName;
	}

}
