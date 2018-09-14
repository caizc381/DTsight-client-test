package com.tijiantest.model.order.snapshot;

public enum ExamItemToMealEnum {

	inMeal(1,"套餐内项目"),
	outMeal(2,"套餐内删除项"),
	addToMeal(3,"套餐内新增项");
	
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

	private ExamItemToMealEnum(int code, String value){
		this.code = code;
		this.value = value;
	}
}
