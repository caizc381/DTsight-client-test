package com.tijiantest.model.order;

import java.io.Serializable;

/**
 * 撤单返回结果集
 */
public class FailOrder implements Serializable{
    private static final long serialVersionUID = 4506933985725611887L;

    FailOrder(Integer orderId, String failMsg) {
        this.orderId = orderId;
        this.failMsg = failMsg;
    }

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 失败原因
     */
    private String failMsg;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}
