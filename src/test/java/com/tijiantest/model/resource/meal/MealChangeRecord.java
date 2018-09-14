package com.tijiantest.model.resource.meal;

import java.io.Serializable;
import java.util.Date;

public class MealChangeRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4133527808131475213L;
	
private Integer id;
	
	private Integer mealId;
	
	private String operation;
	
	private Integer hosptialId;
	
	private Date operationDate;
	
	private Date createDate;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Integer getHosptialId() {
		return hosptialId;
	}

	public void setHosptialId(Integer hosptialId) {
		this.hosptialId = hosptialId;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
