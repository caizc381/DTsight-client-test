package com.tijiantest.model.coupon;

import java.io.Serializable;
import java.util.Date;

public class CouponTemplate implements Serializable{

    /**
     * 券ID
     */
    private int id;
    /**
     * 批次号
     */
    private String batchNum;
    /**
     *  优惠券名称
     */
    private String name;

    /**
     * 发券数量
     */
    private int quantity;

    /**
     * 券价格
     */
    private int price;

    /**
     * 优惠券状态:1,新建 2，发放  3,停用
     */
    private int status;
    /**
     * 每人限领取，-1表示不限制领取数量
     *
     */
    private int receiveLimitNumber;
    /**
     * 最低消费，不限制最低消费则该值为0
     */
    private int minLimitPrice;
    /**
     * 优惠券说明
     */
    private String description;

    /**
     * 券使用起始时间
     */
    private Date startTime;
    /**
     * 券使用结束时间
     */
    private Date endTime;

    /**
     * 是否回收: true 回收， false: 没有回收
     */
    private boolean recovery;
    /**
     * 发券人
     */
    private int operatorId;
    /**
     * 券所属人
     */
    private int ownerId;
    /**
     * 券所属名称 避免根据名字模糊查询要查tb_account表
     */
    private String ownerName;
    /**
     * 券来源  0,crm 1,ops
     */
    private int source;
    /**
     * 机构类型：1：医院 2:渠道
     */
    private int fromSiteOrgType;
    /**
     * 机构ID
     */
    private int organizationId;
    /**
     * 是否删除,0：否，1：是
     */
    private int isDeleted;
    /**
     *创建时间
     */
    private Date gmtCreated;
    /**
     *更新时间
     */
    private Date gmtModifyed;
    /**
     *版本号
     */
    private int version;

    private int usedNumber;

    private int receivedNumber;
    private int limitType;

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReceiveLimitNumber() {
        return receiveLimitNumber;
    }

    public void setReceiveLimitNumber(int receiveLimitNumber) {
        this.receiveLimitNumber = receiveLimitNumber;
    }

    public int getMinLimitPrice() {
        return minLimitPrice;
    }

    public void setMinLimitPrice(int minLimitPrice) {
        this.minLimitPrice = minLimitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getFromSiteOrgType() {
        return fromSiteOrgType;
    }

    public void setFromSiteOrgType(int fromSiteOrgType) {
        this.fromSiteOrgType = fromSiteOrgType;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isRecovery() {
        return recovery;
    }

    public void setRecovery(boolean recovery) {
        this.recovery = recovery;
    }

    public int getUsedNumber() {
        return usedNumber;
    }

    public void setUsedNumber(int usedNumber) {
        this.usedNumber = usedNumber;
    }

    public int getReceivedNumber() {
        return receivedNumber;
    }

    public void setReceivedNumber(int receivedNumber) {
        this.receivedNumber = receivedNumber;
    }
}
