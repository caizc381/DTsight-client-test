package com.tijiantest.model.paymentOrder;

import java.io.Serializable;

public class PaymentQRCodeVO implements Serializable {
    private static final long serialVersionUID = -3857495123583743208L;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 支持支付方式：1 支付宝， 2 微信，3 都可以
     */
    private Integer paymentMethod;

    /**
     * 付款二维码
     */
    private byte[] qrCode;

    private String logo;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
