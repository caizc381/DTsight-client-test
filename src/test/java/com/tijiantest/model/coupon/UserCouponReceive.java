package com.tijiantest.model.coupon;

import java.io.Serializable;
import java.util.Date;

public class UserCouponReceive implements Serializable {

    /**
     * 用户领取优惠券id
     */
    private int id;
    /**
     * 模板批次号
     */
    private String templateBatchNum;
    /**
     * 机构ID
     */
    private  Integer organizationId;
    /**
     * 用户ID
     */
    private  Integer accountId;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 领取人（冗余）
     */
    private String accountName;
    /**
     * 身份证号（冗余）
     */
    private String idCard;
    /**
     * 券的状态，是否使用,0：未使用，1：已使用，2：已过期
     */
    private Integer status;
    /**
     * 使用时间
     */
    private Date usedDateTime;
    /**
     * 到期时间
     */
    private Date endTime;
    /**
     * 是否删除,0：否，1：是
     */
    private int isDeleted;
    /**
     * 创建时间
     */
    private Date gmtCreated;
    /**
     * 更新时间
     */
    private Date gmtModifyed;

    /**
     * 领取时间
     */
    private Date receiveTime;
    /**
     * 使用时间
     */
    private Date useTime;
    /**
     * 领取的优惠券对应的优惠券模板
     */
    private CouponTemplate couponTemplate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateBatchNum() {
        return templateBatchNum;
    }

    public void setTemplateBatchNum(String templateBatchNum) {
        this.templateBatchNum = templateBatchNum;
    }




    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUsedDateTime() {
        return usedDateTime;
    }

    public void setUsedDateTime(Date usedDateTime) {
        this.usedDateTime = usedDateTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModifyed() {
        return gmtModifyed;
    }

    public void setGmtModifyed(Date gmtModifyed) {
        this.gmtModifyed = gmtModifyed;
    }

    public void setCouponTemplate(CouponTemplate couponTemplate) {
        this.couponTemplate = couponTemplate;
    }

    public CouponTemplate getCouponTemplate() {
        return couponTemplate;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }
}
