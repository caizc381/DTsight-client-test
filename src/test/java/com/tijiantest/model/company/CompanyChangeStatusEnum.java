package com.tijiantest.model.company;

public enum CompanyChangeStatusEnum {

	UnSync(-1, "未同步"), Syncing(0, "同步中"), SyncComplete(1, "同步完成"), Conflicting(2, "单位冲突"), HisNameDuplication(3,
			"his单位名称重复");

	private int code;
	private String value;

	private CompanyChangeStatusEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

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

}
