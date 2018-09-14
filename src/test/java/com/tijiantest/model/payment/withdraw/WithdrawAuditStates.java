package com.tijiantest.model.payment.withdraw;

//提现审核状态
public enum WithdrawAuditStates {
	Wait(0, "待审核"), FirstTrialAdopt(1, "客户审核通过"), FirstTrialReject(2, "客服审核拒绝"), FinalReviewAdopt(3,
			"财务审核通过"), FinalReviewReject(4, "财务审核拒绝");

	private int code;
	private String value;

	private WithdrawAuditStates(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static WithdrawAuditStates valueOf(int value) throws WithdrawAuditException {
		switch (value) {
		case 0:
			return WithdrawAuditStates.Wait;
		case 1:
			return WithdrawAuditStates.FirstTrialAdopt;
		case 2:
			return WithdrawAuditStates.FirstTrialReject;
		case 3:
			return WithdrawAuditStates.FinalReviewAdopt;
		case 4:
			return WithdrawAuditStates.FinalReviewReject;
		default:
			throw new WithdrawAuditException(WithdrawAuditException.ILLEGAL_STATE, String.format("错误的状态 %s", value));
		}
	}

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

}
