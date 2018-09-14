package com.tijiantest.model.payment.trade;

import java.io.Serializable;
/**
 * 子账户流水表
 * @author Administrator
 *
 */
import java.util.Date;
public class TradeAccountDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 子账户流水编号
	 */
	private String sn;
	/**
	 * 交易子账户类型(1:客户经理余额账户,2:用户体检卡账户,3:用户支付宝账户,4:用户微信支付账户,5:用户余额账户,6:用户现场支付账户)
	 */
	private Integer tradeSubAccountType;
	/**
	 * 交易子账户ID
	 */
	private Integer tradeSubAccountId;
	/**
	 * 总账户ID
	 */
	private Integer tradeAccountId;
	/**
	 * 交易批次号
	 */
	private String tradeOrderNum;
	/**
	 * 创建时间
	 */
	private Date gmtCreated;
	/**
	 * 修改时间
	 */
	private Date gmtModified;
	/**
	 * 业务类型(1:支付,2:退款,3:充值,4:提现)
	 */
	private Integer bizType;
	/**
	 * 关联流水号
	 */
	private String refBizSn;
	/**
	 * 变动金额
	 */
	private Long changeAmount;
	/**
	 *变动前账户金额 
	 */
	private Long preAmount;
	/**
	 * 变动后账户金额
	 */
	private Long aftAmount;
	/**
	 * 账户变动方向(0:入账,1:出账)
	 */
	private Integer flag;
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getTradeSubAccountType() {
		return tradeSubAccountType;
	}
	public void setTradeSubAccountType(Integer tradeSubAccountType) {
		this.tradeSubAccountType = tradeSubAccountType;
	}
	public Integer getTradeSubAccountId() {
		return tradeSubAccountId;
	}
	public void setTradeSubAccountId(Integer tradeSubAccountId) {
		this.tradeSubAccountId = tradeSubAccountId;
	}
	public Integer getTradeAccountId() {
		return tradeAccountId;
	}
	public void setTradeAccountId(Integer tradeAccountId) {
		this.tradeAccountId = tradeAccountId;
	}
	public String getTradeOrderNum() {
		return tradeOrderNum;
	}
	public void setTradeOrderNum(String tradeOrderNum) {
		this.tradeOrderNum = tradeOrderNum;
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
	public Integer getBizType() {
		return bizType;
	}
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	public String getRefBizSn() {
		return refBizSn;
	}
	public void setRefBizSn(String refBizSn) {
		this.refBizSn = refBizSn;
	}
	public Long getChangeAmount() {
		return changeAmount;
	}
	public void setChangeAmount(Long changeAmount) {
		this.changeAmount = changeAmount;
	}
	public Long getPreAmount() {
		return preAmount;
	}
	public void setPreAmount(Long preAmount) {
		this.preAmount = preAmount;
	}
	public Long getAftAmount() {
		return aftAmount;
	}
	public void setAftAmount(Long aftAmount) {
		this.aftAmount = aftAmount;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
}
