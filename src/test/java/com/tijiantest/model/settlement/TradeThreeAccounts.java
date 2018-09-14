package com.tijiantest.model.settlement;


import java.io.Serializable;
import java.util.Date;

/**
 * 用户第三方支付账户
 * @author Administrator
 *
 */
public class TradeThreeAccounts implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 三方账户类型：0 支付宝, 1 微信
     */
    private Integer type;

    /**
     * 三方账户标识
     */
    private String threeAccountId;

    /**
     * 三方账户余额
     */
    private Long balance;

    /**
     * 总账户ID
     */
    private Integer tradeAccountId;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date gmtCreated;

    /**
     * 修改时间
     */
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getThreeAccountId() {
        return threeAccountId;
    }

    public void setThreeAccountId(String threeAccountId) {
        this.threeAccountId = threeAccountId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Integer getTradeAccountId() {
        return tradeAccountId;
    }

    public void setTradeAccountId(Integer tradeAccountId) {
        this.tradeAccountId = tradeAccountId;
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
