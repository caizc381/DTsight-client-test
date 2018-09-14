package com.tijiantest.model.account;

import java.io.Serializable;
import java.util.Date;

public class LoggedLog implements Serializable{

	private static final long serialVersionUID = -5468683928295883244L;

	private Integer id;
	
	private Integer accountId;
	
	private Integer successLoggedCount = 0;
	
	private Date successLoggedTime;

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

	public Integer getSuccessLoggedCount() {
		return successLoggedCount;
	}

	public void setSuccessLoggedCount(Integer successLoggedCount) {
		this.successLoggedCount = successLoggedCount;
	}

	public Date getSuccessLoggedTime() {
		return successLoggedTime;
	}

	public void setSuccessLoggedTime(Date successLoggedTime) {
		this.successLoggedTime = successLoggedTime;
	}
	
}
