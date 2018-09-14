package com.tijiantest.model.account;

public enum RoleEnum {
	Normal_USER(1, "普通用户"), CRM_USER(2, "CRM用户"), HOSPITAL_MANAGER(3, "体检中心客户经理"), PLATFORM_MANAGER(4,
			"平台客户经理"), HOSPITAL_OPERATOR(5, "体检中心操作员"), HOSPITAL_GENERAL_MANAGER(7,
					"体检中心主管"), COMPANY_CUSTOMER_SERVICES(10, "客服"), COMPANY_FINANCE(11, "财务"), OPERATOPM_MANAGER(12,
							"运营主管"), CHANNEL_GENERAL_MANAGER(13,
									"渠道商主管"), CHANNEL_OPERATOR(14, "渠道商操作员"), CHANNEL_MANAGER(15, "渠道商客户经理"),HOSPITAL_MANAGER_ADJUST_PRICE(16, "可调价的体检中心客户经理");;
	private int code;
	private String name;

	RoleEnum(int code, String name) {
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
