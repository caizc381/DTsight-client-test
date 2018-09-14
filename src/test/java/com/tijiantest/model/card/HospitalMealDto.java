package com.tijiantest.model.card;

import java.io.Serializable;

public class HospitalMealDto extends CardMeal implements Serializable{

	private static final long serialVersionUID = 992791387083357194L;
	private Integer mealPrice;
	
	private String mealName;

	private Integer gender;
	/**
	 * 1：单位套餐，3：通用套餐
	 */
	private Integer mealType;

	/**
	 * 套餐状态  mealStateEnum
	 */
	private Integer mealStatus;

	/**
	 * 套餐序号
	 */
	private Integer mealSequence;


	public Integer getMealSequence() {
		return mealSequence;
	}

	public void setMealSequence(Integer mealSequence) {
		this.mealSequence = mealSequence;
	}

	public Integer getGender() {
		return gender;
	}


	public Integer getMealStatus() {
		return mealStatus;
	}

	public void setMealStatus(Integer mealStatus) {
		this.mealStatus = mealStatus;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getMealPrice() {
		return mealPrice;
	}

	public void setMealPrice(Integer mealPrice) {
		this.mealPrice = mealPrice;
	}

	public Integer getMealType() {
		return mealType;
	}

	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	
	
}
