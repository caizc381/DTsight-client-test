package com.tijiantest.model.card;

public class CardMeal {

	private Integer cardId;
	
	private Integer hospitalId;
	
	private Integer mealId;
	
	/**
	 * 本人套餐
	 */
	private Boolean isMealForSelf;
	
	/**
	 * 家属套餐
	 */
	private Boolean isMealForFamily;

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public Boolean getIsMealForSelf() {
		return isMealForSelf;
	}

	public void setIsMealForSelf(Boolean isMealForSelf) {
		this.isMealForSelf = isMealForSelf;
	}

	public Boolean getIsMealForFamily() {
		return isMealForFamily;
	}

	public void setIsMealForFamily(Boolean isMealForFamily) {
		this.isMealForFamily = isMealForFamily;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	
}
