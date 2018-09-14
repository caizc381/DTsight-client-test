package com.tijiantest.model.payment.invoice;

import java.util.Date;

public class OrderInvoiceApplyVo extends InvoiceApply{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4667023844246660059L;

	private Date examDate;
	
	private Integer orderPrice;
	
	private String invoiceEmailDateTip;
	
	private Boolean isCreateInvoiceApply;
	
	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Integer getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getInvoiceEmailDateTip() {
		return invoiceEmailDateTip;
	}

	public void setInvoiceEmailDateTip(String invoiceEmailDateTip) {
		this.invoiceEmailDateTip = invoiceEmailDateTip;
	}

	public Boolean getIsCreateInvoiceApply() {
		return isCreateInvoiceApply;
	}

	public void setIsCreateInvoiceApply(Boolean isCreateInvoiceApply) {
		this.isCreateInvoiceApply = isCreateInvoiceApply;
	}
	
}
