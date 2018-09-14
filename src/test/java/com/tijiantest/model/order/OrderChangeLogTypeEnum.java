package com.tijiantest.model.order;

public enum OrderChangeLogTypeEnum {
	CUSTOM(1,"用户订单改项目"),
	MANAGER(2,"体检中心管理员修改项目"),
	SYSTEM(3,"系统定时任务修改项目"),
	CHANGE_NO_EXPORT(4,"恢复未改"),
	CHANGE_TO_LAST_SUCCESSFUL(5,"回到最好一次订单状态");
	private int code;
	private String value;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private OrderChangeLogTypeEnum(int code, String value){
		this.code = code;
		this.value = value;
	}
}
