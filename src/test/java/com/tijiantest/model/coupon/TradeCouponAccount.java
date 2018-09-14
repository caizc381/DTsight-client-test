package com.tijiantest.model.coupon;

import java.io.Serializable;
import java.util.Date;

/**
 * 机构优惠券子账号
 */
public class TradeCouponAccount implements Serializable {
    //机构优惠券账号ID
    private int id ;
    //交易总账号（机构交易总账号）
    private int tradeAccountId;
    //余额
    private int balance;

    private int version;

    private int isDeleted;

    private Date gmtCreated;

    private Date gmtModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTradeAccountId() {
        return tradeAccountId;
    }

    public void setTradeAccountId(int tradeAccountId) {
        this.tradeAccountId = tradeAccountId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
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
