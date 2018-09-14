package com.tijiantest.model.order;

import java.io.Serializable;

public class OrderExtInfoSnapshot implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 130841554004882507L;
	/**
	 * 是否是家属订单
	 */
	private String isFamily;
	/**
	 * 订单调整金额
	 */
	private Integer adjustPrice;
	/**
	 * 是否是vip
	 */
	private String vip;
	/**
	 * 自付金额
	 */
	private int selfMoney;
	/**
	 *下单人
	 */
	private String operator;
	/**
	 * 下单人手机
	 */
	private String operatorMobile;
	/**
	 * 是否需要纸质报告
	 */
	private Boolean needPaperReport;	
	/**
	 * 订单单位显示名称
	 */
	private String examCompany;
	/**
	 * 挂账客户经理,兼容老业务,未来会废弃
	 */
	private String accountManager;
	/**
	 * 客户经理,兼容老业务,未来会废弃
	 */
	private String manager;
	
	public String getIsFamily() {
		return isFamily;
	}
	public void setIsFamily(String isFamily) {
		this.isFamily = isFamily;
	}
	public Integer getAdjustPrice() {
		return adjustPrice;
	}
	public void setAdjustPrice(Integer adjustPrice) {
		this.adjustPrice = adjustPrice;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public int getSelfMoney() {
		return selfMoney;
	}
	public void setSelfMoney(int selfMoney) {
		this.selfMoney = selfMoney;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperatorMobile() {
		return operatorMobile;
	}
	public void setOperatorMobile(String operatorMobile) {
		this.operatorMobile = operatorMobile;
	}
	public Boolean getNeedPaperReport() {
		return needPaperReport;
	}
	public void setNeedPaperReport(Boolean needPaperReport) {
		this.needPaperReport = needPaperReport;
	}
	public String getExamCompany() {
		return examCompany;
	}
	public void setExamCompany(String examCompany) {
		this.examCompany = examCompany;
	}
	public String getAccountManager() {
		return accountManager;
	}
	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	
}
