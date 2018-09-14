package com.tijiantest.model.settlement;

import java.util.Date;

/**
 * 支付订单结算表
 */
public class PaymentOrderSettlementDO {
    private Integer id;
    private String orderNum;
    private Integer orderType;
    private Integer organizationId;
    private Integer hospitalCompanyId;
    private Integer hospitalSettlementStatus;
    private Integer refundSettlement;
    private String settlementBatchSn;
    private String paymentName;
    private Integer isDeleted;
    private Date gmtCreated;
    private Date gmtModified;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getHospitalCompanyId() {
        return hospitalCompanyId;
    }

    public void setHospitalCompanyId(Integer hospitalCompanyId) {
        this.hospitalCompanyId = hospitalCompanyId;
    }

    public Integer getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
    }

    public Integer getRefundSettlement() {
        return refundSettlement;
    }

    public void setRefundSettlement(Integer refundSettlement) {
        this.refundSettlement = refundSettlement;
    }

    public String getSettlementBatchSn() {
        return settlementBatchSn;
    }

    public void setSettlementBatchSn(String settlementBatchSn) {
        this.settlementBatchSn = settlementBatchSn;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
