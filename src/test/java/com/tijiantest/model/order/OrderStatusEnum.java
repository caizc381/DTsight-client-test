package com.tijiantest.model.order;


public enum OrderStatusEnum {
	
	unpaid(0,"未付款"),
	paySuccess(1,"已支付"),
	appointmentSuccess(2,"已预约"),
	examSuccess(3,"体检完成"),
	unExam(4,"未到检"),
	revoke(5,"已撤销"),
	remove(6,"已删除"),
	paying(7,"支付中"),
	closed(8,"已关闭"),
	partRefund(9,"部分退"),
	exportFailed(10,"导出失败"),
	needLocalPay(11, "现场付款");
	
	private int code;
	
	private String value;

	public static OrderStatusEnum getByCode(int code){
		for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()){
			if (orderStatusEnum.getCode() == code){
				return orderStatusEnum;
			}
		}
		return null;
	}

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
	
	private OrderStatusEnum(int code, String value){
		this.code = code;
		this.value = value;
	}
}
