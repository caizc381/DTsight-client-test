package com.tijiantest.model.order.snapshot;

import java.io.Serializable;
import java.util.List;

public class OrderMealSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4858136684519517706L;

	private Integer id;

	private String orderNum;

	/**
	 * '支付流水id'
	 */
	private Integer paymentRecordId;

	/**
	 * 退款流水id
	 */
	private Integer refundRecordId;


	/**
	 * 套餐快照
	 */
	private MealSnapshot mealSnapshot;
	/**
	 * 加项包快照
	 */
	private ExamItemPackageSnapshot examItemPackageSnapshot;
	
	/**
	 * 体检项目快照列表包含
	 */
	List<ExamItemSnapshot> examItemSnapList;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getPaymentRecordId() {
		return paymentRecordId;
	}

	public void setPaymentRecordId(Integer paymentRecordId) {
		this.paymentRecordId = paymentRecordId;
	}

	public Integer getRefundRecordId() {
		return refundRecordId;
	}

	public void setRefundRecordId(Integer refundRecordId) {
		this.refundRecordId = refundRecordId;
	}

	public List<ExamItemSnapshot> getExamItemSnapList() {
		return examItemSnapList;
	}

	public void setExamItemSnapList(List<ExamItemSnapshot> examItemSnapList) {
		this.examItemSnapList = examItemSnapList;
	}

	public MealSnapshot getMealSnapshot() {
		return mealSnapshot;
	}
	public void setMealSnapshot(MealSnapshot mealSnapshot) {
		this.mealSnapshot = mealSnapshot;
	}
	public ExamItemPackageSnapshot getExamItemPackageSnapshot() {
		return examItemPackageSnapshot;
	}
	public void setExamItemPackageSnapshot(ExamItemPackageSnapshot examItemPackageSnapshot) {
		this.examItemPackageSnapshot = examItemPackageSnapshot;
	}
	
}
