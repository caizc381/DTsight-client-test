package com.tijiantest.model.payment;

public enum PaymentTypeEnum {
	Card(1, "卡支付"), Balance(2, "余额支付"), Alipay(3, "支付宝支付"), Weixin(4, "微信支付"), Unionpay(5, "银联"),

	/**
	 * 线下支付 code 7
	 */
	Offline(7, "线下支付"),

	WxApp(10, "微信小程序支付");

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

	private PaymentTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
