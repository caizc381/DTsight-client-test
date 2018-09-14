package com.tijiantest.model.item;

public enum ExamitemSpeciesEnum {

	OFFICIAL_SPECIES(1, "官方分类"), BODY_SPECIES(2, "身体部位");

	private String name;
	private int code;

	private ExamitemSpeciesEnum(int code, String name) {
		this.code = code;
		this.name = name;
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
