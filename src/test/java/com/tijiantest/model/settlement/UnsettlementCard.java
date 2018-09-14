package com.tijiantest.model.settlement;

import java.io.Serializable;

/**
 * Created by wangzhongxing on 2017/8/23.
 */
public class UnsettlementCard implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 体检卡id
     */
    private Integer id;
    /**
     * 发卡时间
     */
    private long senCardTime;
    /**
     * 客户经理id
     */
    private Integer managerId;
    /**
     * 客户经理名字
     */
    private String managerName;
    /**
     * 体检用户名
     */
    private String accountName;
    /**
     * 体检用户身份证号
     */
    private String accountIdcard;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 初始金额
     */
    private Long capacity;
    /**
     * 余额
     */
    private Long balance;
    /**
     * 已使用金额
     */
    private Long alreadyUsedAmount;
    /**
     * 体检卡状态
     */
    private Integer status;
    /**
     * 结算方式
     */
    private Integer settlementMode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getSenCardTime() {
        return senCardTime;
    }

    public void setSenCardTime(long senCardTime) {
        this.senCardTime = senCardTime;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountIdcard() {
        return accountIdcard;
    }

    public void setAccountIdcard(String accountIdcard) {
        this.accountIdcard = accountIdcard;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAlreadyUsedAmount() {
        return alreadyUsedAmount;
    }

    public void setAlreadyUsedAmount(Long alreadyUsedAmount) {
        this.alreadyUsedAmount = alreadyUsedAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSettlementMode() {
        return settlementMode;
    }

    public void setSettlementMode(Integer settlementMode) {
        this.settlementMode = settlementMode;
    }

}
