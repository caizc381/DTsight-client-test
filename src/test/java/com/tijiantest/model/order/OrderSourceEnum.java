package com.tijiantest.model.order;

public enum OrderSourceEnum {
	MYTIJIAN(1,"PC端"),
	MOBILE(2,"手机端"),
	CRM(3,"CRM"),
	AUTO_BOOKING(4,"自动下单"),
	NOLOGIN_BOOKING(5,"免登录下单");
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
	
	private OrderSourceEnum(int code, String value){
		this.code = code;
		this.value = value;
	}
}
