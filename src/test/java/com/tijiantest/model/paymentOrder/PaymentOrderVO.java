package com.tijiantest.model.paymentOrder;

import com.tijiantest.model.settlement.TradeCommonLogResultDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentOrderVO{

    private Integer id;
    private String orderNum;
    private Integer hospitalId;
    private Integer companyId;
    private String name;
    private Integer status;
    private Long amount;
    private Long refundAmount;
    private Integer managerId;
    private String managerName;
    private String remark;
    private String hospitalRemark;
    private Integer settlementStatus;
    private String settlementBatchSn;
    private Date createTime;
    private String hospitalSettlementStatus;
    private List<TradeCommonLogResultDTO> circulationLog = new ArrayList();
    private String hospitalName;
    private String companyName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHospitalRemark() {
        return hospitalRemark;
    }

    public void setHospitalRemark(String hospitalRemark) {
        this.hospitalRemark = hospitalRemark;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getSettlementBatchSn() {
        return settlementBatchSn;
    }

    public void setSettlementBatchSn(String settlementBatchSn) {
        this.settlementBatchSn = settlementBatchSn;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    public void setHospitalSettlementStatus(String hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
    }

    public List<TradeCommonLogResultDTO> getCirculationLog() {
        return circulationLog;
    }

    public void setCirculationLog(List<TradeCommonLogResultDTO> circulationLog) {
        this.circulationLog = circulationLog;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
