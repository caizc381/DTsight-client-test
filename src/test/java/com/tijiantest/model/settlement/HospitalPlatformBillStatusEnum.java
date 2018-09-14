package com.tijiantest.model.settlement;

/**
 * Created by wangzhongxing on 2017/8/10.
 */
public enum HospitalPlatformBillStatusEnum {

    HOSPITAL_TO_BE_CONFIRM(2,"医院待确认"),
    HOSPITAL_CANCELED(3,"医院已撤销"),
    PLATFORM_TO_BE_CONFIRMD(4,"平台审核中"),
    PLATFORM_CONFIREMD(5,"平台审核通过(医院审核通过）"),
    PLATFORM_COMPLETE_PAYMENT(6,"平台完成收款"),
    PLATFORM_CONFIRMD_BILL(7,"平台审核通过（消费额度等）/医院审核中"),
    PLATFORM_CAIWU_TOBE_CONFIRM(8,"平台审核通过（消费额度等）/财务审核中");


    private Integer code;

    private String value;

    private HospitalPlatformBillStatusEnum(Integer code, String value){
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
