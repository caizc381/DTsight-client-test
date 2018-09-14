package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.Date;
import com.tijiantest.util.DateUtils;

public class OrderReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 54974872373435686L;

	private Date createDate;
	private int orderCount;
	private int orderClientCount;
	private int orderAddItemCount;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getOrderClientCount() {
		return orderClientCount;
	}

	public void setOrderClientCount(int orderClientCount) {
		this.orderClientCount = orderClientCount;
	}

	public int getOrderAddItemCount() {
		return orderAddItemCount;
	}

	public void setOrderAddItemCount(int orderAddItemCount) {
		this.orderAddItemCount = orderAddItemCount;
	}

	public String getCreateDateString() {
		return this.createDate != null ? DateUtils.format("yyyy-MM-dd", this.createDate) : null;
	}
}
