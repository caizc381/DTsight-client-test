/**
 * 
 */
package com.tijiantest.model.counter;

import java.util.Date;

import com.tijiantest.util.key.PrimaryKeyGenerator;
/**
 * @author ren
 *
 */
public class CompanyCapacityUsed implements PrimaryKeyGenerator{
	
	public static final Integer RELEASED = 1;
	public static final Integer NOT_RELEASE = 0;
	
	// 主键
	private Integer id;
	
	// 体检单位
	private Integer companyId;
	
	// 体检中心
	private Integer hospitalId;
	
	// 分时段id
	private Integer periodId;
	
	// 日期
	private Date currentDate;
	
	// 体检项目id，-1：全量项目
	private Integer examItem;
	
	// 已预约数
	private Integer usedNum; 

	// 预留数
	private Integer reservationNum; 
	
	//预留已使用量
	private Integer reservationUsedNum;
	
	// 限额
	private Integer maxNum;
	/**
	 * 预留是否被释放  1：是 0：否
	 */
	private Integer release;
	
	public Integer getRelease() {
		return release;
	}

	public void setRelease(Integer release) {
		this.release = release;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Integer getExamItem() {
		return examItem;
	}

	public void setExamItem(Integer examItem) {
		this.examItem = examItem;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getReservationNum() {
		return reservationNum;
	}

	public void setReservationNum(Integer reservationNum) {
		this.reservationNum = reservationNum;
	}

	public Integer getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(Integer maxNum) {
		this.maxNum = maxNum;
	}

	public Integer getReservationUsedNum() {
		return reservationUsedNum;
	}

	public void setReservationUsedNum(Integer reservationUsedNum) {
		this.reservationUsedNum = reservationUsedNum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyCapacityUsed [id=");
		builder.append(id);
		builder.append(", companyId=");
		builder.append(companyId);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", periodId=");
		builder.append(periodId);
		builder.append(", currentDate=");
		builder.append(currentDate);
		builder.append(", examItem=");
		builder.append(examItem);
		builder.append(", usedNum=");
		builder.append(usedNum);
		builder.append(", reservationNum=");
		builder.append(reservationNum);
		builder.append(", reservationUsedNum=");
		builder.append(reservationUsedNum);
		builder.append(", maxNum=");
		builder.append(maxNum);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public CompanyCapacityKey getPrimaryKey() {
		
		return new CompanyCapacityKey(companyId, hospitalId, periodId, examItem, currentDate);
	}
}
