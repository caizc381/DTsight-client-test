package com.tijiantest.model.payment.invoice;

public enum InvoiceStatusEnum {
	NOT_MAKE_OUT_INVOICE(1,"未开"),
	MARK_OUT_INVOICE(2,"已开"),
	REVOCATION(3,"撤销");
	
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

	private InvoiceStatusEnum(int code,String value) {
		this.code = code;
		this.value = value;
	}
}
