package com.tijiantest.model.company;

public class HospitalExamCompanyRelationDto {

	private Integer hospitalId;
	private Integer companyId;
	private Double discount;
	
	private boolean showReport;//体检报告对客户是否可见
	private Boolean sendExamSms;//是否发送检前短信
	private Integer sendExamSmsDays;//提前几天发送检前短信。现在是提前一天，不用设置，由定时任务发送
	private boolean showInvoice;//是否显示发票信息
	private boolean supportAnytimeImport;//是否支持即时导入 1：是；0：否
	
	private Integer settlementMode;//结算方式，0：按项目；1：按人数
	private String companyAlias;//单位别名
	private String examinationAddress;//体检地址
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public boolean isShowReport() {
		return showReport;
	}
	public void setShowReport(boolean showReport) {
		this.showReport = showReport;
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
	public boolean isShowInvoice() {
		return showInvoice;
	}
	public void setShowInvoice(boolean showInvoice) {
		this.showInvoice = showInvoice;
	}
	public boolean isSupportAnytimeImport() {
		return supportAnytimeImport;
	}
	public void setSupportAnytimeImport(boolean supportAnytimeImport) {
		this.supportAnytimeImport = supportAnytimeImport;
	}
	public Integer getSettlementMode() {
		return settlementMode;
	}
	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}
	public String getCompanyAlias() {
		return companyAlias;
	}
	public void setCompanyAlias(String companyAlias) {
		this.companyAlias = companyAlias;
	}
	public String getExaminationAddress() {
		return examinationAddress;
	}
	public void setExaminationAddress(String examinationAddress) {
		this.examinationAddress = examinationAddress;
	}
	
	
	
}
