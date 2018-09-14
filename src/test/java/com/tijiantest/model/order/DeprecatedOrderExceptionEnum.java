package com.tijiantest.model.order;
@Deprecated
public enum DeprecatedOrderExceptionEnum {

	BOOK_ORDER_FAIL(1000, "下单失败"), 
	CHANGE_EXAMDATE_FAIL(1001, "订单改期失败"),
	REVOKE_ORDER_FAIL(1003, "订单撤销失败"),
	CLOSE_ORDER_FAIL(1005, "订单关闭失败"),
	DELETE_ORDER_FAIL(1008, "订单关闭失败"),
	CANNOT_REVOKE(1002, "订单无法撤销"),
	STATUS_CANNOT_REVOKE(1017, "此状态订单无法撤销"),
	CANNOT_CLOSED(1006, "订单无法关闭"),
	CANNOT_DELETE(1007, "订单无法删除"),
	CHANGE_EXAMITEM_FAIL(1004,"订单改项失败"),
	NOT_ACCEPT_OFFLINE_PAYMENT(1009, "订单无法线下收款"),
	ORDER_NOT_EXSITS(1009, "订单不存在"),
	ACCEPT_LOCAL_PAY_FAIL(1010,"收款失败"),
	UPDATE_AFTER_EXPORT_FAIL(1011,"标记订单导出失败"),
	CANNOT_LOCAL_PAY_UP(1012,"订单状态不支持收款操作"),
	CANNOT_CLOSE_ORDER_PROCESS(1014,"无法关闭该下单进程"),
	CHANGE_TO_UN_EXPORT_FAIL(1015,"恢复订单到未导状态失败"),
	CHANGE_TO_UPDATE_ORDER_STATUS(1016,"更新订单状态失败"),
	CANNOT_CHANGE_EXAMDATE(1013, "订单无法改期");


	private int code;
	private String msg;

	private DeprecatedOrderExceptionEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
