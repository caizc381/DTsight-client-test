package com.tijiantest.model.settlement;

import java.io.Serializable;

/**
 * Created by wangzhongxing on 2017/8/4.
 */
public class UnsettlementOrder implements Serializable {

    private static final long serialVersionUID = -1L;

    private String orderNum;
    /**
     * 下单时间
     */
    private Long insertTime;
    /**
     * 客户经理id
     */
    private Integer ownerId;
    /**
     * 客户经理名称
     */
    private String managerName;
    /**
     * 体检日期
     */
    private String examDate;
    /**
     * 用户名
     */
    private String accountName;
    /**
     * 用户身份证号
     */
    private String accountIdcard;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 部门名称
     */
    private String department;
    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 订单价格
     */
    private Integer orderPrice;
    
    

    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAccountIdcard() {
        return accountIdcard;
    }

    public void setAccountIdcard(String accountIdcard) {
        this.accountIdcard = accountIdcard;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Long insertTime) {
        this.insertTime = insertTime;
    }

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
}
