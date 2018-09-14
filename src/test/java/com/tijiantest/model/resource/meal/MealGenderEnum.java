package com.tijiantest.model.resource.meal;

public enum MealGenderEnum {
	MALE(0,"男"),
	FEMALE(1,"女"),
	GENERAL(2,"通用");
	
	private int code;
	private String name;
	
	private MealGenderEnum(int code, String name){
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
