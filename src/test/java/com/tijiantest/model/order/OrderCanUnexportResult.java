package com.tijiantest.model.order;

import java.io.Serializable;
import java.util.List;

public class OrderCanUnexportResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3179634196935750704L;

	private int exportFailedNum=0;
	private int exportedNum = 0;
	private List<Integer> orderIds;
	public int getExportFailedNum() {
		return exportFailedNum;
	}
	public void setExportFailedNum(int exportFailedNum) {
		this.exportFailedNum = exportFailedNum;
	}
	public int getExportedNum() {
		return exportedNum;
	}
	public void setExportedNum(int exportedNum) {
		this.exportedNum = exportedNum;
	}
	public List<Integer> getOrderIds() {
		return orderIds;
	}
	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}
	
	
}
