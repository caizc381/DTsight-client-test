package com.tijiantest.model.company;

public enum IsDeletedEnum {
	DELETED(1),NOT_DELETED(0);
	private int code;
	private IsDeletedEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
