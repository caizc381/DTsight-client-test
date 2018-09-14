package com.tijiantest.model.account;

public enum AccountRelationTypeEnum {
	MedicalReserver(1, "预约人"), MedicalUser(2, "体检人");
	private String name;
	private int code;

	private AccountRelationTypeEnum(int code, String name) {
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
