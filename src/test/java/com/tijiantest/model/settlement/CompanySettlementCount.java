package com.tijiantest.model.settlement;

import java.io.Serializable;

/**
 * Created by wangzhongxing on 2017/8/2.
 */
public class CompanySettlementCount implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 单位id
     */
    private Integer id;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 未结算体检订单数
     */
    private Integer orderNum;


    /**
     * 未结算收款订单数
     */
    private Integer paymentOrderNum;
    /**
     * 未结算卡数
     */
    private Integer cardNum;
    /**
     * 未结算退款数
     */
    private Integer refundNum;
    /**
     * 未结算特殊退款数
     */
    private Integer prepayNum;
    
    /**
     * 机构类型
     */
    private Integer organiztionType;
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Integer getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(Integer refundNum) {
        this.refundNum = refundNum;
    }

    public Integer getPrepayNum() {
        return prepayNum;
    }

    public void setPrepayNum(Integer prepayNum) {
        this.prepayNum = prepayNum;
    }

	public Integer getOrganiztionType() {
		return organiztionType;
	}

	public void setOrganiztionType(Integer organiztionType) {
		this.organiztionType = organiztionType;
	}

    public Integer getPaymentOrderNum() {
        return paymentOrderNum;
    }

    public void setPaymentOrderNum(Integer paymentOrderNum) {
        this.paymentOrderNum = paymentOrderNum;
    }
}
