package com.tijiantest.model.coupon;


import java.io.Serializable;
import java.util.Date;

public class CouponTemplateResult implements Serializable {
    private String batchNum;
    private String name;
    // 券面值
    private Long price;
    // 发放数量
    private Integer quantity;
    // 领取限制
    private Integer receiveLimitNumber;
    // 最低消费
    private Integer minLimitPrice;
    private String descrpiton;
    // 结束时间
    private Date startTime;
    // 开始时间
    private Date endTime;
    // 优惠券来源
    //@See CouponTemplateSourceEnum
    private Integer source;
    private String ownerName;
    // 券所属人
    private Integer ownerId;
    // 已经使用的数量
    private Integer usedNumber;
    // 已经领取的数量
    private Integer receivedNumber;
    //@see CouponTemplateStatusEnum
    private Integer status;
    private Integer operatorId;
    private Integer organizationId;
    private Boolean recovery;
    private Integer version;
    private Date updateTime;
    private Date createTime;
    // 体检中心 |渠道  OrganizationTypeEnum
    private Integer fromSiteOrgType;

    public Integer getFromSiteOrgType() {
        return fromSiteOrgType;
    }

    public void setFromSiteOrgType(Integer fromSiteOrgType) {
        this.fromSiteOrgType = fromSiteOrgType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getRecovery() {
        return recovery;
    }

    public void setRecovery(Boolean recovery) {
        this.recovery = recovery;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReceiveLimitNumber() {
        return receiveLimitNumber;
    }

    public void setReceiveLimitNumber(Integer receiveLimitNumber) {
        this.receiveLimitNumber = receiveLimitNumber;
    }

    public Integer getMinLimitPrice() {
        return minLimitPrice;
    }

    public void setMinLimitPrice(Integer minLimitPrice) {
        this.minLimitPrice = minLimitPrice;
    }

    public String getDescrpiton() {
        return descrpiton;
    }

    public void setDescrpiton(String descrpiton) {
        this.descrpiton = descrpiton;
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

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getUsedNumber() {
        return usedNumber;
    }

    public void setUsedNumber(Integer usedNumber) {
        this.usedNumber = usedNumber;
    }

    public Integer getReceivedNumber() {
        return receivedNumber;
    }

    public void setReceivedNumber(Integer receivedNumber) {
        this.receivedNumber = receivedNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}
