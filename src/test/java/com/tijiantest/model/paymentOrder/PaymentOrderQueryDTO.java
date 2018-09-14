package com.tijiantest.model.paymentOrder;



import java.io.Serializable;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * @author mawanqun
 */
public class PaymentOrderQueryDTO implements Serializable{

    private static final long serialVersionUID = -4387276337383161912L;

    private Integer organizationId;
    /**
     * 下单开始时间
     */
    private String startTime;

    /**
     * 下单结束时间
     */
    private String endTime;

    /**
     * 付款人姓名
     */
    private String name;

    /**
     * 订单状态：0 未支付，1 支付中,2 已支付，3部分退款,4 已撤销，5.已关闭
     */
    private List<Integer> statusList;


    /**
     * 结算状态：0 未结算，1 已结算
     */
    private Integer settlementStatus;

    /**
     * 客户经理id
     */
    private Integer managerId;

    private Page page;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}
