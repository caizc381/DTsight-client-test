package com.tijiantest.model.resource.meal;

public enum CardMealTypeEnum {
	SELF(1,"个人套餐"),
	FAMILY(2,"家属套餐");
	
	private String name;
	private int code;
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
	
	private CardMealTypeEnum(int code,String name){
		this.name = name;
		this.code = code;
	}
}
