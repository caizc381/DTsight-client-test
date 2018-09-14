package com.tijiantest.model.examitempackage;

import java.util.List;

public class ExamItemCalculateVo {
	private Integer hospitalId;
	private Double discount;
	private List<Integer> itemIds;
	
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public List<Integer> getItemIds() {
		return itemIds;
	}
	public void setItemIds(List<Integer> itemIds) {
		this.itemIds = itemIds;
	}
}
