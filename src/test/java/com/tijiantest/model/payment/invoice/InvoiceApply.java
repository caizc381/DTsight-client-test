package com.tijiantest.model.payment.invoice;

import java.io.Serializable;

import com.tijiantest.model.account.UserAddress;

public class InvoiceApply implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2887408777880822773L;
	
	private Integer id;
	/**
	 * 发票抬头
	 */
	private String title;
	/**
	 * 发票内容
	 */
	private String content;
	/**
	 * 发票申请金额
	 */
	private Integer applyAmount;
	/**
	 * 发票实开金额
	 */
	private Integer amount;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 状态1：未开，2：已开，3：撤销
	 */
	private Integer status;
	/**
	 *订单id
	 */
	private Integer orderId;
	/**
	 * 获取方式 1：寄送 2：自取 3：电子
	 */
	private Integer deliveryType;
	/**
	 * 寄送费用
	 */
	private Integer postage;
	/**
	 * 申请人
	 */
	private Integer proposer;
	/**
	 * 审批人
	 */
	private Integer approver;  
	/**
	 * 寄送地址
	 */
	private Integer addressId;
	/**
	 * 寄送地址
	 */
	private UserAddress userAddress;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(Integer applyAmount) {
		this.applyAmount = applyAmount;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(Integer deliveryType) {
		this.deliveryType = deliveryType;
	}
	public Integer getPostage() {
		return postage;
	}
	public void setPostage(Integer postage) {
		this.postage = postage;
	}
	public UserAddress getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(UserAddress userAddress) {
		this.userAddress = userAddress;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public Integer getProposer() {
		return proposer;
	}
	public void setProposer(Integer proposer) {
		this.proposer = proposer;
	}
	public Integer getApprover() {
		return approver;
	}
	public void setApprover(Integer approver) {
		this.approver = approver;
	}
	
}
