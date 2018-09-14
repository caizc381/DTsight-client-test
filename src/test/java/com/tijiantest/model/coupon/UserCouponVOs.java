package com.tijiantest.model.coupon;

import java.io.Serializable;

public class UserCouponVOs implements Serializable {

    private Integer accountId;
    private int status;
    private Integer couponId;
    private String mobile;

    private CouponTemplateResult couponTemplateResult;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public CouponTemplateResult getCouponTemplateResult() {
        return couponTemplateResult;
    }

    public void setCouponTemplateResult(CouponTemplateResult couponTemplateResult) {
        this.couponTemplateResult = couponTemplateResult;
    }
}
