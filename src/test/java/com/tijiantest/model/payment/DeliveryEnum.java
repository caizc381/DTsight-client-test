package com.tijiantest.model.payment;

public enum DeliveryEnum {
	delivery(1,"寄送"),
	getBySelf(2,"自取"),
	email(3,"电子");
	
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

	private DeliveryEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
