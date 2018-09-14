package com.tijiantest.model.payment.trade;

import java.io.Serializable;
import java.util.Date;

public class TradePayRecord implements Serializable {
	
	private static final long serialVersionUID = -645928938088895523L;

	/**
	 * 主键ID
	 */
	private Integer id;
	
	/**
	 * 交易流水号
	 */
	private String sn;
	/**
	 * 交易系统订单号
	 * 注意区分体检订单号、交易系统订单号、和sn的区别
	 * 一个交易系统订单号可以有多个sn(支付流水号)
	 */
	private String tradeOrderNum;

	/**
	 * 业务系统的订单号(目前仅有体检订单)
	 */
	private String refOrderNum;
	/**
	 * 订单变更标志
	 * refOrderNum + refOrderNumVersion 要求唯一
	 */
	private String refOrderNumVersion;
	
	/**
	 * 业务系统订单类型 目前固定值1 表示体检订单
	 */
	private Integer refOrderType;
	
	/**
	 * 支付配置表ID
	 */
	private Integer tradeMethodConfigId;
	/**
	 * 支付类型
	 * 1=卡 2=余额 3=支付宝 4=微信 5=支付宝扫码 6=微信扫码 7=线下支付 8=线上支付 9=母卡支付 10=平台优惠券支付 11=医院优惠券支付 12=渠道优惠券支付 13=微信小程序';
	 * @see PayConstants.PayMethod
	 */
	private Integer tradeMethodType;
	
	/**
	 * 支付状态
	 * @see PayConstants.TradeStatus
	 */
	private Integer payStatus;
	private Long payAmount;
	
	private Integer payTradeSubaccountType;
	private Integer payTradeAccountId;
	private Integer payTradeSubaccountId;
	private String  payTradeAccountSnap;
	
	
	private Integer receiveTradeSubaccountType;
	private Integer receiveTradeAccountId;
	private Integer receiveTradeSubaccountId;
	private String  receiveTradeAccountSnap;
	

	/**
	 * 外部交易账户
	 * 比如支付宝 填入支付宝账号
	 * 微信填写微信openid
	 */
	private String outTradeAccount;
	/**
	 * 外部交易订单号
	 * 外部交易订单号是指mytijian作为第三方支付的商户，
	 * 向第三方支付发起支付请求的时候，声明的mytijian平台本次交易的订单号，目前使用支付记录的sn
	 * 外部订单号任何时候都要求唯一
	 */
	private String outOrderId;
	
	/**
	 * 交易凭据
	 * 在使用第三方支付的时候，用户支付成功后，
	 * 第三方会返回一个对方生成的唯一交易编号，
	 * 通过这条唯一编号可以确认一比支付的最终结果
	 */
	private String credentials;
	
	
	private String remark;
	
	private Date gmtCallback;
	private Date gmtCreated;
	private Date gmtModified;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getTradeOrderNum() {
		return tradeOrderNum;
	}
	public void setTradeOrderNum(String tradeOrderNum) {
		this.tradeOrderNum = tradeOrderNum;
	}
	public String getRefOrderNum() {
		return refOrderNum;
	}
	public void setRefOrderNum(String refOrderNum) {
		this.refOrderNum = refOrderNum;
	}
	public String getRefOrderNumVersion() {
		return refOrderNumVersion;
	}
	public void setRefOrderNumVersion(String refOrderNumVersion) {
		this.refOrderNumVersion = refOrderNumVersion;
	}
	public Integer getRefOrderType() {
		return refOrderType;
	}
	public void setRefOrderType(Integer refOrderType) {
		this.refOrderType = refOrderType;
	}
	public Integer getTradeMethodConfigId() {
		return tradeMethodConfigId;
	}
	public void setTradeMethodConfigId(Integer tradeMethodConfigId) {
		this.tradeMethodConfigId = tradeMethodConfigId;
	}
	public Integer getTradeMethodType() {
		return tradeMethodType;
	}
	public void setTradeMethodType(Integer tradeMethodType) {
		this.tradeMethodType = tradeMethodType;
	}
	public Integer getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	public Long getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Long payAmount) {
		this.payAmount = payAmount;
	}
	public Integer getPayTradeSubaccountType() {
		return payTradeSubaccountType;
	}
	public void setPayTradeSubaccountType(Integer payTradeSubaccountType) {
		this.payTradeSubaccountType = payTradeSubaccountType;
	}
	public Integer getPayTradeAccountId() {
		return payTradeAccountId;
	}
	public void setPayTradeAccountId(Integer payTradeAccountId) {
		this.payTradeAccountId = payTradeAccountId;
	}
	public Integer getPayTradeSubaccountId() {
		return payTradeSubaccountId;
	}
	public void setPayTradeSubaccountId(Integer payTradeSubaccountId) {
		this.payTradeSubaccountId = payTradeSubaccountId;
	}
	public String getPayTradeAccountSnap() {
		return payTradeAccountSnap;
	}
	public void setPayTradeAccountSnap(String payTradeAccountSnap) {
		this.payTradeAccountSnap = payTradeAccountSnap;
	}

	
	public Integer getReceiveTradeSubaccountType() {
		return receiveTradeSubaccountType;
	}
	public void setReceiveTradeSubaccountType(Integer receiveTradeSubaccountType) {
		this.receiveTradeSubaccountType = receiveTradeSubaccountType;
	}
	public Integer getReceiveTradeAccountId() {
		return receiveTradeAccountId;
	}
	public void setReceiveTradeAccountId(Integer receiveTradeAccountId) {
		this.receiveTradeAccountId = receiveTradeAccountId;
	}
	public Integer getReceiveTradeSubaccountId() {
		return receiveTradeSubaccountId;
	}
	public void setReceiveTradeSubaccountId(Integer receiveTradeSubaccountId) {
		this.receiveTradeSubaccountId = receiveTradeSubaccountId;
	}
	public String getReceiveTradeAccountSnap() {
		return receiveTradeAccountSnap;
	}
	public void setReceiveTradeAccountSnap(String receiveTradeAccountSnap) {
		this.receiveTradeAccountSnap = receiveTradeAccountSnap;
	}
	public String getOutOrderId() {
		return outOrderId;
	}
	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}
	public String getCredentials() {
		return credentials;
	}
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getGmtCallback() {
		return gmtCallback;
	}
	public void setGmtCallback(Date gmtCallback) {
		this.gmtCallback = gmtCallback;
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
	public String getOutTradeAccount() {
		return outTradeAccount;
	}
	public void setOutTradeAccount(String outTradeAccount) {
		this.outTradeAccount = outTradeAccount;
	}
}
