package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

public class TradePrepaymentRecord implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8531858981139443123L;

	private Integer id;

    /**
     * 机构ID
     */
    private Integer organizationId;

    /**
     * 机构名称
     */
    private String organizationName;

    /**
     * 单位ID
     */
    private Integer companyId;

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 是否为平台单位
     */
    private Boolean isPlatformCompany;

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

    /**
     * 付款类型:0=个人开票，1=单位开票
     */
    private Integer type;

    /**
     * 状态:1=未结算 2=结算待医院确认 3=结算医院已撤销 4=结算医院已确认
     */
    private Integer status;

    private Integer settlementStatus;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 付款凭证
     */
    private String certificate;

    /**
     * 备注
     */
    private String remark;
    /**
     * 退款机构ID
     */
    private Integer refundOrganizationId;

    private String refundOrganizationName;

    /**
     * 退款单位ID
     */
    private Integer refundCompanyId;

    private String refundCompanyName;


    /**
     * 渠道特殊退款批次号
     */
    private String sn ;
    /**
     * 结算视角 0 医院平台 1 平台渠道
     */
    private Integer settlementViewType;

    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getIsPlatformCompany() {
        return isPlatformCompany;
    }

    public void setIsPlatformCompany(Boolean platformCompany) {
        isPlatformCompany = platformCompany;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getRefundOrganizationId() {
        return refundOrganizationId;
    }

    public void setRefundOrganizationId(Integer refundOrganizationId) {
        this.refundOrganizationId = refundOrganizationId;
    }

    public Integer getRefundCompanyId() {
        return refundCompanyId;
    }

    public void setRefundCompanyId(Integer refundCompanyId) {
        this.refundCompanyId = refundCompanyId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Boolean getPlatformCompany() {
        return isPlatformCompany;
    }

    public void setPlatformCompany(Boolean platformCompany) {
        isPlatformCompany = platformCompany;
    }

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }

    public String getRefundOrganizationName() {
        return refundOrganizationName;
    }

    public void setRefundOrganizationName(String refundOrganizationName) {
        this.refundOrganizationName = refundOrganizationName;
    }

    public String getRefundCompanyName() {
        return refundCompanyName;
    }

    public void setRefundCompanyName(String refundCompanyName) {
        this.refundCompanyName = refundCompanyName;
    }
}
