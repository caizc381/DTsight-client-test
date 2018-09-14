package com.tijiantest.model.company;

import java.util.List;

public class ExamCompanyVo {

	private HospitalCompany hospitalCompany;

	private List<CompanyManagerVo> companyManagerRelList;

	private CompanyHisRelationDto companyHisRelationDto;

	private Integer hospitalId;

	public List<CompanyManagerVo> getCompanyManagerRelList() {
		return companyManagerRelList;
	}

	public void setCompanyManagerRelList(List<CompanyManagerVo> companyManagerRelList) {
		this.companyManagerRelList = companyManagerRelList;
	}

	public CompanyHisRelationDto getCompanyHisRelationDto() {
		return companyHisRelationDto;
	}

	public void setCompanyHisRelationDto(CompanyHisRelationDto companyHisRelationDto) {
		this.companyHisRelationDto = companyHisRelationDto;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public HospitalCompany getHospitalCompany() {
		return hospitalCompany;
	}

	public void setHospitalCompany(HospitalCompany hospitalCompany) {
		this.hospitalCompany = hospitalCompany;
	}

}
