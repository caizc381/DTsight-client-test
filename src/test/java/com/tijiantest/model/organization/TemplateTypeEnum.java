package com.tijiantest.model.organization;

public enum TemplateTypeEnum {

	PC(1, "PC端"), MOBILE(2, "mobile");
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

	TemplateTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
