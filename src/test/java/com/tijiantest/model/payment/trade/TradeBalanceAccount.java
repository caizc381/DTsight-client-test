package com.tijiantest.model.payment.trade;

import java.io.Serializable;
import java.util.Date;
/**
 * 用户余额账户
 * @author Administrator
 *
 */
public class TradeBalanceAccount implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	private Integer id;
	/**
	 * 主账户ID
	 */
	private Integer accountId;
	/**
	 * 用户余额
	 */
	private Long balance;
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 修改时间
	 */
	private Date gmtModified;
	/**
	 * 是否删除(0:未删除,1:已删除)
	 */
	private Integer isDeleted;
	/**
	 * 是否冻结(0:未冻结,1:已冻结)
	 */
	private Integer freeze;
	/**
	 * 总账户ID
	 */
	private Integer tradeAccountId;

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
	public Date getGmtCreated() {
		return gmtCreated;
	}
	public void setGmtCreated(Date gmtCreated) {
		this.gmtCreated = gmtCreated;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Integer getFreeze() {
		return freeze;
	}
	public void setFreeze(Integer freeze) {
		this.freeze = freeze;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Integer getTradeAccountId() {
		return tradeAccountId;
	}
	public void setTradeAccountId(Integer tradeAccountId) {
		this.tradeAccountId = tradeAccountId;
	}
	
}
