package com.tijiantest.model.payment.withdraw;
//用户提现申请

import java.util.Date;

import com.tijiantest.model.account.Account;

public class SaveWithdraw {
	private int id;
	// 提现用户真实姓名
	private String realName;
	// 提现金额，保存用户提交的原始金额
	private String amount;
	/**
	 * <pre>
	 * 提现渠道: 1 提现类型是银行 表示具体提现银行 2 提现类型是支付宝 固定值 支付宝
	 * 
	 */
	private String channelName;
	/**
	 * <pre>
	 * 收款帐号 1 提现类型是银行 表示银行卡号 2 提现类型是支付宝 表示支付宝帐号
	 */
	private String receivable;

	// 用户ID
	private Integer accountId;
	/**
	 * 状态
	 * 
	 * @see com.tijiantest.model.payment.WithdrawAuditStates;
	 */
	private Integer state;
	// 财务付款凭证
	private String serialNumber;
	/**
	 * 提现类型 支付宝 银行
	 * 
	 * @see com.tijiantest.model.payment.WithdrawTypeEnum;
	 */
	private Integer withdrawType;
	// 申请日期
	private Date createDate;
	// 更新日期
	private Date updateDate;

	// 操作者ID
	private String operator;
	// 备注
	private String remark;
	// 用户余额
	private Integer balance;
	private Account account;	

	public SaveWithdraw() {
		super();
	}

	public SaveWithdraw(String realName, String amount, String channelName, String receivable, Integer accountId,
			Integer state, Integer withdrawType, Date createDate, Integer id,Integer balance, Account account) {
		super();
		this.realName = realName;
		this.amount = amount;
		this.channelName = channelName;
		this.receivable = receivable;
		this.accountId = accountId;
		this.state = state;
		this.withdrawType = withdrawType;
		this.createDate = createDate;
		this.id = id;
		this.balance = balance;
		this.account = account;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getReceivable() {
		return receivable;
	}

	public void setReceivable(String receivable) {
		this.receivable = receivable;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Integer getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(Integer withdrawType) {
		this.withdrawType = withdrawType;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
