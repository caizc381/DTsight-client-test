package com.tijiantest.model.payment.withdraw;

//用户充值异常
public class WithdrawAuditException extends Exception{
	
	private static final long serialVersionUID = 7354754914219581590L;
	//状态异常
	public static int ILLEGAL_STATE =1;
	//当前状态不允许操作
	public static int OPERATION_WAS_DENIED = 2;
	//余额不足
	public static int INSUFFICIENT_BALANCE=3;
	
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public WithdrawAuditException(String message){
		super(message);
	}
	
	public WithdrawAuditException(int code, String message){
		super(message);
		this.code = code;
	}
}
