package com.tijiantest.model.settlement;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangzhongxing on 2017/8/10.
 */
public class TradeSettlementCard implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer id;
    /***
     * 结算体检卡号
     */
    private String sn;
    /***
     * 体检卡id
     */
    private Integer refCardId;
    /***
     * 体检机构id
     */
    private Integer organizationId;
    /***
     * 体检单位id
     */
    private Integer companyId;
    /***
     * 结算批次号
     */
    private String batchSn;
    /***
     * 医院平台账单流水号
     */
    private String hospitalPlatformSn;
    /***
     * 医院单位账单流水号
     */
    private String hospitalCompanySn;
    /***
     * 结算金额
     */
    private Long settlementAmount;
    /***
     * 结算回收金额
     */
    private Long recycleAmount;
    /***
     * 结算方式，0=按项目，1=按人数
     * @see com.mytijian.card.enums.CardSettlementEnum
     */
    private Integer settlementMode;
    /***
     * 1=医院待确认 2=医院已撤销 3=医院已确认
     * @see com.mytijian.card.enums.CardSettlementEnum
     */
    private Integer hospitalSettlementStatus;
    /**
     * 0=数据有效  1=数据无效
     */
    private Integer isDeleted;
    /**
     * 数据插入时间
     */
    private Date gmtCreated;
    /**
     * 数据更新时间
     */
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getRefCardId() {
        return refCardId;
    }

    public void setRefCardId(Integer refCardId) {
        this.refCardId = refCardId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getBatchSn() {
        return batchSn;
    }

    public void setBatchSn(String batchSn) {
        this.batchSn = batchSn;
    }

    public String getHospitalPlatformSn() {
        return hospitalPlatformSn;
    }

    public void setHospitalPlatformSn(String hospitalPlatformSn) {
        this.hospitalPlatformSn = hospitalPlatformSn;
    }

    public String getHospitalCompanySn() {
        return hospitalCompanySn;
    }

    public void setHospitalCompanySn(String hospitalCompanySn) {
        this.hospitalCompanySn = hospitalCompanySn;
    }

    public Long getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(Long settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public Long getRecycleAmount() {
        return recycleAmount;
    }

    public void setRecycleAmount(Long recycleAmount) {
        this.recycleAmount = recycleAmount;
    }

    public Integer getSettlementMode() {
        return settlementMode;
    }

    public void setSettlementMode(Integer settlementMode) {
        this.settlementMode = settlementMode;
    }

    public Integer getHospitalSettlementStatus() {
        return hospitalSettlementStatus;
    }

    public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
        this.hospitalSettlementStatus = hospitalSettlementStatus;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
