package com.tijiantest.model.item;

public enum ExamItemRelationEnum {

	/**
	 * 一个项目包含几个小项，比如眼科检查包含（视力，裂隙灯...）
	 */
	COMPOSE(1, "合并关系"),
	/**
	 * 两个项目不可同时选择
	 */
	CONFLICT(2, "互斥关系"),
	/**
	 * 选择该项目需要先选择另一个项目，比如选择视力，必须要先选择眼科
	 */
	DEPEND(3, "依赖关系"),
	/**
	 * 一个项目由几个小项合并而成
	 */
	FAMILY(4, "父子关系");

	private String name;
	private int code;

	private ExamItemRelationEnum(int code, String name) {
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
