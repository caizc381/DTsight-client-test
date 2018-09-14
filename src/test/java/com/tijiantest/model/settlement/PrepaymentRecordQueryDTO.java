package com.tijiantest.model.settlement;



import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.tijiantest.util.pagination.Page;

public class PrepaymentRecordQueryDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3042509928192606536L;

	private Integer id;

    /**
     * 机构ID
     */
    private Integer organizationId;

    /**
     * 单位ID
     */
    private List<Integer> companyIds;

    /**
     * 结算批次号
     */
    private String batchSn;

    /**
     * 付款金额
     */
    private Integer amount;

    /**
     * 付款时间
     */
    private Date paymentTime;

    private Date startTime;

    private Date endTime;

    /**
     * 付款类型:0=个人开票，1=单位开票
     */
    private Integer type;

    /**
     * 状态:0=未结算 1=结算待医院确认 2=结算医院已确认 3=结算医院已撤销
     */
    private Integer status;

    private Integer settlementStatus;

    private Integer channelSettlementStatus;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 付款凭证
     */
    private String certificate;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

    private Page page;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }


    public List<Integer> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Integer> companyIds) {
        this.companyIds = companyIds;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Integer getSettlementStatus() {	
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Integer getChannelSettlementStatus() {
        return channelSettlementStatus;
    }

    public void setChannelSettlementStatus(Integer channelSettlementStatus) {
        this.channelSettlementStatus = channelSettlementStatus;
    }
}
