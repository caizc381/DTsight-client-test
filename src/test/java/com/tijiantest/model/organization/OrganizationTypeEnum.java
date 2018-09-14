package com.tijiantest.model.organization;

public enum OrganizationTypeEnum {
	HOSPITAL(1, "体检中心"), CHANNEL(2, "渠道商");
	private String name;
	private int code;

	private OrganizationTypeEnum(int code, String name) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
