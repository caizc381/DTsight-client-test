package com.tijiantest.model.order;

public enum RefuseStatusEnum {

	refused(1, "拒检"),

	examed(2, "提交");

	private String name;
	private int code;

	private RefuseStatusEnum(int code, String name) {
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
