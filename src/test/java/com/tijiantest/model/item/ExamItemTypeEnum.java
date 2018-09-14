package com.tijiantest.model.item;

public enum ExamItemTypeEnum {
	HOSPITAL(1, "体检中心项目"), 
	OUTSIDE(2, "外送项目"), 
	SELF(3, "自有项目"), 
	CHILD(4, "子项"), 
	LIMIT(5, "人数控制项"),
	DELETE(6, "废除项目");

	private String name;
	private int code;

	private ExamItemTypeEnum(int code, String name) {
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
