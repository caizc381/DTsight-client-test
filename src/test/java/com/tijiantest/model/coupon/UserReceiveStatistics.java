package com.tijiantest.model.coupon;

import org.testng.reporters.jq.INavigatorPanel;

import java.io.Serializable;
import java.util.Date;

public class UserReceiveStatistics implements Serializable {
    //ID
    private int id;
    //用户ID
    private Integer accountId;
    //机构ID
    private Integer organizationId;
    //模板批次号
    private String templateBatchNum;
    //该优惠券模板领取限制数量（冗余）
    private int receiveLimitNum;
    //当前用户领取的数量
    private int receiveNum;
    //当前用户已使用的数量
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


    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getTemplateBatchNum() {
        return templateBatchNum;
    }

    public void setTemplateBatchNum(String templateBatchNum) {
        this.templateBatchNum = templateBatchNum;
    }

    public int getReceiveLimitNum() {
        return receiveLimitNum;
    }

    public void setReceiveLimitNum(int receiveLimitNum) {
        this.receiveLimitNum = receiveLimitNum;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
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
