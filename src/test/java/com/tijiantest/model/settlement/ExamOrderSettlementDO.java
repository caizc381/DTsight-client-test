package com.tijiantest.model.settlement;

import java.util.Date;

public class ExamOrderSettlementDO {
    /**
     * 
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 机构ID
     */
    private Integer organizationId;

    /**
     * 单位ID
     */
    private Integer hospitalCompanyId;

    /**
     * 1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     */
    private Integer hospitalSettlementStatus;

    /**
     * 0=不需结算订单退款，1=需结算订单退款 2=已结算订单退款
     */
    private Integer refundSettlement;

    /**
     * 结算批次号
     */
    private String settlementBatchSn;
    /**
     *渠道单位ID
     */
    private Integer channelCompanyid;
    /**
     * 渠道订单结算状态(渠道端)  1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     */
    private Integer channelSettlementStatus;
    /**
     * 渠道订单需结算退款（渠道端)
     */
    private Integer channelRefundSettlement;

    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;

    /**
     * 
     */
    private Date gmtCreated;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 
     * @return id 
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id 
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 订单编号
     * @return order_num 订单编号
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 订单编号
     * @param orderNum 订单编号
     */
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum == null ? null : orderNum.trim();
    }

    /**
     * 机构ID
     * @return organization_id 机构ID
     */
    public Integer getOrganizationId() {
        return organizationId;
    }

    /**
     * 机构ID
     * @param organizationId 机构ID
     */
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getHospitalCompanyId() {
        return hospitalCompanyId;
    }

    public void setHospitalCompanyId(Integer hospitalCompanyId) {
        this.hospitalCompanyId = hospitalCompanyId;
    }

    /**
     * 1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     * @return hospital_settlement_status 1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     */
    public Integer getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    /**
     * 1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     * @param hospitalSettlementStatus 1=医院未结算  2=批次待确认 3=批次已确认 4=批次已撤销
     */
    public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
    }

    /**
     * 1=不需结算订单退款，2=需结算订单退款 3=已结算订单退款
     * @return refund_settlement 1=不需结算订单退款，2=需结算订单退款 3=已结算订单退款
     */
    public Integer getRefundSettlement() {
        return refundSettlement;
    }

    /**
     * 1=不需结算订单退款，2=需结算订单退款 3=已结算订单退款
     * @param refundSettlement 1=不需结算订单退款，2=需结算订单退款 3=已结算订单退款
     */
    public void setRefundSettlement(Integer refundSettlement) {
        this.refundSettlement = refundSettlement;
    }

    /**
     * 结算批次号
     * @return settlement_batch_sn 结算批次号
     */
    public String getSettlementBatchSn() {
        return settlementBatchSn;
    }

    /**
     * 结算批次号
     * @param settlementBatchSn 结算批次号
     */
    public void setSettlementBatchSn(String settlementBatchSn) {
        this.settlementBatchSn = settlementBatchSn == null ? null : settlementBatchSn.trim();
    }

    /**
     * 0=数据有效  1=数据无效
     * @return is_deleted 0=数据有效  1=数据无效
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * 0=数据有效  1=数据无效
     * @param isDeleted 0=数据有效  1=数据无效
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 
     * @return gmt_created 
     */
    public Date getGmtCreated() {
        return gmtCreated;
    }

    /**
     * 
     * @param gmtCreated 
     */
    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * 
     * @return gmt_modified 
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 
     * @param gmtModified 
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getChannelCompanyid() {
        return channelCompanyid;
    }

    public void setChannelCompanyid(Integer channelCompanyid) {
        this.channelCompanyid = channelCompanyid;
    }

    public Integer getChannelSettlementStatus() {
        return channelSettlementStatus;
    }

    public void setChannelSettlementStatus(Integer channelSettlementStatus) {
        this.channelSettlementStatus = channelSettlementStatus;
    }

    public Integer getChannelRefundSettlement() {
        return channelRefundSettlement;
    }

    public void setChannelRefundSettlement(Integer channelRefundSettlement) {
        this.channelRefundSettlement = channelRefundSettlement;
    }
}