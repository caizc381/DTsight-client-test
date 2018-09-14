package com.tijiantest.model.payment.invoice;

import java.io.Serializable;

import com.tijiantest.base.DomainObjectBase;

public class InvoiceDto extends DomainObjectBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6921653393784384543L;

	private Integer orderId;
	private String operator;
	private String title;
	private String money;
	private Integer status;
public InvoiceDto() {
		
	}
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceDto [orderId=");
		builder.append(orderId);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", title=");
		builder.append(title);
		builder.append(", money=");
		builder.append(money);
		builder.append("]");
		return builder.toString();
	}
}
