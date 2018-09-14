package com.tijiantest.model.order;

import java.io.Serializable;


public class HospitalSnapshot extends OrgnizationSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8964747566674959986L;
	/**
	 * 自动确认订单
	 */
	private Boolean autoConfirmOrder;
	/**
	 * 对接方式,0:无缝,1:有缝
	 */
	private Integer cooperateType;

	/**
	 * 预约成功是否向客户发送短信 0:否、1：是
	 */
	private Boolean isSendMessage;
    /**
     * 是否开通打印导检单
     */
    private Boolean openPrintExamGuide;
    /**
     * 是否开通队列
     */
    private Boolean openQueue;
	public Boolean getAutoConfirmOrder() {
		return autoConfirmOrder;
	}
	public void setAutoConfirmOrder(Boolean autoConfirmOrder) {
		this.autoConfirmOrder = autoConfirmOrder;
	}
	public Integer getCooperateType() {
		return cooperateType;
	}
	public void setCooperateType(Integer cooperateType) {
		this.cooperateType = cooperateType;
	}
	public Boolean getIsSendMessage() {
		return isSendMessage;
	}
	public void setIsSendMessage(Boolean isSendMessage) {
		this.isSendMessage = isSendMessage;
	}
	public Boolean getOpenPrintExamGuide() {
		return openPrintExamGuide;
	}
	public void setOpenPrintExamGuide(Boolean openPrintExamGuide) {
		this.openPrintExamGuide = openPrintExamGuide;
	}
	public Boolean getOpenQueue() {
		return openQueue;
	}
	public void setOpenQueue(Boolean openQueue) {
		this.openQueue = openQueue;
	}

}
