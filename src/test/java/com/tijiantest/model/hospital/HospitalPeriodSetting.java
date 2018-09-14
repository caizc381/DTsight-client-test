package com.tijiantest.model.hospital;
public class HospitalPeriodSetting {

	private Integer id;
	
	private Integer hospitalId;
	
	private String name;
	
	/**
	 * 内部使用时段 true:仅限内部使用  false:无限制
	 */
	@SuppressWarnings("unused")
	private Boolean internalUsePeriod;
	

	public HospitalPeriodSetting(){}
	public HospitalPeriodSetting(String name,Integer hospitalId){
		this.name = name;
		this.hospitalId = hospitalId;
	}
	
	
	public HospitalPeriodSetting(Integer hospitalId, String name, Boolean internalUsePeriod) {
		super();
		this.hospitalId = hospitalId;
		this.name = name;
		this.internalUsePeriod = internalUsePeriod;
	}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}