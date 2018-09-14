package com.tijiantest.model.payment.withdraw;

import com.tijiantest.model.payment.PaymentException;

public enum WithdrawTypeEnum {
	aliType(1, "支付宝"), bankType(2, "银行"), original(3, "原路退回");

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

	private WithdrawTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static WithdrawTypeEnum valueOf(int code) throws PaymentException {
		switch (code) {
		case 1:
			return WithdrawTypeEnum.aliType;
		case 2:
			return WithdrawTypeEnum.bankType;
		case 3:
			return original;
		default:
			throw new PaymentException(PaymentException.WITHDRAW_TYPE_NOT_FOUND, "错误的提现方式");
		}
	}
}
