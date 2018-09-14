package com.tijiantest.model.company;

import java.io.Serializable;

public class PlatformCompHospitalApply implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4782003706326012308L;
	private Integer companyId;
	private Integer hospitalId;
	private String hospitalName;
	private Double discount;
	private Integer status;
	private String settingDetail;
	//for manage 
	private boolean showReport;
	
    /**
     * 是否发送检前短信
     */
    private Boolean sendExamSms;
    /**
     * 提前几天发送检前短信
     */
    private Integer sendExamSmsDays;
	
	public boolean isShowReport() {
		return showReport;
	}
	public void setShowReport(boolean showReport) {
		this.showReport = showReport;
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
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSettingDetail() {
		return settingDetail;
	}
	public void setSettingDetail(String settingDetail) {
		this.settingDetail = settingDetail;
	}
	public Boolean getSendExamSms() {
		return sendExamSms;
	}
	public void setSendExamSms(Boolean sendExamSms) {
		this.sendExamSms = sendExamSms;
	}
	public Integer getSendExamSmsDays() {
		return sendExamSmsDays;
	}
	public void setSendExamSmsDays(Integer sendExamSmsDays) {
		this.sendExamSmsDays = sendExamSmsDays;
	}
	
	
}
