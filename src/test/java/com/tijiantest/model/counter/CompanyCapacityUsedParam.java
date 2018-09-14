/**
 * 
 */
package com.tijiantest.model.counter;

import java.util.Date;
import java.util.List;

/**
 * @author ren
 * 查询单位使用情况的参数
 */
public class CompanyCapacityUsedParam {
	
	// 体检单位
	private Integer companyId;
	
	// 体检中心
	private Integer hospitalId;
	
	// 分时段id
	private List<Integer> dayRanges;
	
	// 体检项目id，-1：全量项目
	private List<Integer> examItems;
	 
	// 体检单位容量使用量查询时间范围-起始时间
	private Date startDate;
	
	// 体检单位容量使用量查询时间范围-结束时间
	private Date endDate;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public List<Integer> getDayRanges() {
		return dayRanges;
	}

	public void setDayRanges(List<Integer> dayRanges) {
		this.dayRanges = dayRanges;
	}

	public List<Integer> getExamItems() {
		return examItems;
	}

	public void setExamItems(List<Integer> examItems) {
		this.examItems = examItems;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyCapacityUsedDto [companyId=");
		builder.append(companyId);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", dayRanges=");
		builder.append(dayRanges);
		builder.append(", examItems=");
		builder.append(examItems);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append("]");
		return builder.toString();
	}
}
