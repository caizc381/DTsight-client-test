package com.tijiantest.model.paylog;

import java.util.Date;

import com.tijiantest.model.paylog.PayLogThreadInfo;

/**
 * 支付流水
 * @author linzhihao
 */
public class PayLog implements PayLogThreadInfo {
	
	private int id;
	
	// === 交易信息 ===
	private Integer tradeBodyType; // 1. 账户余额， 2. 卡
	private Integer tradeBody; // 交易主体
	private String tradeBatchNo; // 交易批次号
	private Date gmtTradeCreated; //交易创建时间
	private Date gmtTradeReach; // 交易达成时间 (包括success, fail)
	private Date gmtModified; // 最后 update 时间
	private Integer tradeType; // 交易类型
	private long amount; // 交易金额
	private long surplus; // 交易后账户金额
	private Integer channel; // 交易渠道 
	private String expenseAccount; // 微信号、支付宝号、银行卡号。
	private Integer tradeIndex; // 交易序号
	
	// === 状态信息 ===
	private int status; // 交易状态 created reached failed canceled
	
	// === 操作人信息 ===
	private Integer operaterType; // CRM、PC、定时任务、对接
	private Integer operater;

	// === 交易凭证 ===
	private String credentialsType;// 凭据类型 无、支付宝、微信、银行回单、发票、收据
	private String credentials; // 凭据 支付宝、微信单号、银行回单号
	
	// === 订单信息 ===
	private String orderNum; // 订单号
	private Integer orderId; // 订单号
	
	private String remark; // 备注
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getTradeBodyType() {
		return tradeBodyType;
	}

	public void setTradeBodyType(Integer tradeBodyType) {
		this.tradeBodyType = tradeBodyType;
	}

	public Integer getTradeBody() {
		return tradeBody;
	}

	public void setTradeBody(Integer tradeBody) {
		this.tradeBody = tradeBody;
	}

	public String getTradeBatchNo() {
		return tradeBatchNo;
	}

	public void setTradeBatchNo(String tradeBatchNo) {
		this.tradeBatchNo = tradeBatchNo;
	}

	public Date getGmtTradeCreated() {
		return gmtTradeCreated;
	}

	public void setGmtTradeCreated(Date gmtTradeCreated) {
		this.gmtTradeCreated = gmtTradeCreated;
	}

	public Date getGmtTradeReach() {
		return gmtTradeReach;
	}

	public void setGmtTradeReach(Date gmtTradeReach) {
		this.gmtTradeReach = gmtTradeReach;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Integer getTradeType() {
		return tradeType;
	}

	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getSurplus() {
		return surplus;
	}

	public void setSurplus(long surplus) {
		this.surplus = surplus;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getExpenseAccount() {
		return expenseAccount;
	}

	public void setExpenseAccount(String expenseAccount) {
		this.expenseAccount = expenseAccount;
	}

	public Integer getTradeIndex() {
		return tradeIndex;
	}

	public void setTradeIndex(Integer tradeIndex) {
		this.tradeIndex = tradeIndex;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getOperaterType() {
		return operaterType;
	}

	public void setOperaterType(Integer operaterType) {
		this.operaterType = operaterType;
	}

	public Integer getOperater() {
		return operater;
	}

	public void setOperater(Integer operater) {
		this.operater = operater;
	}

	public String getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}



