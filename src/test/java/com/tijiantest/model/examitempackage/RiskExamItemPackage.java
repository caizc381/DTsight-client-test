package com.tijiantest.model.examitempackage;

public class RiskExamItemPackage extends ExamItemPackage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5817019559397040422L;
	private Integer riskId;
	private Integer minVal;
	private Integer maxVal;
	
	public Integer getRiskId() {
		return riskId;
	}
	public void setRiskId(Integer riskId) {
		this.riskId = riskId;
	}
	public Integer getMinVal() {
		return minVal;
	}
	public void setMinVal(Integer minVal) {
		this.minVal = minVal;
	}
	public Integer getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Integer maxVal) {
		this.maxVal = maxVal;
	}
	
	
}
