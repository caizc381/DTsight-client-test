package com.tijiantest.model.resource.meal;

import java.util.ArrayList;
import java.util.List;

public class EditMealBody {
	
	private Meal meal;
	/**
	 * 套餐内项目
	 */
	private List<MealItem> mealItemList;
	/**
	 * 单位id
	 */
	private Integer companyId;
	
	
	/**
	 * 默认规则
	 */
	private Integer ruleId;
	
	/**
	 * 新单位id
	 */
	private Integer newCompanyId;

	List<MealMultiChoosen> mealMultiChoosenList;

	
	
	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * 
	 * @param meal
	 * @param mealItemList
	 */
	public EditMealBody(Meal meal,List<MealItem> mealItemList,int ruleId){
		//super();
		this.meal = meal;
		this.mealItemList = mealItemList;
		this.ruleId = ruleId;
	}
	
	/**
	 * 
	 * @param companyId
	 * @param meal
	 * @param mealItemList
	 */
	public EditMealBody(int companyId,Meal meal,List<MealItem> mealItemList,int ruleId){
		//super();
		this.meal = meal;
		this.mealItemList = mealItemList;
		this.companyId = companyId;	
		this.ruleId = ruleId;
		this.newCompanyId = companyId;
		this.mealMultiChoosenList = new ArrayList<>();
	}


	public Meal getMeal() {
		return meal;
	}


	public void setMeal(Meal meal) {
		this.meal = meal;
	}


	public List<MealItem> getMealItemList() {
		return mealItemList;
	}

	public void setMealItemList(List<MealItem> mealItemList) {
		this.mealItemList = mealItemList;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public List<MealMultiChoosen> getMealMultiChoosenList() {
		return mealMultiChoosenList;
	}

	public void setMealMultiChoosenList(List<MealMultiChoosen> mealMultiChoosenList) {
		this.mealMultiChoosenList = mealMultiChoosenList;
	}
}
