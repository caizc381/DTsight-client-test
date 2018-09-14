package com.tijiantest.model.settlement;

import java.io.Serializable;

public class TradeReviewBillDTO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8657691961678718266L;

	private String sn;

    private Long platformActurallyPayAmount;

    private String remark;

    private Long discountAmount;

    private Long consumeQuotaAmount;
    private Integer operatorId;

    private String operatorName;


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }


    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

}
