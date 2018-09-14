package com.tijiantest.model.order;

import java.io.Serializable;

public class OrderListVO extends MongoOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6812741716207968903L;

	/**
	 * 医院是否开启了结算功能
	 */
	private Boolean hasSettlementOpen;
	 /**
     * 退款场景:5 表示手动退款（订单列表标示是否是手动退款使用）
     *  请查看com.mytijian.trade.refund.constant.RefundConstants.RefundScene
     */
	private Integer refundScene;
	public Boolean getHasSettlementOpen() {
		return hasSettlementOpen;
	}
	public void setHasSettlementOpen(Boolean hasSettlementOpen) {
		this.hasSettlementOpen = hasSettlementOpen;
	}
	public Integer getRefundScene() {
		return refundScene;
	}
	public void setRefundScene(Integer refundScene) {
		this.refundScene = refundScene;
	}
	
	
}
