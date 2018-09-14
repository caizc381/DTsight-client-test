package com.tijiantest.model.counter;

//体检单位分时段容量单元格
public class OrderableCapacityCell {

	private int availableNum;// 当前可预约量

	private int limit;// 体检单位限额

	private boolean fromCompany;// 是否是单位预留

	private boolean enough;// 当前可预约量是否满足本次预约需求量

	private boolean rest;// 是否为休

	private boolean expireDay;// 当前预约日期已经超过体检中心设置最晚时间

	private Integer release;// 预留是否被释放 1:是；0：否

	public int getAvailableNum() {
		return availableNum;
	}

	public void setAvailableNum(int availableNum) {
		this.availableNum = availableNum;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isFromCompany() {
		return fromCompany;
	}

	public void setFromCompany(boolean fromCompany) {
		this.fromCompany = fromCompany;
	}

	public boolean isEnough() {
		return enough;
	}

	public void setEnough(boolean enough) {
		this.enough = enough;
	}

	public boolean isRest() {
		return rest;
	}

	public void setRest(boolean rest) {
		this.rest = rest;
	}

	public boolean isExpireDay() {
		return expireDay;
	}

	public void setExpireDay(boolean expireDay) {
		this.expireDay = expireDay;
	}

	public Integer getRelease() {
		return release;
	}

	public void setRelease(Integer release) {
		this.release = release;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{availableNum:");
		builder.append(availableNum);
		builder.append(", limit:");
		builder.append(limit);
		builder.append(", fromCompany:");
		builder.append(fromCompany);
		builder.append(", enough:");
		builder.append(enough);
		builder.append(", isDefaultConfig:");
		builder.append(rest);
		builder.append(", expireDay:");
		builder.append(expireDay);
		builder.append("}");
		return builder.toString();
	}
}
