package com.tijiantest.model.resource.meal;

import java.io.Serializable;
import java.util.Map;

import com.tijiantest.util.PinYinUtil;

public class MealSnap implements Serializable {

	private static final long serialVersionUID = 4717022571294620591L;

	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 现价
	 */
	private Integer price;
	/**
	 * 原价
	 */
	private Integer originalPrice;
	/**
	 * 调整价格
	 */
	private Integer adjustPrice;
	/**
	 * 折扣
	 */
	private Double discount;

	/**
	 * 套餐外折扣
	 */
	private Double externalDiscount;

	/**
	 * 性别
	 */
	private Integer gender;

	/**
	 * 其他费用，体检项目、住宿、纸质报告寄送、发票 key: 费用名称 value: 价格
	 */
	private Map<String, Integer> otherMoneys;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return PinYinUtil.fullWidth2halfWidth(name);
	}

	public void setName(String name) {
		this.name = PinYinUtil.fullWidth2halfWidth(name);
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(Integer originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Map<String, Integer> getOtherMoneys() {
		return otherMoneys;
	}

	public void setOtherMoneys(Map<String, Integer> otherMoneys) {
		this.otherMoneys = otherMoneys;
	}

	public Double getExternalDiscount() {
		return externalDiscount;
	}

	public void setExternalDiscount(Double externalDiscount) {
		this.externalDiscount = externalDiscount;
	}

	public Integer getAdjustPrice() {
		return adjustPrice;
	}

	public void setAdjustPrice(Integer adjustPrice) {
		this.adjustPrice = adjustPrice;
	}

	public boolean equals(Object o) {
		MealSnap mealSnap = (MealSnap) o;
		if (mealSnap.getId() == this.getId()) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}
}
