package com.tijiantest.model.item;

public enum PriceEffectPropertyEnum {
	PRICE(1, "价格"), IS_DISCOUNT(2, "折扣"), DELETE(3, "删除"), IS_SHOW(4, "是否显示"), COMPOSE(5, "合并关系"), CONFLICT(6,
			"互斥关系"), DEPEND(7, "依赖关系"), COMPOSE_CHILD(8, "合并项小项变化"), FAMILY(9, "父子关系");

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

	private PriceEffectPropertyEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static String getChangeProperty(Integer type) {
		switch (type) {
		case 1:
			return PriceEffectPropertyEnum.PRICE.getValue();
		case 2:
			return PriceEffectPropertyEnum.IS_DISCOUNT.getValue();
		case 3:
			return PriceEffectPropertyEnum.DELETE.getValue();
		case 4:
			return PriceEffectPropertyEnum.IS_SHOW.getValue();
		case 5:
			return PriceEffectPropertyEnum.COMPOSE.getValue();
		case 6:
			return PriceEffectPropertyEnum.CONFLICT.getValue();
		case 7:
			return PriceEffectPropertyEnum.DEPEND.getValue();
		}
		return null;
	}
}
