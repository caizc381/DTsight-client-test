package com.tijiantest.model.resource.meal;

public class MealSetting {
	
	/**
	 * 套餐id
	 */
	private Integer mealId;
	
	/**
	 * 显示单项价格
	 */
	private Boolean showItemPrice;
	
	/**
	 * 显示套餐价格
	 */
	private Boolean showMealPrice;
	
	/**
	 * 是否只显示套餐内项目
	 */
	private Boolean onlyShowMealItem;
	
	/**
	 * 套餐调整价格
	 */
	private int adjustPrice;
	
	//锁定价格
	private Boolean lockPrice;
	
	public MealSetting(){}
	
	public MealSetting(int mealId,boolean showItemPrice,boolean showMealPrice,boolean onlyShowMealItem,int adjustPrice){
		this.mealId = mealId;
		this.showItemPrice = showItemPrice;
		this.showMealPrice = showMealPrice;
		this.onlyShowMealItem = onlyShowMealItem;
		this.adjustPrice = adjustPrice;
	}
	
	public MealSetting(int mealId,int adjustPrice){
		this.mealId = mealId;
		this.adjustPrice = adjustPrice;
	}
	
	public MealSetting(boolean showItemPrice,boolean showMealPrice,boolean onlyShowMealItem,int adjustPrice){
		this.showItemPrice = showItemPrice;
		this.showMealPrice = showMealPrice;
		this.onlyShowMealItem = onlyShowMealItem;
		this.adjustPrice = adjustPrice;
	}	

	public MealSetting(Integer mealId, Boolean showItemPrice, Boolean showMealPrice,
			int adjustPrice, Boolean lockPrice) {
		super();
		this.mealId = mealId;
		this.showItemPrice = showItemPrice;
		this.showMealPrice = showMealPrice;
		this.adjustPrice = adjustPrice;
		this.lockPrice = lockPrice;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public Boolean isShowItemPrice() {
		return showItemPrice;
	}

	public void setShowItemPrice(Boolean showItemPrice) {
		this.showItemPrice = showItemPrice;
	}

	public Boolean isShowMealPrice() {
		return showMealPrice;
	}

	public void setShowMealPrice(Boolean showMealPrice) {
		this.showMealPrice = showMealPrice;
	}

	public Boolean isOnlyShowMealItem() {
		return onlyShowMealItem == null ? false : onlyShowMealItem;
	}

	public void setOnlyShowMealItem(Boolean onlyShowMealItem) {
		this.onlyShowMealItem = onlyShowMealItem;
	}

	public int getAdjustPrice() {
		return adjustPrice;
	}

	public void setAdjustPrice(int adjustPrice) {
		this.adjustPrice = adjustPrice;
	}

	public Boolean getLockPrice() {
		return lockPrice;
	}

	public void setLockPrice(Boolean lockPrice) {
		this.lockPrice = lockPrice;
	}
	
	
	
	
}
