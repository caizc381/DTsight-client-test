package com.tijiantest.model.paylog;

import java.io.Serializable;
import java.util.Date;

public class PaymentRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9125660359788376388L;

	private Integer id;
	/**
	 * 流水号
	 */
	private String serialNumer;
	/**
	 * 订单号
	 */
	private Integer orderId;
	/**
	 * 支付人accountId
	 */
	private Integer accountId;
	/**
	 * 支付方式
	 */
	private PaymentMethod paymentMethod;
	/**
	 * 支付状态 false：未支付 true：支付成功
	 */
	private boolean status;
	/**
	 * 金额
	 */
	private Integer amount;
	/**
	 * 支付时间
	 */
	private Date payTime;
	/**
	 * 交易类型 1: 订单支付 2：取消订单退款 3：体检完成退款 4：充值 5：提现
	 */
	private Integer tradeType;
	/**
	 * 是否主卡
	 */
	private boolean isPrimary;
	/**
	 * 支出账户
	 */
	private String expenseAccount;
	
	/**
	 * 第三方支付的交易号
	 */
	private String tradeNo;
	
	private String remark;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getExpenseAccount() {
		return expenseAccount;
	}

	public void setExpenseAccount(String expenseAccount) {
		this.expenseAccount = expenseAccount;
	}

	public String getSerialNumer() {
		return serialNumer;
	}

	public void setSerialNumer(String serialNumer) {
		this.serialNumer = serialNumer;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
