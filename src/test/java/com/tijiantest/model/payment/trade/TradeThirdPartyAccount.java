package com.tijiantest.model.payment.trade;

import java.io.Serializable;
import java.util.Date;
/**
 * 用户第三方支付账户
 * @author Administrator
 *
 */
public class TradeThirdPartyAccount implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	private Integer id;
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 修改时间
	 */
	private Date gmtModified;
	/**
	 * 是否删除
	 */
	private Integer isDeleted;
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
	public Integer getTradeAccountId() {
		return tradeAccountId;
	}
	public void setTradeAccountId(Integer tradeAccountId) {
		this.tradeAccountId = tradeAccountId;
	}
	
}
