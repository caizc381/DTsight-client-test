package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wangzhongxing on 2017/8/7.
 */
public class SettlementRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer hospitalId;

    private List<String> orderNums;

    /**
     * 付款订单
     */
    private List<String> payOrderNums;

    private List<Integer> cardIds;

    private List<Integer> companyIds;
    /**
     * 当前登陆人id
     */
    private Integer operatorId;

    private String batchSn;

    private String placeOrderEndTime;
    /**
     * 退款数量
     */
    private int refundSize;

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public List<String> getOrderNums() {
        return orderNums;
    }

    public void setOrderNums(List<String> orderNums) {
        this.orderNums = orderNums;
    }

    public List<Integer> getCardIds() {
        return cardIds;
    }

    public void setCardIds(List<Integer> cardIds) {
        this.cardIds = cardIds;
    }

    public List<Integer> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Integer> companyIds) {
        this.companyIds = companyIds;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public List<String> getPayOrderNums() {
        return payOrderNums;
    }

    public void setPayOrderNums(List<String> payOrderNums) {
        this.payOrderNums = payOrderNums;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }

    public String getPlaceOrderEndTime() {
        return placeOrderEndTime;
    }

    public void setPlaceOrderEndTime(String placeOrderEndTime) {
        this.placeOrderEndTime = placeOrderEndTime;
    }

    public int getRefundSize() {
        return refundSize;
    }

    public void setRefundSize(int refundSize) {
        this.refundSize = refundSize;
    }
}
