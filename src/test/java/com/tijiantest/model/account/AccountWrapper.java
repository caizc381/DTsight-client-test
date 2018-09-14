package com.tijiantest.model.account;

import java.io.Serializable;

public class AccountWrapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1762608476570505899L;

	private Account account;
	private AccountInfo accountInfo;
	
	public AccountWrapper() {
	}

	public AccountWrapper(Account account, AccountInfo accountInfo) {
		super();
		this.account = account;
		this.accountInfo = accountInfo;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}

}
