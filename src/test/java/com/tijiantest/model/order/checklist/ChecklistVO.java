package com.tijiantest.model.order.checklist;

public class ChecklistVO {
	String orderNum;
	private byte[] oneImages;// 一维码(条形码)
	private byte[] twoImages;// 二维码
	private String wxImages;// 体检中心微信二维码

	private String examAddress;// 体检地址
	private String loginName;// 登录账号
	private String examDate;// 体检时间
	private String canModifyDate;// 可修改订单时间

	private String password = "111111";// 初始密码（写死）

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public byte[] getOneImages() {
		return oneImages;
	}

	public void setOneImages(byte[] oneImages) {
		this.oneImages = oneImages;
	}

	public byte[] getTwoImages() {
		return twoImages;
	}

	public void setTwoImages(byte[] twoImages) {
		this.twoImages = twoImages;
	}

	public String getWxImages() {
		return wxImages;
	}

	public void setWxImages(String wxImages) {
		this.wxImages = wxImages;
	}

	public String getExamAddress() {
		return examAddress;
	}

	public void setExamAddress(String examAddress) {
		this.examAddress = examAddress;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public String getCanModifyDate() {
		return canModifyDate;
	}

	public void setCanModifyDate(String canModifyDate) {
		this.canModifyDate = canModifyDate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
