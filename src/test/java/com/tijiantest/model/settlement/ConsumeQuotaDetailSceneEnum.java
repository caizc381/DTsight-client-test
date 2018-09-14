package com.tijiantest.model.settlement;

public enum ConsumeQuotaDetailSceneEnum {
    HOSPITAL_INVOICE(1,"医院开票"),
    FINANCIAL_ADJUST(2,"账务调整"),
    SETTELMENT_PROFIT(3,"结算盈余"),
    SETTELMENT_PAY(4,"结算支付"),
    PLATFORM_SERVICE(5,"平台服务");



    private Integer code;

    private String value;

    ConsumeQuotaDetailSceneEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

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
}
