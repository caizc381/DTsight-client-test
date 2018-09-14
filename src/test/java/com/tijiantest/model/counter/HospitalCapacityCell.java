package com.tijiantest.model.counter;

/**
 * 体检单位分时段容量单元格，
 * 
 * @author ren
 *
 */
public class HospitalCapacityCell {
	
	//id
	private Integer id;
	
	// 体检中心设置容量
	private int capacity; 
	
	// 体检中心当前可预约量
	private int availableNum;
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getAvailableNum() {
		return availableNum;
	}

	public void setAvailableNum(int availableNum) {
		this.availableNum = availableNum;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(":{capacity:");
		builder.append(capacity);
		builder.append(", availableNum:");
		builder.append(availableNum);
		builder.append("}");
		return builder.toString();
	}
	
}
