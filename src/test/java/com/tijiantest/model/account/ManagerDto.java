package com.tijiantest.model.account;

import java.util.List;
import com.tijiantest.model.company.AccountCompany;

public class ManagerDto {
	private Account account;
	private List<User> userList;
	private AccountCompany accountCompany;
	private Long balance;
	private ManagerSettings managerSettings;
	private Role role;//客户经理角色
	private String identity;//专属网址
	private String hospitalName;//渠道商
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public AccountCompany getAccountCompany() {
		return accountCompany;
	}
	public void setAccountCompany(AccountCompany accountCompany) {
		this.accountCompany = accountCompany;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public ManagerSettings getManagerSettings() {
		return managerSettings;
	}
	public void setManagerSettings(ManagerSettings managerSettings) {
		this.managerSettings = managerSettings;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
	
}
