package com.tijiantest.model.order;

import java.io.Serializable;

public class SettleInfoSnapshot implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1245408975720675192L;
	/**
	 * 折扣
	 */
	private Double discount;
	/**
	 * 结算批次号
	 */
	private String settleBatch;
	/**
	 * 结算标记
	 */
	private Integer settleSign;
	/**
	 * 自付金额
	 */
	private Integer selfMoney;
	
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public String getSettleBatch() {
		return settleBatch;
	}
	public void setSettleBatch(String settleBatch) {
		this.settleBatch = settleBatch;
	}
	public Integer getSettleSign() {
		return settleSign;
	}
	public void setSettleSign(Integer settleSign) {
		this.settleSign = settleSign;
	}
	public Integer getSelfMoney() {
		return selfMoney;
	}
	public void setSelfMoney(Integer selfMoney) {
		this.selfMoney = selfMoney;
	}
	
}
