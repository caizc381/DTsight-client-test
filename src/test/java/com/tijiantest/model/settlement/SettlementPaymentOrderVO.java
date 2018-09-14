package com.tijiantest.model.settlement;


import com.tijiantest.util.pagination.Page;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SettlementPaymentOrderVO{
    /**
     * 单位列表
     */
    private List<Integer> hospitalCompanyIdList;
    /**
     * 医院id
     */
    private Integer hospitalId;
    /**
     * 结算生成的开始时间
     */
    private String settlementStartDate;
    /**
     * 结算生成的结束时间
     */
    private String settlementEndDate;

    /**
     * 付款人
     */
    private String paymentName;
    /**
     * 批次号
     */
    private String batchSn;

    /**
     * 分页信息
     */
    private Page page;

    public List<Integer> getHospitalCompanyIdList() {
        return hospitalCompanyIdList;
    }

    public void setHospitalCompanyIdList(List<Integer> hospitalCompanyIdList) {
        this.hospitalCompanyIdList = hospitalCompanyIdList;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getSettlementStartDate() {
        return settlementStartDate;
    }

    public void setSettlementStartDate(String settlementStartDate) {
        this.settlementStartDate = settlementStartDate;
    }

    public String getSettlementEndDate() {
        return settlementEndDate;
    }

    public void setSettlementEndDate(String settlementEndDate) {
        this.settlementEndDate = settlementEndDate;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }
}
