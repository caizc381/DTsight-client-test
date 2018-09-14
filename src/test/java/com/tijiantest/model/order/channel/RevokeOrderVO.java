package com.tijiantest.model.order.channel;

import java.util.List;

/*
 * 撤单VO
*/
public class RevokeOrderVO {

	private List<Integer> orderIds;// 订单ids
	private Boolean sendMsg;// 是否发送短信
	public List<Integer> getOrderIds() {
		return orderIds;
	}
	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}
	public Boolean getSendMsg() {
		return sendMsg;
	}
	public void setSendMsg(Boolean sendMsg) {
		this.sendMsg = sendMsg;
	}

}
