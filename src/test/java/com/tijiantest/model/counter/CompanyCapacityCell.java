package com.tijiantest.model.counter;

/**
 * 体检单位分时段容量单元格，
 * 
 * @author ren
 *
 */
public class CompanyCapacityCell {
	
	// 体检单位预留容量
	private Integer reserveNum; 
	
	// 体检单位当前可预约量
	private Integer usedNum; 
	
	// 体检单位限额
	private Integer limit;
	
	/**
	 * 预留是否被释放  1：是 0：否
	 */
	private Integer release;
	
	public Integer getRelease() {
		return release;
	}

	public void setRelease(Integer release) {
		this.release = release;
	}

	public Integer getReserveNum() {
		return reserveNum;
	}

	public void setReserveNum(Integer reserveNum) {
		this.reserveNum = reserveNum;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{reserveNum:");
		builder.append(reserveNum);
		builder.append(", usedNum:");
		builder.append(usedNum);
		builder.append(", limit:");
		builder.append(limit);
		builder.append("}");
		return builder.toString();
	}

}
