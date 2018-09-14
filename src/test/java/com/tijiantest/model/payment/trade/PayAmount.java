package com.tijiantest.model.payment.trade;

public class PayAmount {

	/**
	 * 母卡金额
	 */
	private long pcardPayAmount;
	
	/**
	 * 线上金额
	 */
	private long onlinePayAmount;
	/**
	 * 平台金额
	 */
	private long platformPayAmount;
	/**
	 * 用户体检卡金额
	 */
	private long cardPayAmount;
	
	/**
	 * 线下支付金额
	 */
	private long offlinePayAmount;

	/**
	 * 医院优惠券支付
	 */
	private long hospitalCouponAmount;

	/**
	 * 平台优惠券支付
	 */
	private long platformCouponAmount;
	/**
	 * 渠道优惠券支付
	 */
	private long channelCouponAmount;

	/**
	 * 医院线上支付
	 */
	private long hospitalOnlinePayAmount;

	/**
	 * 平台线上支付
	 */
	private long platformOnlinePayAmount;

	/**
	 * 渠道线上支付
	 */
	private long channelOnlinePayAmount;

	/**
	 * 渠道单位支付
	 */
	private long channelCompanyPayAmount;

	/**
	 * 渠道体检卡支付
	 */
	private long channelCardPayAmount;


	/**
	 * 实际优惠金额
	 */
	private long totalCouponPayAmount;

	/**
	 * 实际支付金额
	 */
	private long totalSuccPayAmount;

	public long getPcardPayAmount() {
		return pcardPayAmount;
	}

	public void setPcardPayAmount(long pcardPayAmount) {
		this.pcardPayAmount = pcardPayAmount;
	}

	public long getOnlinePayAmount() {
		return onlinePayAmount;
	}

	public void setOnlinePayAmount(long onlinePayAmount) {
		this.onlinePayAmount = onlinePayAmount;
	}

	public long getPlatformPayAmount() {
		return platformPayAmount;
	}

	public void setPlatformPayAmount(long platformPayAmount) {
		this.platformPayAmount = platformPayAmount;
	}

	public long getCardPayAmount() {
		return cardPayAmount;
	}

	public void setCardPayAmount(long cardPayAmount) {
		this.cardPayAmount = cardPayAmount;
	}

	public long getOfflinePayAmount() {
		return offlinePayAmount;
	}

	public void setOfflinePayAmount(long offlinePayAmount) {
		this.offlinePayAmount = offlinePayAmount;
	}

	public long getTotalSuccPayAmount() {
		return totalSuccPayAmount;
	}

	public void setTotalSuccPayAmount(long totalSuccPayAmount) {
		this.totalSuccPayAmount = totalSuccPayAmount;
	}

	public long getHospitalCouponAmount() {
		return hospitalCouponAmount;
	}

	public void setHospitalCouponAmount(long hospitalCouponAmount) {
		this.hospitalCouponAmount = hospitalCouponAmount;
	}

	public long getPlatformCouponAmount() {
		return platformCouponAmount;
	}

	public void setPlatformCouponAmount(long platformCouponAmount) {
		this.platformCouponAmount = platformCouponAmount;
	}

	public long getChannelCouponAmount() {
		return channelCouponAmount;
	}

	public void setChannelCouponAmount(long channelCouponAmount) {
		this.channelCouponAmount = channelCouponAmount;
	}

	public long getHospitalOnlinePayAmount() {
		return hospitalOnlinePayAmount;
	}

	public void setHospitalOnlinePayAmount(long hospitalOnlinePayAmount) {
		this.hospitalOnlinePayAmount = hospitalOnlinePayAmount;
	}

	public long getPlatformOnlinePayAmount() {
		return platformOnlinePayAmount;
	}

	public void setPlatformOnlinePayAmount(long platformOnlinePayAmount) {
		this.platformOnlinePayAmount = platformOnlinePayAmount;
	}

	public long getChannelOnlinePayAmount() {
		return channelOnlinePayAmount;
	}

	public void setChannelOnlinePayAmount(long channelOnlinePayAmount) {
		this.channelOnlinePayAmount = channelOnlinePayAmount;
	}

	public long getChannelCompanyPayAmount() {
		return channelCompanyPayAmount;
	}

	public void setChannelCompanyPayAmount(long channelCompanyPayAmount) {
		this.channelCompanyPayAmount = channelCompanyPayAmount;
	}

	public long getChannelCardPayAmount() {
		return channelCardPayAmount;
	}

	public void setChannelCardPayAmount(long channelCardPayAmount) {
		this.channelCardPayAmount = channelCardPayAmount;
	}

	public long getTotalCouponPayAmount() {
		return totalCouponPayAmount;
	}

	public void setTotalCouponPayAmount(long totalCouponPayAmount) {
		this.totalCouponPayAmount = totalCouponPayAmount;
	}
}
