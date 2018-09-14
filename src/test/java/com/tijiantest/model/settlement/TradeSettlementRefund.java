package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangzhongxing on 2017/8/16.
 */
public class TradeSettlementRefund implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;
    /**
     * 结算退款号
     */
    private String sn;
    /**
     * 订单编号
     */
    private String refOrderNum;

    /**
     * 订单类型
     */
    private Integer refOrderType;
    /**
     * 体检机构id
     */
    private Integer organizationId;
    /**
     * 体检单位id
     */
    private Integer companyId;
    /**
     * 结算批次号
     */
    private String batchSn;
    /**
     * 医院平台账单流水号
     */
    private String hospitalPlatformSn;
    /**
     * 医院单位账单流水号
     */
    private String hospitalCompanySn;
    /**
     * 母卡退款金额
     */
    private Long pcardRefundAmount;
    /**
     * 线上退款金额
     */
    private Long onlineRefundAmount;
    /**
     * 平台退款金额
     */
    private Long platformRefundAmount;
    /**
     * 体检卡退款金额
     */
    private Long cardRefundAmount;
    /**
     * 线下退款金额
     */
    private Long offlineRefundAmount;
    /**
     * 医院优惠券退款
     */
    private Long hospitalCoupRefundAmount;
    /**
     * 平台优惠券退款
     */
    private Long platformCoupRefundAmount;
    /**
     * 渠道优惠券退款
     */
    private Long channelCoupRefundAmount;
    /**
     * 退款时间
     */
    private Date refundTime;
    /**
     * 1=医院待确认 2=医院已确认 3=医院已撤销
     * @see
     */
    private Integer hospitalSettlementStatus;
    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;
    /**
     * 数据插入时间
     */
    private Date gmtCreated;
    /**
     * 数据更新时间
     */
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getRefOrderNum() {
        return refOrderNum;
    }

    public void setRefOrderNum(String refOrderNum) {
        this.refOrderNum = refOrderNum;
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

    public String getHospitalPlatformSn() {
        return hospitalPlatformSn;
    }

    public void setHospitalPlatformSn(String hospitalPlatformSn) {
        this.hospitalPlatformSn = hospitalPlatformSn;
    }

    public String getHospitalCompanySn() {
        return hospitalCompanySn;
    }

    public void setHospitalCompanySn(String hospitalCompanySn) {
        this.hospitalCompanySn = hospitalCompanySn;
    }

    public Long getPcardRefundAmount() {
        return pcardRefundAmount;
    }

    public void setPcardRefundAmount(Long pcardRefundAmount) {
        this.pcardRefundAmount = pcardRefundAmount;
    }

    public Long getOnlineRefundAmount() {
        return onlineRefundAmount;
    }

    public void setOnlineRefundAmount(Long onlineRefundAmount) {
        this.onlineRefundAmount = onlineRefundAmount;
    }

    public Long getPlatformRefundAmount() {
        return platformRefundAmount;
    }

    public void setPlatformRefundAmount(Long platformRefundAmount) {
        this.platformRefundAmount = platformRefundAmount;
    }

    public Long getCardRefundAmount() {
        return cardRefundAmount;
    }

    public void setCardRefundAmount(Long cardRefundAmount) {
        this.cardRefundAmount = cardRefundAmount;
    }

    public Long getOfflineRefundAmount() {
        return offlineRefundAmount;
    }

    public void setOfflineRefundAmount(Long offlineRefundAmount) {
        this.offlineRefundAmount = offlineRefundAmount;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public Integer getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
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

    public Integer getRefOrderType() {
        return refOrderType;
    }

    public void setRefOrderType(Integer refOrderType) {
        this.refOrderType = refOrderType;
    }

    public Long getHospitalCoupRefundAmount() {
        return hospitalCoupRefundAmount;
    }

    public void setHospitalCoupRefundAmount(Long hospitalCoupRefundAmount) {
        this.hospitalCoupRefundAmount = hospitalCoupRefundAmount;
    }

    public Long getPlatformCoupRefundAmount() {
        return platformCoupRefundAmount;
    }

    public void setPlatformCoupRefundAmount(Long platformCoupRefundAmount) {
        this.platformCoupRefundAmount = platformCoupRefundAmount;
    }

    public Long getChannelCoupRefundAmount() {
        return channelCoupRefundAmount;
    }

    public void setChannelCoupRefundAmount(Long channelCoupRefundAmount) {
        this.channelCoupRefundAmount = channelCoupRefundAmount;
    }
}
