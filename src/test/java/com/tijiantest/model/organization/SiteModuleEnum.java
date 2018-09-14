package com.tijiantest.model.organization;

public enum SiteModuleEnum {

	MAIN_BANNER(1, "主banner区域"), OPERATE_MENU(2, "操作菜单入口区1"), FAST_ENTER(3, "便捷入口区"), DEPUTY_BANNER(4,
			"副banner区"), HOSPITAL_INTRO(5, "医院简介"), LOGO(6, "logo区");

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

	private SiteModuleEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
