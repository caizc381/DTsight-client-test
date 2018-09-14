package com.tijiantest.model.settlement;


public enum ExamOrderRefundSettleEnum {

    NOT_NEED_REFUND(1,"不需结算已结算订单退款"),
    NEED_REFUND(2,"需结算已结算订单退款"),
    REFUND_OK(3,"已结算已结算订单退款");

    private Integer code;

    private String value;

    ExamOrderRefundSettleEnum(Integer code, String value){
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
