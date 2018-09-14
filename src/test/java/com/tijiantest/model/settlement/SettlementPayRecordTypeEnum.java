package com.tijiantest.model.settlement;


public enum SettlementPayRecordTypeEnum {

    COMPANY_PAY(0,"单位收款"),
    PLATFORM_PAY(1,"平台付款");

    private Integer code;

    private String value;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private SettlementPayRecordTypeEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

}
