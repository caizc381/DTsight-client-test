package com.tijiantest.model.examitempackage;

import java.util.Date;

public class AccountRiskItem {
    private Integer id;
    private Date    createTime;
    private Date    updateTime;
    /**
     * 评估报告
     */
    private Integer evaluateReportId;
    /**
     * 用户ID
     */
    private Integer accountId;
    /**
     * 风险名称
     */
    private Integer riskId;
    /**
     * 风险名称
     */
    private String  riskName;
    /**
     * 系数
     */
    private Double  coefficient;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRiskName() {
        return riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Integer getEvaluateReportId() {
        return evaluateReportId;
    }

    public void setEvaluateReportId(Integer evaluateReportId) {
        this.evaluateReportId = evaluateReportId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getRiskId() {
        return riskId;
    }

    public void setRiskId(Integer riskId) {
        this.riskId = riskId;
    }

}

