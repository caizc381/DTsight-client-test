package com.tijiantest.model.order;

public enum OperateAppEnum {

	CLIENT(1, "c端系统"),
	CRM(3,"crm系统"),
	CHANNEL(4,"渠道商系统"),
	SCHEDULE_JOB(5,"定时任务"),
	MANAGE(6,"manage系统"),
	OPEN_API(7,"开方平台" ),
	MEDIATOR(8,"对接系统" ),
	OPS(9, "OPS系统");
	
	private int code;
	
	private String name;
	
	OperateAppEnum(int code, String name){
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	};
}
