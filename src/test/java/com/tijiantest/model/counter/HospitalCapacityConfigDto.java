package com.tijiantest.model.counter;

import java.util.List;
import java.util.Map;


/**
 * 体检中心容量配置
 * 
 * @author ren
 *
 */
public class HospitalCapacityConfigDto {
	
	// 全局设置 OR 自定义设置，true:全局设置 false:自定义设置
	private Boolean globalConfig;
	
	// 体检中心
	private Integer hospitalId;
	
	// 配置有效时间范围-开始时间
	private String startDate;
	
	// 配置有效时间范围-结束时间
	private String endDate;
	
	// 设置关联日期，1:周一 2:周二 3:周三 4:周四 5:周五 6:周六
	private List<Integer> daysOfWeek;
	
	// 体检中心分时段容量配置, <examItem, <period, num>>
	private Map<String, Map<String, Integer>> hosptialPeriodSetting;

	public Boolean isGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(Boolean isGlobalConfig) {
		this.globalConfig = isGlobalConfig;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<Integer> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Integer> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public Map<String, Map<String, Integer>> getHosptialPeriodSetting() {
		return hosptialPeriodSetting;
	}

	public void setHosptialPeriodSetting(Map<String, Map<String, Integer>> hosptialPeriodSetting) {
		this.hosptialPeriodSetting = hosptialPeriodSetting;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HospitalCapacityConfigDto [globalConfig=");
		builder.append(globalConfig);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", daysOfWeek=");
		builder.append(daysOfWeek);
		builder.append(", hosptialPeriodSetting=");
		builder.append(hosptialPeriodSetting);
		builder.append("]");
		return builder.toString();
	}
}
