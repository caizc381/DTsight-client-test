/**
 * 
 */
package com.tijiantest.model.counter;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.tijiantest.util.key.PrimaryKeyGenerator;



/**
 * @author ren
 *
 */
public class HospitalCapacityConfig extends HospitalCapacity implements PrimaryKeyGenerator{
	
	// 周几，1:周日 2:周一 3:周二 4:周三 5:周四 6:周五 7:周六
	private Integer dayOfWeek;
	
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public HospitalCapacityConfigKey getPrimaryKey() {
		return new HospitalCapacityConfigKey(getHospitalId(), getPeriodId(), getExamItem(), getDayOfWeek());
	}
}
