/**
 * 
 */
package com.tijiantest.model.counter;


/**
 * @author ren
 *
 */
public class HospitalCapacity {
	// '自增主键id'
	private Integer id;
	
	// '时段关联体检中心'
	private Integer hospitalId; 
	
	// '分时段id'
	private Integer periodId;
	
	// '体检项目id，-1：全量项目'
	private Integer examItem;
	
	// '当前可预约个数'
    private Integer availableNum;  
    
    // '总共可预约个数'
    private Integer maxNum;
    
    private int configType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getExamItem() {
		return examItem;
	}

	public void setExamItem(Integer examItem) {
		this.examItem = examItem;
	}

	public Integer getAvailableNum() {
		return availableNum;
	}

	public void setAvailableNum(Integer availableNum) {
		this.availableNum = availableNum;
	}

	public Integer getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(Integer maxNum) {
		this.maxNum = maxNum;
	}

	public int getConfigType() {
		return configType;
	}

	public void setConfigType(int configType) {
		this.configType = configType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HospitalCapacity [id=");
		builder.append(id);
		builder.append(", hospitalId=");
		builder.append(hospitalId);
		builder.append(", periodId=");
		builder.append(periodId);
		builder.append(", examItem=");
		builder.append(examItem);
		builder.append(", availableNum=");
		builder.append(availableNum);
		builder.append(", maxNum=");
		builder.append(maxNum);
		builder.append(", configType=");
		builder.append(configType);
		builder.append("]");
		return builder.toString();
	}
}
