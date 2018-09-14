package com.tijiantest.model.item;

public class ExamItemChangeLog {
	private Integer id;
	private Integer itemId;//单项ID
	private Integer hospitalId;//体检中心Id
	private Integer type;//修改类型
	private String originalVal;//修改之前的值
	private String newVal;//修改之后的值
	private boolean complete;//是否操作成功
	private Integer operatorId;//操作人
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getOriginalVal() {
		return originalVal;
	}
	public void setOriginalVal(String originalVal) {
		this.originalVal = originalVal;
	}
	public String getNewVal() {
		return newVal;
	}
	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	
}
