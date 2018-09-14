package com.tijiantest.model.paylog;


import java.io.Serializable;

/**
 * @author yuefengyang
 *
 */
public class PaymentMethod implements Serializable {

	public static final Integer CARD_PAY_METHOD_ID = 1;
	public static final Integer BALANCE_PAY_METHOD_ID = 2;
	
	public static final Integer OFFLINE_PAY_METHOD_TYPE = 7;
	public static final Integer ONLINE_PAY_METHOD_TYPE = 9;//线上支付，包括wx，alipay支付
	public static final Integer ALIPAY_PAY_METHOD_TYPE = 3;
	public static final Integer WX_PAY_METHOD_TYPE = 4;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3981064023420321950L;

	private Integer id;
	/**
	 * 支付名称,卡支付/余额支付/微信支付/支付宝支付/银行
	 *
	 */
	private String name;
	/**
	 * 支付类型,1：卡支付 2：余额支付 3：支付宝支付 4：微信支付 5:银联 7:线下支付
	 *
	 */
	private Integer type;
	/**
	 * 前台是否显示
	 *
	 */
	private boolean isShow;

	/**
	 * 微信公众账号appid或支付宝合作身份者ID
	 */
	private String appidOrPartner;
	/**
	 * 微信商户号或卖家email
	 */
	private String mchidOrEmail;

	/**
	 * 商户私钥
	 */
	private String secretKey;
	/**
	 * 公众账号sercret
	 */
	private String appsecret;

	/**
	 * 网站地址
	 */
	private String url;
	
	public PaymentMethod() {}
	
	public PaymentMethod(Integer id, Integer type) {
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getAppidOrPartner() {
		return appidOrPartner;
	}

	public void setAppidOrPartner(String appidOrPartner) {
		this.appidOrPartner = appidOrPartner;
	}

	public String getMchidOrEmail() {
		return mchidOrEmail;
	}

	public void setMchidOrEmail(String mchidOrEmail) {
		this.mchidOrEmail = mchidOrEmail;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
