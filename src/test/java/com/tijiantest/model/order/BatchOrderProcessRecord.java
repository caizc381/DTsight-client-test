package com.tijiantest.model.order;

import java.io.Serializable;

/**
 * Created by king on 2017/5/9.
 */
public class BatchOrderProcessRecord implements Serializable{
    private static final long serialVersionUID = -1453920494587522886L;
    private Integer id;

    /**
     * 下单进程详情id
     */
    private Integer processId;

    /**
     * 用户id
     */
    private Integer accountId;

    /**
     * 订单id
     */
    private String orderNum;

    /**
     * 失败理由
     */
    private String failMsg;

    /**
     * 状态  1.正在下单  2.完成
     */
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}