package com.tijiantest.model.resource.meal;

import java.io.Serializable;

public class ExamItemSnap implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7019532078713169647L;

	private int id;
	private String hisId;
	private String name;
	private Integer price;
	private Integer originalPrice;
	private boolean discount;
	private String description;//存的时候为空，取值后可以赋值
	private int typeToMeal;//该项目针对套餐的关系：1-套餐内项目；2-套餐内删除项目；3-新增项
	private Integer type;
	private Double itemDiscount;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHisId() {
		return hisId;
	}
	public void setHisId(String hisId) {
		this.hisId = hisId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public boolean isDiscount() {
		return discount;
	}
	public void setDiscount(boolean discount) {
		this.discount = discount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTypeToMeal() {
		return typeToMeal;
	}
	public void setTypeToMeal(int typeToMeal) {
		this.typeToMeal = typeToMeal;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Double getItemDiscount() {
		return itemDiscount;
	}
	public void setItemDiscount(Double itemDiscount) {
		this.itemDiscount = itemDiscount;
	}
	
	@Override
	public String toString() {
		return "ExamItemSnap [id=" + id + ", hisId=" + hisId + ", name=" + name + ", price=" + price
				+ ", originalPrice=" + originalPrice + ", discount=" + discount + ", description=" + description
				+ ", typeToMeal=" + typeToMeal + ", type=" + type + "]";
	}
}
