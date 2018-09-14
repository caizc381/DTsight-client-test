package com.tijiantest.model.payment.trade;

import java.util.Date;

public class TradeOrder {
	
	private Integer id;

	private String tradeOrderNum;

	private String refOrderNum;
	private String refOrderNumVersion;
	private Integer refOrderType;
	private String refOrderGoodsName;
	private String refOrderGoodsDesc;
	private String refOrderBizParam;

	private Integer tradeType;
	private String tradeRemark;
	private String extraCommonParam;

	private Long amount;
	private Long succAmount;

	private Integer payMethodType;

	private Integer tradeStatus;

	private Integer hospitalCouponId;

	private Integer platformCouponId;

	private Date gmtCreated;
	private Date gmtModified;
	
	
	public String getExtraCommonParam() {
		return extraCommonParam;
	}
	public void setExtraCommonParam(String extraCommonParam) {
		this.extraCommonParam = extraCommonParam;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getRefOrderGoodsName() {
		return refOrderGoodsName;
	}
	public void setRefOrderGoodsName(String refOrderGoodsName) {
		this.refOrderGoodsName = refOrderGoodsName;
	}
	public String getRefOrderGoodsDesc() {
		return refOrderGoodsDesc;
	}
	public void setRefOrderGoodsDesc(String refOrderGoodsDesc) {
		this.refOrderGoodsDesc = refOrderGoodsDesc;
	}
	public String getRefOrderBizParam() {
		return refOrderBizParam;
	}
	public void setRefOrderBizParam(String refOrderBizParam) {
		this.refOrderBizParam = refOrderBizParam;
	}
	public Integer getTradeType() {
		return tradeType;
	}
	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeRemark() {
		return tradeRemark;
	}
	public void setTradeRemark(String tradeRemark) {
		this.tradeRemark = tradeRemark;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getSuccAmount() {
		return succAmount;
	}
	public void setSuccAmount(Long succAmount) {
		this.succAmount = succAmount;
	}
	public Integer getPayMethodType() {
		return payMethodType;
	}
	public void setPayMethodType(Integer payMethodType) {
		this.payMethodType = payMethodType;
	}
	public Integer getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(Integer tradeStatus) {
		this.tradeStatus = tradeStatus;
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
	public Integer getHospitalCouponId() {
		return hospitalCouponId;
	}

	public void setHospitalCouponId(Integer hospitalCouponId) {
		this.hospitalCouponId = hospitalCouponId;
	}

	public Integer getPlatformCouponId() {
		return platformCouponId;
	}

	public void setPlatformCouponId(Integer platformCouponId) {
		this.platformCouponId = platformCouponId;
	}
}
