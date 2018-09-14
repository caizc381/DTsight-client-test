package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;

public class OrderGuideInfoSnapshot implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5124908373714700343L;

	/**
	 * 订单编号
	 */
	private String orderNum;
	
	/**
	 * 成功/失败/错误等状态码 1成功，其他未知
	 */
	private String code;
	
	/**
	 * 登录失败/错误时的额外信息
	 */
	private String msg;
	/**
	 * 当前时间
	 */
	private Date currentTime;
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	
}
