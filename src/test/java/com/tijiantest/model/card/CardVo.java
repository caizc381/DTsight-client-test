package com.tijiantest.model.card;

public class CardVo extends Card{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2464901599745660281L;
    private String firstHospitalName;
    private Integer hospitalCount;
	public String getFirstHospitalName() {
		return firstHospitalName;
	}
	public void setFirstHospitalName(String firstHospitalName) {
		this.firstHospitalName = firstHospitalName;
	}
	public Integer getHospitalCount() {
		return hospitalCount;
	}
	public void setHospitalCount(Integer hospitalCount) {
		this.hospitalCount = hospitalCount;
	}
    
}
