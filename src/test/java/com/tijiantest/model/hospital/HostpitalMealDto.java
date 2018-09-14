package com.tijiantest.model.hospital;

public class HostpitalMealDto {

	/**
	 * 如果hospitalId=-1代表所有体检中心
	 */
	private Integer hospitalId;
	
	/**
	 * 如果mealId=-1代表官方套餐，否则单位套餐
	 */
	private Integer mealId;
	
	private Integer mealPrice;
	private String mealName;
	private Integer gender;
	private Integer mealType;//1-单位套餐；3-通用套餐
	
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

	public Integer getMealPrice() {
		return mealPrice;
	}

	public void setMealPrice(Integer mealPrice) {
		this.mealPrice = mealPrice;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getMealType() {
		return mealType;
	}

	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}	

}
