package com.tijiantest.model.resource.meal;

public enum MealTypeEnum {

	COMPANY_MEAL(1, "单位套餐"), FAVORITE_MEAL(2, "收藏套餐"), COMMON_MEAL(3, "通用套餐"), FOUNDATION_MEAL(4, "常规检查套餐");

	private String name;
	private int code;

	private MealTypeEnum(int code, String name) {
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
