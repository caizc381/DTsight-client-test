package com.tijiantest.model.order;

public class HospitalSettleInfoSnapshot extends SettleInfoSnapshot{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3953248377429871829L;
	/**
	 * 线下应收金额
	 */
	private Integer offlinePayMoney;
	/**
	 * 线下未付金额
	 */
	private Integer offlineUnpayMoney;
	
	public Integer getOfflinePayMoney() {
		return offlinePayMoney;
	}
	public void setOfflinePayMoney(Integer offlinePayMoney) {
		this.offlinePayMoney = offlinePayMoney;
	}
	public Integer getOfflineUnpayMoney() {
		return offlineUnpayMoney;
	}
	public void setOfflineUnpayMoney(Integer offlineUnpayMoney) {
		this.offlineUnpayMoney = offlineUnpayMoney;
	}

}
