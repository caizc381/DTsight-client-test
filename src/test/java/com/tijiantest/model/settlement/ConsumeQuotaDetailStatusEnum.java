package com.tijiantest.model.settlement;

/**
 * Created by wangzhongxing on 2017/12/1.
 */
public enum  ConsumeQuotaDetailStatusEnum {

    HOSPITAL_TO_BE_CONFIRMED(1,"财务审核中"),
    FREEZING(2,"冻结中"),
    HOSPITAL_HAS_CONFIRMED(3,"财务已确认"),
    PLATFORM_HAS_REVOKED(4,"平台已撤销");

    private Integer code;

    private String value;

    ConsumeQuotaDetailStatusEnum(Integer code, String value){
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
