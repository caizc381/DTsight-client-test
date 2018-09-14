package com.tijiantest.model.settlement;

import org.testng.reporters.jq.INavigatorPanel;

import java.io.Serializable;
import java.util.Date;

/**
 * 医院与单位账单
 */
public class TradeHospitalCompanyBill implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;

    /**
     * 账单流水号
     */
    private String sn;

    /**
     * 结算批次号
     */
    private String batchSn;

    /**
     * 体检机构id
     */
    private Integer hospitalId;

    /**
     * 体检单位id
     */
    private Integer companyId;
    
    /**
     * 单位名称
     */
    private String companyName;
    

    /**
     * 单位支付金额
     */
    private Long companyPayAmount;

    /**
     * 单位退款金额
     */
    private Long companyRefundAmount;

    /**
     * 应收单位金额
     */
    private Long companyChargedAmount;

    /**
     * 收款记录流水号
     */
    private String paymentRecordSn;

    /**
     * 医院审核状态  1=医院待确认 2=医院已确认 3=医院已撤销 4=医院完成收款
     */
    private Integer status;

    /**
     * 操作人id
     */
    private Integer operatorId;

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
    /**
     * '-1 默认，0 医院单位账单，2 医院优惠券账单，3 医院线上支付账单，4 医院线下支付账单 5 平台优惠券 6 渠道线上 7 渠道单位 8 渠道优惠券 9 医院体检卡 10 渠道体检卡'
     */
    private int type;
    /**
     * 结算视角(0 医院;1 渠道)
     */
    private Integer settlementViewType;



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

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyPayAmount() {
        return companyPayAmount;
    }

    public void setCompanyPayAmount(Long companyPayAmount) {
        this.companyPayAmount = companyPayAmount;
    }

    public Long getCompanyRefundAmount() {
        return companyRefundAmount;
    }

    public void setCompanyRefundAmount(Long companyRefundAmount) {
        this.companyRefundAmount = companyRefundAmount;
    }

    public Long getCompanyChargedAmount() {
        return companyChargedAmount;
    }

    public void setCompanyChargedAmount(Long companyChargedAmount) {
        this.companyChargedAmount = companyChargedAmount;
    }

    public String getPaymentRecordSn() {
        return paymentRecordSn;
    }

    public void setPaymentRecordSn(String paymentRecordSn) {
        this.paymentRecordSn = paymentRecordSn;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }
}
