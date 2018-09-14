package com.tijiantest.model.company;

import java.util.List;

import com.tijiantest.model.hospital.Hospital;

public class ChannelCompanyVO {

	private ChannelCompany channelCompany;

	private Hospital hospital;

	private List<ManagerExamCompanyRelation> companyManagerList;

	public ChannelCompany getChannelCompany() {
		return channelCompany;
	}

	public void setChannelCompany(ChannelCompany channelCompany) {
		this.channelCompany = channelCompany;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public List<ManagerExamCompanyRelation> getCompanyManagerList() {
		return companyManagerList;
	}

	public void setCompanyManagerList(
			List<ManagerExamCompanyRelation> companyManagerList) {
		this.companyManagerList = companyManagerList;
	}

}
