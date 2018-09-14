package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangzhongxing on 2017/8/9.
 */
public class TradeSettlementBatch implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;

    /**
     * 结算批次号
     */
    private String sn;
    /**
     * 体检机构id
     */
    private Integer hospitalId;
    /**
     * 体检单位id
     */
    private Integer companyId;
    /**
     * 单位名称，非DB字段
     */
    private String companyName;
    /**
     * 单位支付金额
     */
    private Long companyPayAmount;
    /**
     * 线上支付金额
     */
    private Long onlinePayAmount;
    /**
     * 平台支付金额
     */
    private Long platformPayAmount;
    /**
     * 单位退款金额
     */
    private Long companyRefundAmount;
    /**
     * 线上退款金额
     */
    private Long onlineRefundAmount;
    /**
     * 平台退款金额
     */
    private Long platformRefundAmount;
    /**
     * 平台预付金额
     */
    private Long platformPrepaymentAmount;

    /**
     * 医院优惠券支付
     */
    private Long hospitalCouponAmount;
    /**
     * 医院优惠券退款
     */
    private Long hospitalCouponRefundAmount;
    /**
     * 平台优惠券支付
     */
    private Long platformCouponAmount;
    /**
     * 平台优惠券退款
     */
    private Long platformCouponRefundAmount;
    /**
     * 渠道优惠券支付
     */
    private Long channelCouponAmount;
    /**
     *渠道优惠券退款
     */
    private Long channelCouponRefundAmount;
    /**
     * 医院线上支付
     */
    private Long hospitalOnlinePayAmount;
    /**
     * 医院线上退款
     */
    private Long hospitalOnlineRefundAmount;
    /**
     * 平台线上支付
     */
    private Long platformOnlinePayAmount;
    /**
     * 平台线上退款
     */
    private Long platformOnlineRefundAmount;
    /**
     * 渠道线上支付
     */
    private Long channelOnlinePayAmount;
    /**
     * 渠道线上退款
     */
    private Long channelOnlineRefundAmount;
    /**
     * 渠道单位母卡支付
     */
    private Long channelCompanyPayAmount;
    /**
     * 渠道单位母卡退款
     */
    private Long channelCompanyRefundAmount;
    /**
     * 渠道体检卡支付
     */
    private Long channelCardPayAmount;
    /**
     * 渠道体检卡退款
     */
    private Long channelCardRefundAmount;
    /**
     * 线下支付
     */
    private Long offlinePayAmount;
    /**
     * 收款记录流水号
     */
    private String paymentRecordSn;
    /**
     * 1=医院待确认 2=医院已确认 3=医院已撤销
     * @See
     */
    private Integer hospitalSettlementStatus;
    /**
     * 操作人id
     */
    private Integer operatorId;
    /**
     * 非DB字段
     */
    private String operatorName;

    /**
     * 渠道ID
     */
    private Integer channelId;
    /**
     * 渠道单位ID
     */
    private Integer channelCompanyId;
    /**
     * 结算视角 0=医院平台 1=平台渠道
     */
    private Integer settlementViewType;
    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isdeleted;
    /**
     * 数据插入时间
     */
    private Date gmtCreated;
    /**
     * 数据更新时间
     */
    private Date gmtModified;

    public Integer getPlatformCompanySign() {
        return platformCompanySign;
    }

    public void setPlatformCompanySign(Integer platformCompanySign) {
        this.platformCompanySign = platformCompanySign;
    }

    private Integer platformCompanySign;

    /**
     * 结算视角 0=医院平台 1=平台渠道
     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Integer getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(Integer isdeleted) {
        this.isdeleted = isdeleted;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
    }

    public Long getPlatformPrepaymentAmount() {
        return platformPrepaymentAmount;
    }

    public void setPlatformPrepaymentAmount(Long platformPrepaymentAmount) {
        this.platformPrepaymentAmount = platformPrepaymentAmount;
    }

    public String getPaymentRecordSn() {
        return paymentRecordSn;
    }

    public void setPaymentRecordSn(String paymentRecordSn) {
        this.paymentRecordSn = paymentRecordSn;
    }

    public Long getPlatformRefundAmount() {
        return platformRefundAmount;
    }

    public void setPlatformRefundAmount(Long platformRefundAmount) {
        this.platformRefundAmount = platformRefundAmount;
    }

    public Long getOnlineRefundAmount() {
        return onlineRefundAmount;
    }

    public void setOnlineRefundAmount(Long onlineRefundAmount) {
        this.onlineRefundAmount = onlineRefundAmount;
    }

    public Long getCompanyRefundAmount() {
        return companyRefundAmount;
    }

    public void setCompanyRefundAmount(Long companyRefundAmount) {
        this.companyRefundAmount = companyRefundAmount;
    }

    public Long getPlatformPayAmount() {
        return platformPayAmount;
    }

    public void setPlatformPayAmount(Long platformPayAmount) {
        this.platformPayAmount = platformPayAmount;
    }

    public Long getOnlinePayAmount() {
        return onlinePayAmount;
    }

    public void setOnlinePayAmount(Long onlinePayAmount) {
        this.onlinePayAmount = onlinePayAmount;
    }

    public Long getCompanyPayAmount() {
        return companyPayAmount;
    }

    public void setCompanyPayAmount(Long companyPayAmount) {
        this.companyPayAmount = companyPayAmount;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getHospitalCouponAmount() {
        return hospitalCouponAmount;
    }

    public void setHospitalCouponAmount(Long hospitalCouponAmount) {
        this.hospitalCouponAmount = hospitalCouponAmount;
    }

    public Long getHospitalCouponRefundAmount() {
        return hospitalCouponRefundAmount;
    }

    public void setHospitalCouponRefundAmount(Long hospitalCouponRefundAmount) {
        this.hospitalCouponRefundAmount = hospitalCouponRefundAmount;
    }

    public Long getPlatformCouponAmount() {
        return platformCouponAmount;
    }

    public void setPlatformCouponAmount(Long platformCouponAmount) {
        this.platformCouponAmount = platformCouponAmount;
    }

    public Long getPlatformCouponRefundAmount() {
        return platformCouponRefundAmount;
    }

    public void setPlatformCouponRefundAmount(Long platformCouponRefundAmount) {
        this.platformCouponRefundAmount = platformCouponRefundAmount;
    }

    public Long getChannelCouponAmount() {
        return channelCouponAmount;
    }

    public void setChannelCouponAmount(Long channelCouponAmount) {
        this.channelCouponAmount = channelCouponAmount;
    }

    public Long getChannelCouponRefundAmount() {
        return channelCouponRefundAmount;
    }

    public void setChannelCouponRefundAmount(Long channelCouponRefundAmount) {
        this.channelCouponRefundAmount = channelCouponRefundAmount;
    }

    public Long getHospitalOnlinePayAmount() {
        return hospitalOnlinePayAmount;
    }

    public void setHospitalOnlinePayAmount(Long hospitalOnlinePayAmount) {
        this.hospitalOnlinePayAmount = hospitalOnlinePayAmount;
    }

    public Long getHospitalOnlineRefundAmount() {
        return hospitalOnlineRefundAmount;
    }

    public void setHospitalOnlineRefundAmount(Long hospitalOnlineRefundAmount) {
        this.hospitalOnlineRefundAmount = hospitalOnlineRefundAmount;
    }

    public Long getPlatformOnlinePayAmount() {
        return platformOnlinePayAmount;
    }

    public void setPlatformOnlinePayAmount(Long platformOnlinePayAmount) {
        this.platformOnlinePayAmount = platformOnlinePayAmount;
    }

    public Long getPlatformOnlineRefundAmount() {
        return platformOnlineRefundAmount;
    }

    public void setPlatformOnlineRefundAmount(Long platformOnlineRefundAmount) {
        this.platformOnlineRefundAmount = platformOnlineRefundAmount;
    }

    public Long getChannelOnlinePayAmount() {
        return channelOnlinePayAmount;
    }

    public void setChannelOnlinePayAmount(Long channelOnlinePayAmount) {
        this.channelOnlinePayAmount = channelOnlinePayAmount;
    }

    public Long getChannelOnlineRefundAmount() {
        return channelOnlineRefundAmount;
    }

    public void setChannelOnlineRefundAmount(Long channelOnlineRefundAmount) {
        this.channelOnlineRefundAmount = channelOnlineRefundAmount;
    }

    public Long getOfflinePayAmount() {
        return offlinePayAmount;
    }

    public void setOfflinePayAmount(Long offlinePayAmount) {
        this.offlinePayAmount = offlinePayAmount;
    }

    public Long getChannelCompanyPayAmount() {
        return channelCompanyPayAmount;
    }

    public void setChannelCompanyPayAmount(Long channelCompanyPayAmount) {
        this.channelCompanyPayAmount = channelCompanyPayAmount;
    }

    public Long getChannelCompanyRefundAmount() {
        return channelCompanyRefundAmount;
    }

    public void setChannelCompanyRefundAmount(Long channelCompanyRefundAmount) {
        this.channelCompanyRefundAmount = channelCompanyRefundAmount;
    }

    public Long getChannelCardPayAmount() {
        return channelCardPayAmount;
    }

    public void setChannelCardPayAmount(Long channelCardPayAmount) {
        this.channelCardPayAmount = channelCardPayAmount;
    }

    public Long getChannelCardRefundAmount() {
        return channelCardRefundAmount;
    }

    public void setChannelCardRefundAmount(Long channelCardRefundAmount) {
        this.channelCardRefundAmount = channelCardRefundAmount;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getChannelCompanyId() {
        return channelCompanyId;
    }

    public void setChannelCompanyId(Integer channelCompanyId) {
        this.channelCompanyId = channelCompanyId;
    }

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }
}
