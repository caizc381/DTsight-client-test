package com.tijiantest.model.company;

public enum ExamCompanyTypeEnum {
	normal(1, "一般体检单位"), guest(2, "散客单位"), P(3, "P单位"), M(4, "M单位");

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

	private ExamCompanyTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
