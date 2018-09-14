package com.tijiantest.model.settlement;


import java.io.Serializable;
import java.util.List;

/**
 * Created by wangzhongxing on 2017/9/12.
 */
public class UnsettlementRefundNumDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer hospitalId;

    private List<String> orderNums;

    private List<String> payOrderNums;

    private List<Integer> hospitalCompanyIds;

    private String batchSn;

    private String placeOrderEndTime;

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

    public List<Integer> getHospitalCompanyIds() {
        return hospitalCompanyIds;
    }

    public void setHospitalCompanyIds(List<Integer> hospitalCompanyIds) {
        this.hospitalCompanyIds = hospitalCompanyIds;
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
}