package com.tijiantest.model.payment.trade;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangzhongxing on 2017/5/18.
 */
public class TradeRefundRecord implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 自增主键
     */
    private Integer id;
    /**
     * 退款流水号
     */
    private String sn;
    /**
     * 交易订单号
     */
    private String tradeOrderNum;
    /**
     * 支付流水号
     */
    private String refPaySn;
    /**
     * 订单编号，对应Order.orderNum
     */
    private String refOrderNum;
    /**
     * 订单金额变更唯一标识，对应Order类里面的OrderMealSnapshot里面的id
     */
    private String refOrderNumVersion;
    /**
     * 订单类型
     */
    private Integer refOrderType;
    /**
     * 交易配置(tb_paymethod_config 表)id
     */
    private Integer tradeMethodConfigId;
    /**
     * 交易方式 卡、余额、支付宝、微信、线下支付
     */
    private Integer tradeMethodType;
    /**
     * 退款状态 1:成功, 2:失败，参考RefundConstants.RefundStatus
     */
    private Integer refundStatus;
    /**
     * 退款金额
     */
    private Long refundAmount;
    /**
     * 交易子账户类型
     */
    private Integer refundTradeSubAccountType;
    /**
     * 退款人总账户
     */
    private Integer refundTradeAccountId;
    /**
     * 退款人子账户
     */
    private Integer refundTradeSubAccountId;
    /**
     * 退款账户快照
     */
    private String refundTradeAccountSnap;
    /**
     * 收款子账户类型
     */
    private Integer receiveTradeSubAccountType;
    /**
     * 收款总账户
     */
    private Integer receiveTradeAccountId;
    /**
     * 收款子账户
     */
    private Integer receiveTradeSubAccountId;
    /**
     * 收款人快照
     */
    private String receiveTradeAccountSnap;
    /**
     * 提交到第三方支付的mytijian订单号，与sn（支付流水号相同）
     */
    private String outOrderId;
    /**
     * 渠道交易单号，第三方支付平台的流水号
     */
    private String credentials;
    /**
     * 备注信息
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date gmtCreated;
    /**
     * 修改时间
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

    public String getTradeOrderNum() {
        return tradeOrderNum;
    }

    public void setTradeOrderNum(String tradeOrderNum) {
        this.tradeOrderNum = tradeOrderNum;
    }

    public String getRefPaySn() {
        return refPaySn;
    }

    public void setRefPaySn(String refPaySn) {
        this.refPaySn = refPaySn;
    }

    public String getRefOrderNum() {
        return refOrderNum;
    }

    public void setRefOrderNum(String refOrderNum) {
        this.refOrderNum = refOrderNum;
    }

    public String getRefOrderNumVersion() {
        return refOrderNumVersion;
    }

    public void setRefOrderNumVersion(String refOrderNumVersion) {
        this.refOrderNumVersion = refOrderNumVersion;
    }

    public Integer getRefOrderType() {
        return refOrderType;
    }

    public void setRefOrderType(Integer refOrderType) {
        this.refOrderType = refOrderType;
    }

    public Integer getTradeMethodConfigId() {
        return tradeMethodConfigId;
    }

    public void setTradeMethodConfigId(Integer tradeMethodConfigId) {
        this.tradeMethodConfigId = tradeMethodConfigId;
    }

    public Integer getTradeMethodType() {
        return tradeMethodType;
    }

    public void setTradeMethodType(Integer tradeMethodType) {
        this.tradeMethodType = tradeMethodType;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Integer getRefundTradeSubAccountType() {
        return refundTradeSubAccountType;
    }

    public void setRefundTradeSubAccountType(Integer refundTradeSubAccountType) {
        this.refundTradeSubAccountType = refundTradeSubAccountType;
    }

    public Integer getRefundTradeAccountId() {
        return refundTradeAccountId;
    }

    public void setRefundTradeAccountId(Integer refundTradeAccountId) {
        this.refundTradeAccountId = refundTradeAccountId;
    }

    public Integer getRefundTradeSubAccountId() {
        return refundTradeSubAccountId;
    }

    public void setRefundTradeSubAccountId(Integer refundTradeSubAccountId) {
        this.refundTradeSubAccountId = refundTradeSubAccountId;
    }

    public String getRefundTradeAccountSnap() {
        return refundTradeAccountSnap;
    }

    public void setRefundTradeAccountSnap(String refundTradeAccountSnap) {
        this.refundTradeAccountSnap = refundTradeAccountSnap;
    }

    public Integer getReceiveTradeSubAccountType() {
        return receiveTradeSubAccountType;
    }

    public void setReceiveTradeSubAccountType(Integer receiveTradeSubAccountType) {
        this.receiveTradeSubAccountType = receiveTradeSubAccountType;
    }

    public Integer getReceiveTradeAccountId() {
        return receiveTradeAccountId;
    }

    public void setReceiveTradeAccountId(Integer receiveTradeAccountId) {
        this.receiveTradeAccountId = receiveTradeAccountId;
    }

    public Integer getReceiveTradeSubAccountId() {
        return receiveTradeSubAccountId;
    }

    public void setReceiveTradeSubAccountId(Integer receiveTradeSubAccountId) {
        this.receiveTradeSubAccountId = receiveTradeSubAccountId;
    }

    public String getReceiveTradeAccountSnap() {
        return receiveTradeAccountSnap;
    }

    public void setReceiveTradeAccountSnap(String receiveTradeAccountSnap) {
        this.receiveTradeAccountSnap = receiveTradeAccountSnap;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
