package com.tijiantest.model.channel;

import java.util.List;

import com.tijiantest.model.account.Account;
import com.tijiantest.model.company.ChannelCompany;
import com.tijiantest.model.hospital.Hospital;

/**
 * 渠道商订单管理页面获取条件（医院列表和单位列表）
 */
public class HospitalsCompanysManagersVO {
	private List<Hospital> hospitals;// 医院
	private List<ChannelCompany> companies;// 单位
	private List<Account> managers;// 客户经理

	public List<Hospital> getHospitals() {
		return hospitals;
	}

	public void setHospitals(List<Hospital> hospitals) {
		this.hospitals = hospitals;
	}

	public List<ChannelCompany> getCompanies() {
		return companies;
	}

	public void setCompanies(List<ChannelCompany> companies) {
		this.companies = companies;
	}

	public List<Account> getManagers() {
		return managers;
	}

	public void setManagers(List<Account> managers) {
		this.managers = managers;
	}

}
