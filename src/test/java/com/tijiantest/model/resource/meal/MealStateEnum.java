package com.tijiantest.model.resource.meal;

public enum MealStateEnum {
	NORMAL(0, "正常"), ERROR(1, "有错误"), DISABLE(2, "删除");
	private String name;
	private int code;

	private MealStateEnum(int code, String name) {
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
