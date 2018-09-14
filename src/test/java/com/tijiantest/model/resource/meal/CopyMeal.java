package com.tijiantest.model.resource.meal;


public class CopyMeal {
	
	/**
	 * 单位id
	 */
	private Integer destCompanyId;
	
	/**
	 * 套餐id
	 */
	private Integer sourceMealId;
	
	/**
	 * 套餐名称
	 */
	private String destMealName;
	
	/**
	 * 套餐类型
	 */
	private Integer destMealType;
	
	
	/**
	 * 
	 * @param companyId
	 * @param mealId
	 * @param mealName
	 * @param mealType 
	 * @param i 
	 */
	public CopyMeal(Integer companyId,Integer mealId,String mealName,Integer mealType){
		//super();
		this.destCompanyId = companyId;
		this.sourceMealId = mealId;
		this.destMealName = mealName;
		this.destMealType = mealType;
	}


	public Integer getDestCompanyId() {
		return destCompanyId;
	}


	public void setDestCompanyId(Integer destCompanyId) {
		this.destCompanyId = destCompanyId;
	}


	public Integer getSourceMealId() {
		return sourceMealId;
	}


	public void setSourceMealId(Integer sourceMealId) {
		this.sourceMealId = sourceMealId;
	}


	public String getDestMealName() {
		return destMealName;
	}


	public void setDestMealName(String destMealName) {
		this.destMealName = destMealName;
	}


	public Integer getDestMealType() {
		return destMealType;
	}


	public void setDestMealType(Integer destMealType) {
		this.destMealType = destMealType;
	}
	
	

}
