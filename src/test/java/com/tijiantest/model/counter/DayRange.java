package com.tijiantest.model.counter;

public class DayRange {
	private Integer id;
	private String name;
	private Boolean internalUsePeriod;//内部使用时段：true-仅限内部使用; false- 无限制
	private Boolean enable;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public Boolean getInternalUsePeriod() {
		return internalUsePeriod;
	}
	public void setInternalUsePeriod(Boolean internalUsePeriod) {
		this.internalUsePeriod = internalUsePeriod;
	}
	
	
}
