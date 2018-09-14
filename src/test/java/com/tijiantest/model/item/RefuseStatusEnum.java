package com.tijiantest.model.item;

public enum RefuseStatusEnum {
    refused(1, "拒检"),
    examed(2, "提交");

    private String name;
    private int code;

    private RefuseStatusEnum(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() { 
        return Integer.valueOf(this.code);
    }

    public void setCode(Integer code) {
        this.code = code.intValue();
    }
}
