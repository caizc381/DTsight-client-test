package com.tijiantest.model.paymentOrder;


import java.io.Serializable;

public class CreatePaymentOrderDTO implements Serializable {
    private static final long serialVersionUID = 4228599451305863978L;

    /**
     * 医院id
     */
    private Integer hospitalId;

    /**
     * 客户经理id
     */
    private Integer managerId;

    /**
     * 付款人姓名
     */
    private String name;

    /**
     * 付款金额
     */
    private Long amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否为微信支付
     */
    private Boolean isWxPay;

    /**
     * 支付终端： wap\pc 手机端\pc端
     */
    private String client;

    private String openid;

    private String subSite;

    private String clientIp;

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getIsWxPay() {
        return isWxPay;
    }

    public void setIsWxPay(Boolean wxPay) {
        isWxPay = wxPay;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSubSite() {
        return subSite;
    }

    public void setSubSite(String subSite) {
        this.subSite = subSite;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
