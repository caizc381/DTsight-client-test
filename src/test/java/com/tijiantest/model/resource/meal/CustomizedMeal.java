package com.tijiantest.model.resource.meal;

import com.tijiantest.model.resource.meal.Meal;

public class CustomizedMeal extends Meal{
	
	/**
	 * 套餐id
	 */
	private Integer mealId;
	/**
	 * 账户id
	 */
	private Integer accountId;
	
	/**
	 * 体检单标识
	 */
	private Integer companyId;
	
	public CustomizedMeal(int id, int hospitalid) {
		super(id, hospitalid);
		// TODO Auto-generated constructor stub
	}

	public CustomizedMeal(Integer accountId, Integer companyId, Integer mealId) {
		// TODO Auto-generated constructor stub
		this(mealId);
		this.accountId = accountId;
		this.companyId = companyId;
	}
	
	public CustomizedMeal(Integer id) {
		// TODO Auto-generated constructor stub
		super(id);
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}
	
}