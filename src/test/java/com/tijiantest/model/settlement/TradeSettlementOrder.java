package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangzhongxing on 2017/8/8.
 */
public class TradeSettlementOrder implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;
    /**
     * 结算订单号
     */
    private String sn;
    /**
     * 订单编号
     */
    private String refOrderNum;
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
     * 母卡支付金额
     */
    private Long pcardPayAmount;
    /**
     * 线上支付金额
     */
    private Long onlinePayAmount;
    /**
     * 平台支付金额
     */
    private Long platformPayAmount;
    /**
     * 体检卡支付金额
     */
    private Long cardPayAmount;
    /**
     * 线下支付金额
     */
    private Long offlinePayAmount;

    /**
     * 医院优惠券支付
     */
    private Long hospitalCoupAmount;

    /**
     * 平台优惠券支付
     */
    private Long platformCoupAmount;

    private Long channelCoupAmount;
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

    public Long getPcardPayAmount() {
        return pcardPayAmount;
    }

    public void setPcardPayAmount(Long pcardPayAmount) {
        this.pcardPayAmount = pcardPayAmount;
    }

    public Long getOnlinePayAmount() {
        return onlinePayAmount;
    }

    public void setOnlinePayAmount(Long onlinePayAmount) {
        this.onlinePayAmount = onlinePayAmount;
    }

    public Long getPlatformPayAmount() {
        return platformPayAmount;
    }

    public void setPlatformPayAmount(Long platformPayAmount) {
        this.platformPayAmount = platformPayAmount;
    }

    public Long getCardPayAmount() {
        return cardPayAmount;
    }

    public void setCardPayAmount(Long cardPayAmount) {
        this.cardPayAmount = cardPayAmount;
    }

    public Long getOfflinePayAmount() {
        return offlinePayAmount;
    }

    public void setOfflinePayAmount(Long offlinePayAmount) {
        this.offlinePayAmount = offlinePayAmount;
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

    public Long getHospitalCoupAmount() {
        return hospitalCoupAmount;
    }

    public void setHospitalCoupAmount(Long hospitalCoupAmount) {
        this.hospitalCoupAmount = hospitalCoupAmount;
    }

    public Long getPlatformCoupAmount() {
        return platformCoupAmount;
    }

    public void setPlatformCoupAmount(Long platformCoupAmount) {
        this.platformCoupAmount = platformCoupAmount;
    }

    public Long getChannelCoupAmount() {
        return channelCoupAmount;
    }

    public void setChannelCoupAmount(Long channelCoupAmount) {
        this.channelCoupAmount = channelCoupAmount;
    }
}
