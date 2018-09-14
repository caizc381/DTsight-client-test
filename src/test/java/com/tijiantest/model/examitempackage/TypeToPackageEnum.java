package com.tijiantest.model.examitempackage;

public enum TypeToPackageEnum {

    NORMAL(0, "正常"),
    DUPLICATE(1, "重复项目"),
	CONFLICT(2, "冲突项目");

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

    private TypeToPackageEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
