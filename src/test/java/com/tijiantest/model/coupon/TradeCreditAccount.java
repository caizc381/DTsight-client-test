package com.tijiantest.model.coupon;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户经理授信账户
 * 主管/操作员
 */
public class TradeCreditAccount implements Serializable {
    //信用账户ID
    private int id;
    //交易总账户
    private int tradeAccountId;
    //余额
    private int balance;
    //信用额度
    private int creditLimit;
    //冻结额度
    private int freezeCreditLimit;
    //版本
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

    public int getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public int getFreezeCreditLimit() {
        return freezeCreditLimit;
    }

    public void setFreezeCreditLimit(int freezeCreditLimit) {
        this.freezeCreditLimit = freezeCreditLimit;
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
