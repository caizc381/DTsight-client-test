package com.tijiantest.model.account;

public class ManagerSettings {
	private Integer mangerId;
	private Integer accountCompanyId;
	private Boolean isSitePay = false;
	private Boolean importWithoutIdCard= false;
	
	private Boolean orderImmediately = false;//直接预约
	private Boolean removeAllItems = false;//是否可以移除所有项目，包括必选项
	private Boolean keepLogin;//用户登陆后，长期保持登录状态
	/**
	 * 散客代预约
	 */
	private Boolean agentReserve;
	
	
	public Integer getMangerId() {
		return mangerId;
	}
	public void setMangerId(Integer mangerId) {
		this.mangerId = mangerId;
	}
	public Integer getAccountCompanyId() {
		return accountCompanyId;
	}
	public void setAccountCompanyId(Integer accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}
	public Boolean getIsSitePay() {
		return isSitePay;
	}
	public void setIsSitePay(Boolean isSitePay) {
		this.isSitePay = isSitePay;
	}
	public Boolean getImportWithoutIdCard() {
		return importWithoutIdCard;
	}
	public void setImportWithoutIdCard(Boolean importWithoutIdCard) {
		this.importWithoutIdCard = importWithoutIdCard;
	}
	public Boolean getOrderImmediately() {
		return orderImmediately;
	}
	public void setOrderImmediately(Boolean orderImmediately) {
		this.orderImmediately = orderImmediately;
	}
	public Boolean getRemoveAllItems() {
		return removeAllItems;
	}
	public void setRemoveAllItems(Boolean removeAllItems) {
		this.removeAllItems = removeAllItems;
	}
	public Boolean getKeepLogin() {
		return keepLogin;
	}
	public void setKeepLogin(Boolean keepLogin) {
		this.keepLogin = keepLogin;
	}
	public Boolean getAgentReserve() {
		return agentReserve;
	}
	public void setAgentReserve(Boolean agentReserve) {
		this.agentReserve = agentReserve;
	}
	
}
