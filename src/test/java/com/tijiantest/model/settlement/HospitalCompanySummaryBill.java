package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.List;

/**
 * 医院与单位汇总账单
 */
public class HospitalCompanySummaryBill implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7780672414213898340L;

	/**
     * 单位名称
     */
    private String companyName;

    /**
     * 收款总额
     */
    private Long totalPayment;

    /**
     * 医院与单位账单列表
     */
    private List<TradeHospitalCompanyBill> tradeHospitalCompanyBillList;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<TradeHospitalCompanyBill> getTradeHospitalCompanyBillList() {
        return tradeHospitalCompanyBillList;
    }

    public void setTradeHospitalCompanyBillList(List<TradeHospitalCompanyBill> tradeHospitalCompanyBillList) {
        this.tradeHospitalCompanyBillList = tradeHospitalCompanyBillList;
    }
}
