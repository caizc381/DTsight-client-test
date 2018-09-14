package com.tijiantest.model.counter;

import java.util.Date;

import com.tijiantest.util.key.PrimaryKey;


public class CompanyCapacityKey implements PrimaryKey{

	/**
	 * 体检单位
	 */
	private Integer companyId;
	
	/**
	 * 体检中心
	 */
	private Integer hospitalId;
	
	/**
	 * 分时段id
	 */
	private Integer periodId;
	
	/**
	 * 日期
	 */
	private Date currentDate;
	/**
	 * 体检项目
	 */
	private Integer examItem;
	
	public CompanyCapacityKey(Integer companyId, Integer hospitalId, Integer periodId, Integer examItem, Date currentDate) {
		this.companyId = companyId; 
		this.hospitalId = hospitalId;
		this.periodId = periodId;
		this.currentDate = currentDate;
		this.examItem = examItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((currentDate == null) ? 0 : currentDate.hashCode());
		result = prime * result + ((examItem == null) ? 0 : examItem.hashCode());
		result = prime * result + ((hospitalId == null) ? 0 : hospitalId.hashCode());
		result = prime * result + ((periodId == null) ? 0 : periodId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompanyCapacityKey other = (CompanyCapacityKey) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (currentDate == null) {
			if (other.currentDate != null)
				return false;
		} else if (!currentDate.equals(other.currentDate))
			return false;
		if (examItem == null) {
			if (other.examItem != null)
				return false;
		} else if (!examItem.equals(other.examItem))
			return false;
		if (hospitalId == null) {
			if (other.hospitalId != null)
				return false;
		} else if (!hospitalId.equals(other.hospitalId))
			return false;
		if (periodId == null) {
			if (other.periodId != null)
				return false;
		} else if (!periodId.equals(other.periodId))
			return false;
		return true;
	}
	
}
