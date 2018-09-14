package com.tijiantest.model.counter;

import java.util.List;

public class CountForCheck {

	public List<HospitalCapacityUsed> hospitalCount;
	
	public List<CompanyCapacityUsed> companyCount;

	public List<HospitalCapacityUsed> getHospitalCount() {
		return hospitalCount;
	}

	public void setHospitalCount(List<HospitalCapacityUsed> hospitalCount) {
		this.hospitalCount = hospitalCount;
	}

	public List<CompanyCapacityUsed> getCompanyCount() {
		return companyCount;
	}

	public void setCompanyCount(List<CompanyCapacityUsed> companyCount) {
		this.companyCount = companyCount;
	}
}
