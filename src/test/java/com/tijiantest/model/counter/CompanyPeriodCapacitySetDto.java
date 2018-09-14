/**
 * 
 */
package com.tijiantest.model.counter;

import java.util.Date;
import java.util.Map;

import com.tijiantest.util.DateUtils;

/**
 * @author ren
 *
 */
public class CompanyPeriodCapacitySetDto {
	
    // 体检单位id
	private Integer companyId;

	private Integer oldCompanyId;
	
	// 体检单位id
	private Integer hospitalId;
	
	// 设置时间范围-起始日期
	private Date startDate;
	
	// 设置时间范围-结束日期
	private Date endDate;
	
	// 分时间段人数设置, <period, <examitem, CompanyCapacityCell>>
	private Map<String, Map<String, CompanyCapacityCell>> companyPeriodSetting;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = DateUtils.toDayStartSecond(startDate);
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = DateUtils.toDayStartSecond(endDate);
	}

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

	public Integer getOldCompanyId() {
		return oldCompanyId;
	}

	public void setOldCompanyId(Integer oldCompanyId) {
		this.oldCompanyId = oldCompanyId;
	}

	public Map<String, Map<String, CompanyCapacityCell>> getCompanyPeriodSetting() {
		return companyPeriodSetting;
	}

	public void setCompanyPeriodSetting(Map<String, Map<String, CompanyCapacityCell>> companyPeriodSetting) {
		this.companyPeriodSetting = companyPeriodSetting;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyPeriodCapacityDto [companyId=");
		builder.append(companyId);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", companyPeriodSetting=");
		builder.append(companyPeriodSetting);
		builder.append("]");
		return builder.toString();
	}
}
