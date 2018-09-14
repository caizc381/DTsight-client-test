package com.tijiantest.model.payment;

import java.util.List;

public class SearchDto {
	private String username;
	private String idCard;
	private String mobile;
	private String amount;
	private String serialNumber;
	
	//收款人账号
	private String receivable;
	
	private List<Integer> state;
	//审核类型：财务审核，客户审核
	private Integer auditType;	
	
	public SearchDto(String username, String idCard, String mobile, String amount, String serialNumber,
			String receivable, List<Integer> state, Integer auditType) {
		super();
		this.username = username;
		this.idCard = idCard;
		this.mobile = mobile;
		this.amount = amount;
		this.serialNumber = serialNumber;
		this.receivable = receivable;
		this.state = state;
		this.auditType = auditType;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getReceivable() {
		return receivable;
	}
	public void setReceivable(String receivable) {
		this.receivable = receivable;
	}
	public List<Integer> getState() {
		return state;
	}
	public void setState(List<Integer> state) {
		this.state = state;
	}
	public Integer getAuditType() {
		return auditType;
	}
	public void setAuditType(Integer auditType) {
		this.auditType = auditType;
	}
	
	
}
