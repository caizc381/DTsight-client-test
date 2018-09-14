package com.tijiantest.model.order.orderrefund;

import java.io.Serializable;
import java.util.List;


/**
 * @author weifeng
 * @date 2017/8/3
 */
public class BatchOrderRefundAuditVO implements Serializable{
    private static final long serialVersionUID = -4047521954091228662L;

    private List<String> orderNumList;
    private String reason;

    public List<String> getOrderNumList() {
        return orderNumList;
    }

    public void setOrderNumList(List<String> orderNumList) {
        this.orderNumList = orderNumList;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
