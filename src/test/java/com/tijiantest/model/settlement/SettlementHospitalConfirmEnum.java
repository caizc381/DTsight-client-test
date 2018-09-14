package com.tijiantest.model.settlement;

/**
 * Created by wangzhongxing on 2017/8/3.
 */
public enum SettlementHospitalConfirmEnum {

    UNSETTLEMENT(1,"未结算"),
    SETTLEMENT_TO_BE_CONFIRM(2,"医院待确认"),
    SETTLEMENT_CANCELED(3,"医院已撤销"),
    SETTLEMENT_CONFIRMD(4,"医院已确认"),
    SETTLEMENT_COMPLETE_PAYMENT(5,"医院完成收款");

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

    private SettlementHospitalConfirmEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

}
