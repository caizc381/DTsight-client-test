package com.tijiantest.model.examitempackage;

import java.io.Serializable;

public class PriceEntry implements Serializable {
	private static final long serialVersionUID = 2248162528476144182L;

	public PriceEntry() {
	}

	public PriceEntry(int originalPrice, int price) {
		this.originalPrice = originalPrice;
		this.price = price;
	}

	public int getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(int originalPrice) {
		this.originalPrice = originalPrice;
	}

	public void addOriginalPrice(int originalPrice) {
		this.originalPrice += originalPrice;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void addPrice(int price) {
		this.price += price;
	}

	private int originalPrice;
	private int price;
}
