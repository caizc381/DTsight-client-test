package com.tijiantest.model.coupon;

import java.io.Serializable;
import java.util.Date;

public class CouponTemplateStatistics implements Serializable {

    private int id;
    //模板批次号
    private String templateBatchNum;
    //机构ID
    private int organizationId;
    //已经领取的数量
    private int receivedNum;
    //发放数量
    private int quantity;
    //已使用的数量
    private int usedNum;
    //创建时间
    private Date gmtCreated;
    //更新时间
    private Date gmtModified;
    //版本号
    private int version;
    //是否删除,0：否，1：是
    private int isDeleted;

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

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getReceivedNum() {
        return receivedNum;
    }

    public void setReceivedNum(int receivedNum) {
        this.receivedNum = receivedNum;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
