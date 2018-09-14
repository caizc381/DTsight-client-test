package com.tijiantest.model.settlement;

import java.io.Serializable;

/**
 * Created by wangzhongxing on 2017/11/30.
 */
public class TradeConsumeQuotaStatistics implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 机构id
     */
    private Integer organizationId;

    /**
     * 医院名称
     */
    private String organizationName;

    /**
     * 消费明细总额
     */
    private Long totalAmount;

    /**
     * 本月净消费额
     */
    private Long presentMounthAmont;

    /**
     * 上月净消费额
     */
    private Long forwardMounthAmont;

    /**
     * 未处理消费明细数
     */
    private Integer todoList;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getPresentMounthAmont() {
        return presentMounthAmont;
    }

    public void setPresentMounthAmont(Long presentMounthAmont) {
        this.presentMounthAmont = presentMounthAmont;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getForwardMounthAmont() {
        return forwardMounthAmont;
    }

    public void setForwardMounthAmont(Long forwardMounthAmont) {
        this.forwardMounthAmont = forwardMounthAmont;
    }

    public Integer getTodoList() {
        return todoList;
    }

    public void setTodoList(Integer todoList) {
        this.todoList = todoList;
    }
}
