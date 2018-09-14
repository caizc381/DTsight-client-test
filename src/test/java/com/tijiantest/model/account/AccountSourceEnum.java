package com.tijiantest.model.account;

public enum AccountSourceEnum {

	MY_TIJIAN(1, "来至主站"), CRM(2, "来至CRM"), MANAGE(3,"来自manage"),CHANNEL(4, "来至渠道商");

	private int code;

	private String name;

	AccountSourceEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	};
}
