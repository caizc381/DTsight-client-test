package com.tijiantest.model.company;

public enum CompanySettleModeEnum {
	USE_PROJECT(0),USE_PERSONCOUNT(1);
	private int code;
	private CompanySettleModeEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
