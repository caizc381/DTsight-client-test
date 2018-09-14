package com.tijiantest.model.settlement;

public enum HospitalCompanyBillStatusEnum {

    HOSPITAL_TO_BE_CONFIRMED(2,"医院待确认"),
    HOSPITAL_HAS_REVOKED(3,"医院已撤销"),
    HOSPITAL_HAS_CONFIRMED(4,"医院已确认"),
    HOSPITAL_HAS_RECEVIDE_MONEY(5,"医院完成收款");

    private Integer code;

    private String value;

    HospitalCompanyBillStatusEnum(Integer code, String value){
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
