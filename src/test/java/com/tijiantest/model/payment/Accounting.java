package com.tijiantest.model.payment;

import java.io.Serializable;

public class Accounting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4647501133756284687L;

	private Integer id;
	private Integer accountId;
	private Integer balance;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

}
