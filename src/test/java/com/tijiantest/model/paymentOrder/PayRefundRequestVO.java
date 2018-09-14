package com.tijiantest.model.paymentOrder;

public class PayRefundRequestVO {
    private String refOrderNum;
    private Integer refOrderType;
    private Long amount;
    private String tradeRemark;


    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getRefOrderNum() {
        return refOrderNum;
    }

    public void setRefOrderNum(String refOrderNum) {
        this.refOrderNum = refOrderNum;
    }


    public Integer getRefOrderType() {
        return refOrderType;
    }

    public void setRefOrderType(Integer refOrderType) {
        this.refOrderType = refOrderType;
    }

    public String getTradeRemark() {
        return tradeRemark;
    }

    public void setTradeRemark(String tradeRemark) {
        this.tradeRemark = tradeRemark;
    }
}
