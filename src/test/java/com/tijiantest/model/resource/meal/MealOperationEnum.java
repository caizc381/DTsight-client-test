package com.tijiantest.model.resource.meal;

public enum MealOperationEnum {
	CREATE("create"),
	MODIFY("modify"),
	DELETE("delete");
	private String value;
	
	MealOperationEnum(String value){
		this.value = value;
	}
	public String value(){
		return this.value;
	}
}
