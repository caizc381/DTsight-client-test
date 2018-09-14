package com.tijiantest.model.counter;

import java.util.Date;

import com.tijiantest.util.key.PrimaryKeyGenerator;

/**
 * @author ren
 *
 */
public class HospitalCapacityUsed extends HospitalCapacity implements PrimaryKeyGenerator {
	// '日期'
	private Date currentDate;

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HospitalCapacityUsed [currentDate=");
		builder.append(currentDate);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getHospitalId()=");
		builder.append(getHospitalId());
		builder.append(", getPeriodId()=");
		builder.append(getPeriodId());
		builder.append(", getExamItem()=");
		builder.append(getExamItem());
		builder.append(", getAvailableNum()=");
		builder.append(getAvailableNum());
		builder.append(", getMaxNum()=");
		builder.append(getMaxNum());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public HospitalCapacityUsedKey getPrimaryKey() {
		return new HospitalCapacityUsedKey(getHospitalId(), getPeriodId(), getExamItem(), currentDate);
	}

}
