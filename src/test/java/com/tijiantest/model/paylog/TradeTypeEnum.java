package com.tijiantest.model.paylog;

public enum TradeTypeEnum {
	OrderPayment(1, "订单支付"),
	OrderCancelRefund(2, "取消订单退款"),
	CompleteRefund(3,"体检完成退款"),
	Recharge(4,"充值"),
	Withdraw(5,"提现"),
	OrderChangeRefund(6, "订单修改项目退款"),
	OrderCancelByCRM(7, "CRM客户经理取消订单"),
    WithdrawFailed(8, "提现失败退款"),
	OrderCancelByService(9, "每天健康客服取消订单"),
	OfflineRefund(10, "线下支付退款"),
	unifyRefund(11, "统一退款，不区分退款场景，用于退款灰度发布过渡");
	
	private int code;
	private String value;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private TradeTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

}
