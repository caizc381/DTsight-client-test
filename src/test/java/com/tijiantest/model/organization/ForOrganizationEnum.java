package com.tijiantest.model.organization;

public enum ForOrganizationEnum {

	HOSPITAL(1,"体检中心站点"),
	CHANNEL(2,"渠道商站点");
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

	ForOrganizationEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
