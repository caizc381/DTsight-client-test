package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TradeSettlementPayRecord implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -225197578082988759L;

	private Integer id;

    /**
     * 收款记录流水号
     */
    private String sn;

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
     * 操作人Id
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 应付金额
     */
    private Long payableAmount;

    

    private Long totalDiscountAmount;

    private Long totalConsumeQuotaAmount;
    /**
     * 实付金额
     */
    private Long realAmount;

    /**
     * 付款时间
     */
    private Date paymentTime;

    /**
     * 付款凭证
     */
    private String certificate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 付款类型:0 单位收款，1 平台付款
     */
    private Integer type;

    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

    /**
     * 医院与单位账单列表
     */
    private List<TradeHospitalCompanyBill> tradeHospitalCompanyBillList;

    /**
     * 医院与平台账单列表
     */
    private List<TradeHospitalPlatformBill> tradeHospitalPlatformBillList;

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

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Long getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Long payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Long getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(Long realAmount) {
        this.realAmount = realAmount;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public List<TradeHospitalCompanyBill> getTradeHospitalCompanyBillList() {
        return tradeHospitalCompanyBillList;
    }

    public void setTradeHospitalCompanyBillList(List<TradeHospitalCompanyBill> tradeHospitalCompanyBillList) {
        this.tradeHospitalCompanyBillList = tradeHospitalCompanyBillList;
    }

    public List<TradeHospitalPlatformBill> getTradeHospitalPlatformBillList() {
        return tradeHospitalPlatformBillList;
    }

    public void setTradeHospitalPlatformBillList(List<TradeHospitalPlatformBill> tradeHospitalPlatformBillList) {
        this.tradeHospitalPlatformBillList = tradeHospitalPlatformBillList;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

	public Long getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(Long totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public Long getTotalConsumeQuotaAmount() {
		return totalConsumeQuotaAmount;
	}

	public void setTotalConsumeQuotaAmount(Long totalConsumeQuotaAmount) {
		this.totalConsumeQuotaAmount = totalConsumeQuotaAmount;
	}
}
