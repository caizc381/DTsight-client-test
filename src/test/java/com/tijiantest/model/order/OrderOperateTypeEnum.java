package com.tijiantest.model.order;


/**
 * 订单操作
 * @author Administrator
 *
 */
public enum OrderOperateTypeEnum {
	BOOK_ORDER(1, "下单"), PAY(2, "支付 "), FINISH_PAY(3, "完成支付"), CANCEL_ORDRE(4, "撤销"), DELETE_ORDER(5, "删除"), CLOSE_ORDER(6,
			"关闭"), RECEIPT(7, "回单"), RXPORT_ORDER(8, "导出"), CHANGE_ITEM(9, "改项"), CHANGE_DATE(10, "改期"), GATHERING(11,
			"收款"), SIGN_SETTLE(12, "标记结算");

	private int code;
	private String name;

	private OrderOperateTypeEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
