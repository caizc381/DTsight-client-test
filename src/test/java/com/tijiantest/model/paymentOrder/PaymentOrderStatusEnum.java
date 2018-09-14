package com.tijiantest.model.paymentOrder;

public enum PaymentOrderStatusEnum {

    UNPAID(0,"未付款"),
    PAYING(1,"支付中"),
    PAYSUCCESS(2,"已支付"),
    PARTREFUND(3,"部分退"),
    REVOKE(4, "已撤销"),
    CLOSE(5, "已关闭");

    private int code;
    private String name;

    PaymentOrderStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
