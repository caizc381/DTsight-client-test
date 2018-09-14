package com.tijiantest.model.payment;

import java.util.List;

import com.tijiantest.model.payment.invoice.Invoice;


/**
 * 支付页面表单数据
 *
 * @author clozz
 *
 */
public class PaymentForm {

	private Integer orderId;

	/**
	 * 新的支付系统暂时不支持使用多张卡支付，所有支付方式都为入口卡？。，
	 */
	@Deprecated
	private List<Integer> idcards; //

	// 入口卡
	private Integer selectedCardId;
	private boolean useBalance;
	private String orderSnapId;

	private Invoice invoice;
	private Integer payType; // 3支付宝、4微信、7线下支付
	private String client; // wap\pc\wx 手机端、pc端、微信端
	private String subSite;
	private Integer hospitalId;
	private String openid;
	private Integer apptype;
	private Integer operator;
	private Boolean isNoLoginIn;
	private Integer couponId;

	private String operatorName;

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Integer getCouponId() {
		return couponId;
	}

	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	@Deprecated
	public List<Integer> getIdcards() {
		return idcards;
	}
	public void setIdcards(List<Integer> idcards) {
		this.idcards = idcards;
	}
	public Integer getSelectedCardId() {
		return selectedCardId;
	}
	public void setSelectedCardId(Integer selectedCardId) {
		this.selectedCardId = selectedCardId;
	}
	public boolean isUseBalance() {
		return useBalance;
	}
	public void setUseBalance(boolean useBalance) {
		this.useBalance = useBalance;
	}
	public String getOrderSnapId() {
		return orderSnapId;
	}
	public void setOrderSnapId(String orderSnapId) {
		this.orderSnapId = orderSnapId;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
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
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}


	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getApptype() {
		return apptype;
	}

	public void setApptype(Integer apptype) {
		this.apptype = apptype;
	}

	public Integer getOperator() {
		return operator;
	}

	public void setOperator(Integer operator) {
		this.operator = operator;
	}

	public Boolean getIsNoLoginIn() {
		return isNoLoginIn;
	}

	public void setIsNoLoginIn(Boolean noLoginIn) {
		isNoLoginIn = noLoginIn;
	}
}
