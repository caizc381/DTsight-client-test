package com.tijiantest.model.counter;

import com.tijiantest.util.key.PrimaryKey;

public class HospitalCapacityConfigKey implements PrimaryKey {

	/**
	 * 时段关联体检中心
	 */
	private Integer hospitalId;

	/**
	 * 分时段id
	 */
	private Integer periodId;

	/**
	 * 体检项目id，-1：全量项目
	 */
	private Integer examItem;
	
	private Integer dayOfWeek;

	public HospitalCapacityConfigKey(Integer hospitalId, Integer periodId, Integer examItem, Integer dayOfWeek) {
		this.hospitalId = hospitalId;
		this.periodId = periodId;
		this.examItem = examItem;
		this.dayOfWeek = dayOfWeek;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
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
		HospitalCapacityConfigKey other = (HospitalCapacityConfigKey) obj;
		if (dayOfWeek == null) {
			if (other.dayOfWeek != null)
				return false;
		} else if (!dayOfWeek.equals(other.dayOfWeek))
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
