package com.tijiantest.model.settlement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 医院与平台账单
 */
public class TradeHospitalPlatformBill implements Serializable {

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
     * 单位名称，非DB字段
     */
    private String companyName;

    /**
     * 单位类型，非DB字段
     */
    private String companyType;

    /**
     * 平台折扣，非DB字段
     */
    private Double platformDiscount;

    /**
     * 平台支付金额
     */
    private Long platformPayAmount;

    /**
     * 平台退款金额
     */
    private Long platformRefundAmount;

    /**
     * 平台预付金额
     */
    private Long platformPrepaymentAmount;

    /**
     * 应收平台金额
     */
    private Long platformChargedAmount;

    /**
     * 平台实付医院金额
     */
    private Long platformActurallyPayAmount;
    
    /**
     * 折后应付
     */
    private Long discountAmount;

    /**
     * 消费额度
     */
    private Long consumeQuotaAmount;
    

    /**
     * 审核备注
     */
    private String remark;

    /**
     * 收款记录流水号
     */
    private String paymentRecordSn;

    /**
     * 平台审核状态 1=待医院确认  2=平台审核中（医院已确认） 3=医院已撤销 4=平台审核通过 5=平台完成收款
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
     * 结算视角(0 医院;1 渠道)
     */
    private Integer settlementViewType;
    
    /**
     * 流转日志
     */
    private List<TradeCommonLogResultDTO> circulationLog = new ArrayList<>();


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

    public Long getPlatformPayAmount() {
        return platformPayAmount;
    }

    public void setPlatformPayAmount(Long platformPayAmount) {
        this.platformPayAmount = platformPayAmount;
    }

    public Long getPlatformRefundAmount() {
        return platformRefundAmount;
    }

    public void setPlatformRefundAmount(Long platformRefundAmount) {
        this.platformRefundAmount = platformRefundAmount;
    }

    public Long getPlatformPrepaymentAmount() {
        return platformPrepaymentAmount;
    }

    public void setPlatformPrepaymentAmount(Long platformPrepaymentAmount) {
        this.platformPrepaymentAmount = platformPrepaymentAmount;
    }

    public Long getPlatformChargedAmount() {
        return platformChargedAmount;
    }

    public void setPlatformChargedAmount(Long platformChargedAmount) {
        this.platformChargedAmount = platformChargedAmount;
    }

    public Long getPlatformActurallyPayAmount() {
        return platformActurallyPayAmount;
    }

    public void setPlatformActurallyPayAmount(Long platformActurallyPayAmount) {
        this.platformActurallyPayAmount = platformActurallyPayAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Double getPlatformDiscount() {
        return platformDiscount;
    }

    public void setPlatformDiscount(Double platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

	public Long getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Long discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Long getConsumeQuotaAmount() {
		return consumeQuotaAmount;
	}

	public void setConsumeQuotaAmount(Long consumeQuotaAmount) {
		this.consumeQuotaAmount = consumeQuotaAmount;
	}

	public List<TradeCommonLogResultDTO> getCirculationLog() {
		return circulationLog;
	}

	public void setCirculationLog(List<TradeCommonLogResultDTO> circulationLog) {
		this.circulationLog = circulationLog;
	}

    public Integer getSettlementViewType() {
        return settlementViewType;
    }

    public void setSettlementViewType(Integer settlementViewType) {
        this.settlementViewType = settlementViewType;
    }
}
