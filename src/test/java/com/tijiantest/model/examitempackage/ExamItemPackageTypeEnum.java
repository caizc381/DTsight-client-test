package com.tijiantest.model.examitempackage;

public enum ExamItemPackageTypeEnum {

	CUSTOMIZED(1, "自定义"), RISK(2, "风险");

	private String name;
	private int code;

	private ExamItemPackageTypeEnum(int code, String name) {
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
