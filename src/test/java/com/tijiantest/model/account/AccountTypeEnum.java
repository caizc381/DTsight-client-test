package com.tijiantest.model.account;

public enum AccountTypeEnum {
	Register("mytijian注册生成", 1), Import("crm/tjzx导入", 2), Medical("体检人", 3), Demo("演示", 4), Manager("客户经理",
			5), SECOND_SITE("二级站点生成", 6);
	private String name;
	private int code;

	private AccountTypeEnum(String name, int code) {
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
