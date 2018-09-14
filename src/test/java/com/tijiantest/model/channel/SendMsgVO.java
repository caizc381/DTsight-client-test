package com.tijiantest.model.channel;

import java.util.List;

/**
 * 批量发送短信vo
 * @author Administrator
 *
 */
public class SendMsgVO {

	private List<Integer> orderIds;//订单id
	private String msgContent;//消息内容
	public List<Integer> getOrderIds() {
		return orderIds;
	}
	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
}
