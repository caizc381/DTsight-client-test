package com.tijiantest.model.card;

public enum CardStatusEnum {
	UNUSABLE(0,"不可用"),
	USABLE(1,"可用"),
	CANCELLED(2,"已撤销"),
	BALANCE_RECOVERED(3,"余额收回");
	
	private Integer code;
	
	private String value;

	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private CardStatusEnum(Integer code, String value){
		this.code = code;
		this.value = value;
	}
}
