package com.tijiantest.model.settlement;

import java.io.Serializable;

public class UnsettlementPaymentOrder implements Serializable{

    private static final long serialVersionUID = 7733694079884909419L;
    /**
     * 订单号
     */
    private String orderNum;
    /**
     * 下单时间
     */
    private Long insertTime;

    /**
     * 客户经理id
     */
    private Integer managerId;
    /**
     * 客户经理名称
     */
    private String managerName;

    /**
     * 付款人
     */
    private String paymentName;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单金额
     */
    private Long amount;

    public Long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Long insertTime) {
        this.insertTime = insertTime;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
