package com.tijiantest.model.account;

public enum SystemTypeEnum {
	C_LOGIN(1, "C端登录账户"),
	CRM_LOGIN(2,"crm登录账户"),
	CHANNEL_LOGIN(3,"渠道商系统录账户"),
	MANAGE_LOGIN(4,"manage系统录账户");

	private int code;

	private String name;

	SystemTypeEnum(int code, String name) {
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
