package com.tijiantest.model.company;

public class ExamCompanyDto extends Company {

	private boolean sendSMSWhenShowReport;
	private boolean sendWeixinWhenShowReport;
	private String crmCompanyName;

	public boolean isSendSMSWhenShowReport() {
		return sendSMSWhenShowReport;
	}

	public void setSendSMSWhenShowReport(boolean sendSMSWhenShowReport) {
		this.sendSMSWhenShowReport = sendSMSWhenShowReport;
	}

	public boolean isSendWeixinWhenShowReport() {
		return sendWeixinWhenShowReport;
	}

	public void setSendWeixinWhenShowReport(boolean sendWeixinWhenShowReport) {
		this.sendWeixinWhenShowReport = sendWeixinWhenShowReport;
	}

	public String getCrmCompanyName() {
		return crmCompanyName;
	}

	public void setCrmCompanyName(String crmCompanyName) {
		this.crmCompanyName = crmCompanyName;
	}

}
