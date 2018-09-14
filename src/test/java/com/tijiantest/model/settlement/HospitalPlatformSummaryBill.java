package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.List;

/**
 * 医院与平台汇总账单
 */
public class HospitalPlatformSummaryBill implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6347397092771214522L;
	private Integer organizationId;
    /**
     * 单位名称
     */
    private String organizationName;

    /**
     * 实付总额
     */
    private Long totalPayment;

    /**
     * 应付总额
     */
    private Long payableTotalAmount;

    /**
     * 消费额度总额
     */
    private Long totalConsumeQuotaAmount;

    /**
     * 折后应付总额
     */
    private Long totalDiscountAmount;
    
    
	/**
     * 医院消费额度
     */
    private Long hospitalConsumeQuotaAmount;

    /**
     * 医院与平台账单列表
     */
    private List<TradeHospitalPlatformBill> tradeHospitalPlatformBillList;
    

    public Long getPayableTotalAmount() {
		return payableTotalAmount;
	}

	public void setPayableTotalAmount(Long payableTotalAmount) {
		this.payableTotalAmount = payableTotalAmount;
	}

	public Long getTotalConsumeQuotaAmount() {
		return totalConsumeQuotaAmount;
	}

	public void setTotalConsumeQuotaAmount(Long totalConsumeQuotaAmount) {
		this.totalConsumeQuotaAmount = totalConsumeQuotaAmount;
	}

	public Long getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(Long totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public Long getHospitalConsumeQuotaAmount() {
		return hospitalConsumeQuotaAmount;
	}

	public void setHospitalConsumeQuotaAmount(Long hospitalConsumeQuotaAmount) {
		this.hospitalConsumeQuotaAmount = hospitalConsumeQuotaAmount;
	}



    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<TradeHospitalPlatformBill> getTradeHospitalPlatformBillList() {
        return tradeHospitalPlatformBillList;
    }

    public void setTradeHospitalPlatformBillList(List<TradeHospitalPlatformBill> tradeHospitalPlatformBillList) {
        this.tradeHospitalPlatformBillList = tradeHospitalPlatformBillList;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}
