package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.tijiantest.util.pagination.Page;

/**
 * Created by wangzhongxing on 2017/8/3.
 */
public class UnsettlementOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 医院id
     */
    private Integer hospitalId;

    /**
     * 单位id集合
     */
    private List<Integer> examCompanyIds;
    /**
     * 结算开启时间
     */
    private Date settlementOpenTime;
    /**
     * 下单起始时间
     */
    private String placeOrderStartTime;
    /**
     * 下单终止时间
     */
    private String placeOrderEndTime;
    /**
     * 体检起始时间
     */
    private String examStartDate;
    /**
     * 体检终止时间
     */
    private String examEndDate;
    /**
     * 客户经理id
     */
    private Integer orderManagerId;
    /**
     * 订单状态位
     */
    private List<Integer> status;
    /**
     * 体检人身份证或姓名
     */
    private String idCardOrAccountName;
    /**
     * 订单金额是否只包含线上付款（用于涮选）
     */
    private  Boolean containOnline;

    private String batchSn;
    /**
     * 分页信息
     */
    private Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getIdCardOrAccountName() {
        return idCardOrAccountName;
    }

    public void setIdCardOrAccountName(String idCardOrAccountName) {
        this.idCardOrAccountName = idCardOrAccountName;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public Integer getOrderManagerId() {
        return orderManagerId;
    }

    public void setOrderManagerId(Integer orderManagerId) {
        this.orderManagerId = orderManagerId;
    }

    public String getExamEndDate() {
        return examEndDate;
    }

    public void setExamEndDate(String examEndDate) {
        this.examEndDate = examEndDate;
    }

    public String getExamStartDate() {
        return examStartDate;
    }

    public void setExamStartDate(String examStartDate) {
        this.examStartDate = examStartDate;
    }

    public String getPlaceOrderEndTime() {
        return placeOrderEndTime;
    }

    public void setPlaceOrderEndTime(String placeOrderEndTime) {
        this.placeOrderEndTime = placeOrderEndTime;
    }

    public String getPlaceOrderStartTime() {
        return placeOrderStartTime;
    }

    public void setPlaceOrderStartTime(String placeOrderStartTime) {
        this.placeOrderStartTime = placeOrderStartTime;
    }

    public Date getSettlementOpenTime() {
        return settlementOpenTime;
    }

    public void setSettlementOpenTime(Date settlementOpenTime) {
        this.settlementOpenTime = settlementOpenTime;
    }

    public List<Integer> getExamCompanyIds() {
        return examCompanyIds;
    }

    public void setExamCompanyIds(List<Integer> examCompanyIds) {
        this.examCompanyIds = examCompanyIds;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Boolean getContainOnline() {
        return containOnline;
    }

    public void setContainOnline(Boolean containOnline) {
        this.containOnline = containOnline;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }
}
