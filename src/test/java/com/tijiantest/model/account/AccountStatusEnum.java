package com.tijiantest.model.account;

public enum AccountStatusEnum {
	NORMAL(0,"正常"),
	DISABLED(-1,"不可用"),
	ABNORMAL(1,"异常");
	
	private String name;
	private int code;
	
	private AccountStatusEnum(int code, String name){
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
