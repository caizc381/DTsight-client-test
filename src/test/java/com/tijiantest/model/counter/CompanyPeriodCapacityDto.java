/**
 * 
 */
package com.tijiantest.model.counter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ren
 *
 */
public class CompanyPeriodCapacityDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8510056984517695935L;

	// 体检单位名称
    private String companyName;
	
	// 分时间段人数设置, <period, <examitem, CompanyCapacityCell>>
	private Map<Integer, Map<Integer, CompanyCapacityCell>> companyPeriodSetting;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Map<Integer, Map<Integer, CompanyCapacityCell>> getCompanyPeriodSetting() {
		return companyPeriodSetting;
	}

	public void setCompanyPeriodSetting(Map<Integer, Map<Integer, CompanyCapacityCell>> companyPeriodSetting) {
		this.companyPeriodSetting = companyPeriodSetting;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyPeriodCapacityDto [companyName=");
		builder.append(companyName);
		builder.append(", companyPeriodSetting=");
		builder.append(companyPeriodSetting);
		builder.append("]");
		return builder.toString();
	}
}
