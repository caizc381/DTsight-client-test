package com.tijiantest.model.organization;

public enum ResourceTypeEnum {

	MOBILE_LOGO(1, "logo"), PC_LOGO(2, "PC LOGO"), QR_CODE(3, "二维码"), COVER(4, "医院封面"), ENVIRONMENT_BIG_IMAGE(5,
			"医院环境大图片"), ENVIRONMENT_SMALL_IMAGE(6, "医院环境小图片"), PC_BANNER(7,
					"PC banner"), MOBILE_MAIN_BANNER(8, "手机端主banner"), MOBILE_DEPUTY_BANNER(9, "手机副banner");

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

	private ResourceTypeEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}
}
