package com.tijiantest.model.settlement;


/**
 * Created by wangzhongxing on 2017/8/29.
 */
public enum PrepaymentRecordTypeEnum {

    PERSIONAL_INVOICE(0,"个人开票"),
    COMPANY_INVOICE(1,"单位开票"),;

    private Integer code;

    private String value;

    private PrepaymentRecordTypeEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public static PrepaymentRecordTypeEnum getByCode(Integer code){
        for (PrepaymentRecordTypeEnum prepaymentRecordTypeEnum : PrepaymentRecordTypeEnum.values()){
            if (prepaymentRecordTypeEnum.getCode().equals(code)){
                return prepaymentRecordTypeEnum;
            }
        }
        return null;
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
