package com.tijiantest.model.resource.meal;

import java.util.List;

import com.tijiantest.model.item.ExamItem;

public class MealDto{

	/**
	 * 体检单位标识
	 */
	private Integer companyId;
	
	/**
	 * 加项包规则
	 */
	private Integer ruleId;
	
	/**
	 * 套餐
	 */
	private Meal meal;
	
	/**
	 * 套餐体检项列表
	 */
	List<ExamItem> mealItemList;
	
	/**
	 * 新单位id
	 */
	private Integer newCompanyId;
	
	/**
	 * 客户经理账户id
	 */
	private Integer accountId;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Meal getMeal() {
		return meal;
	}

	public void setMeal(Meal meal) {
		this.meal = meal;
	}

	public List<ExamItem> getMealItemList() {
		return mealItemList;
	}

	public void setMealItemList(List<ExamItem> mealItemList) {
		this.mealItemList = mealItemList;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
}
