package com.tijiantest.model.payment.trade;

public class RefundAmount {

	/**
	 * 母卡退款金额
	 */
	private long pcardRefundAmount;
	
	/**
	 * 线上退款金额
	 */
	private long onlineRefundAmount;
	/**
	 * 平台退款金额
	 */
	private long platformRefundAmount;
	/**
	 * 用户体检卡退款金额
	 */
	private long cardRefundAmount;
	
	/**
	 * 线下退款金额
	 */
	private long offlineRefundAmount;

	/**
	 * 医院优惠券支付
	 */
	private long hospitalCouponRefundAmount;

	/**
	 * 平台优惠券退款
	 */
	private long platformCouponRefundAmount;
	/**
	 * 渠道优惠券退款
	 */
	private long channelCouponRefundAmount;

	/**
	 * 医院线上退款
	 */
	private long hospitalOnlineRefundAmount;

	/**
	 * 平台线上退款
	 */
	private long platformOnlineRefundAmount;

	/**
	 * 渠道线上退款
	 */
	private long channelOnlineRefundAmount;

	/**
	 * 渠道单位退款
	 */
	private long channelCompanyRefundAmount;

	/**
	 * 渠道体检卡退款
	 */
	private long channelCardRefundAmount;
	/**
	 * 成功退款金额
	 */
	private long totalSuccessRefundAmount;

	public long getPcardRefundAmount() {
		return pcardRefundAmount;
	}

	public void setPcardRefundAmount(long pcardRefundAmount) {
		this.pcardRefundAmount = pcardRefundAmount;
	}

	public long getOnlineRefundAmount() {
		return onlineRefundAmount;
	}

	public void setOnlineRefundAmount(long onlineRefundAmount) {
		this.onlineRefundAmount = onlineRefundAmount;
	}

	public long getPlatformRefundAmount() {
		return platformRefundAmount;
	}

	public void setPlatformRefundAmount(long platformRefundAmount) {
		this.platformRefundAmount = platformRefundAmount;
	}

	public long getCardRefundAmount() {
		return cardRefundAmount;
	}

	public void setCardRefundAmount(long cardRefundAmount) {
		this.cardRefundAmount = cardRefundAmount;
	}

	public long getOfflineRefundAmount() {
		return offlineRefundAmount;
	}

	public void setOfflineRefundAmount(long offlineRefundAmount) {
		this.offlineRefundAmount = offlineRefundAmount;
	}

	public long getHospitalCouponRefundAmount() {
		return hospitalCouponRefundAmount;
	}

	public void setHospitalCouponRefundAmount(long hospitalCouponRefundAmount) {
		this.hospitalCouponRefundAmount = hospitalCouponRefundAmount;
	}

	public long getPlatformCouponRefundAmount() {
		return platformCouponRefundAmount;
	}

	public void setPlatformCouponRefundAmount(long platformCouponRefundAmount) {
		this.platformCouponRefundAmount = platformCouponRefundAmount;
	}

	public long getChannelCouponRefundAmount() {
		return channelCouponRefundAmount;
	}

	public void setChannelCouponRefundAmount(long channelCouponRefundAmount) {
		this.channelCouponRefundAmount = channelCouponRefundAmount;
	}

	public long getHospitalOnlineRefundAmount() {
		return hospitalOnlineRefundAmount;
	}

	public void setHospitalOnlineRefundAmount(long hospitalOnlineRefundAmount) {
		this.hospitalOnlineRefundAmount = hospitalOnlineRefundAmount;
	}

	public long getPlatformOnlineRefundAmount() {
		return platformOnlineRefundAmount;
	}

	public void setPlatformOnlineRefundAmount(long platformOnlineRefundAmount) {
		this.platformOnlineRefundAmount = platformOnlineRefundAmount;
	}

	public long getChannelOnlineRefundAmount() {
		return channelOnlineRefundAmount;
	}

	public void setChannelOnlineRefundAmount(long channelOnlineRefundAmount) {
		this.channelOnlineRefundAmount = channelOnlineRefundAmount;
	}

	public long getChannelCompanyRefundAmount() {
		return channelCompanyRefundAmount;
	}

	public void setChannelCompanyRefundAmount(long channelCompanyRefundAmount) {
		this.channelCompanyRefundAmount = channelCompanyRefundAmount;
	}

	public long getChannelCardRefundAmount() {
		return channelCardRefundAmount;
	}

	public void setChannelCardRefundAmount(long channelCardRefundAmount) {
		this.channelCardRefundAmount = channelCardRefundAmount;
	}

	public long getTotalSuccessRefundAmount() {
		return totalSuccessRefundAmount;
	}

	public void setTotalSuccessRefundAmount(long totalSuccessRefundAmount) {
		this.totalSuccessRefundAmount = totalSuccessRefundAmount;
	}
}
