package com.tijiantest.model.payment;

public class PaymentException extends Exception {
	
	private static final long serialVersionUID = 8006419184585835544L;
	public static int NONE_PAYMENT_WAY = 1;
	public static int BALANCE_NOT_ENOUGH = 2;
	public static int BALANCE_IS_ZERO = 3;

	public static int ALI_PAY_FAILED = 4;
	public static int ALI_PAY_VERIFY_FAILED = 5;

	public static int WEIXIN_PAY_BALANCE_NOTENOUGH = 6;
	public static int WEIXIN_VERIFY_FAILED = 7;

	/**
	 * 提现类型不存在
	 */
	public static int WITHDRAW_TYPE_NOT_FOUND = 8;

	/**
	 * 不支持线下支付
	 */
	public static int NOT_ACCEPT_OFFLINE_PAYMENT = 10;
	/**
	 * 支付金额不能为0
	 */
	public static int AMOUNT_IS_ZERO = 11;

	private int code;

	public PaymentException(int code, String message) {
		this(code, message, null);
	}

	public PaymentException(int code, Throwable t) {
		this(code, null, t);
	}

	public PaymentException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
