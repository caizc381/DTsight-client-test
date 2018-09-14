package com.tijiantest.model.card;

public enum CardTypeEnum {

	ENTITY(2, "实体卡"), VIRTUAL(1, "虚拟卡");

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

	private CardTypeEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}
}
