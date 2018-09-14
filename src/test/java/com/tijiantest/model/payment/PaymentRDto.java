package com.tijiantest.model.payment;

import java.util.Date;

public class PaymentRDto {
	private String orderNum;
	private Date date;
	private String payType;
	private Integer useBlance;
	private Integer status;
	private String tradeNo;

	// private List<WxBill> wxbill;
	
	private Integer tradeType;
	
	public String getOrderId() {
		return orderNum;
	}

	public void setOrderId(String orderNum) {
		this.orderNum = orderNum;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getUseBlance() {
		return useBlance;
	}

	public void setUseBlance(Integer useBlance) {
		this.useBlance = useBlance;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
	
}
