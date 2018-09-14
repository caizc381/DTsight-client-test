package com.tijiantest.model.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamCompanyHospitalDto extends Company implements Serializable {

	private static final long serialVersionUID = 8748986885873824715L;

	private List<ExamCompanyHospital> companyHospitalList = new ArrayList<ExamCompanyHospitalDto.ExamCompanyHospital>();

	public List<ExamCompanyHospital> getCompanyHospitalList() {
		return companyHospitalList;
	}

	public void addCompanyHospital(Integer hospitalId, Double discount, boolean employeeImport, boolean showReport,
			boolean showInvoice, Boolean sendExamSms, Integer sendExamSmsDays) {
		ExamCompanyHospital eHospital = new ExamCompanyHospital();
		eHospital.setHospitalId(hospitalId);
		eHospital.setDiscount(discount);
		eHospital.setEmployeeImport(employeeImport);
		eHospital.setShowReport(showReport);
		eHospital.setShowInvoice(showInvoice);
		eHospital.setSendExamSms(sendExamSms);
		eHospital.setSendExamSmsDays(sendExamSmsDays);
		companyHospitalList.add(eHospital);

	}

	public class ExamCompanyHospital implements Serializable {

		private static final long serialVersionUID = 5502075366766678524L;

		private Integer hospitalId;
		private Double discount;
		private boolean employeeImport;// 是否员工号导入
		private boolean showReport;// 是否显示纸质报告选项
		private Boolean sendExamSms;// 是否发送检前短信
		private Integer sendExamSmsDays;// 提前几天发送检前短信
		private boolean showInvoice;// 是否显示发票信息
		private boolean supportAnytimeImport;// 是否支持即时导入 1-是；0-否

		public Integer getHospitalId() {
			return hospitalId;
		}

		public void setHospitalId(Integer hospitalId) {
			this.hospitalId = hospitalId;
		}

		public Double getDiscount() {
			return discount;
		}

		public void setDiscount(Double discount) {
			this.discount = discount;
		}

		public boolean isEmployeeImport() {
			return employeeImport;
		}

		public void setEmployeeImport(boolean employeeImport) {
			this.employeeImport = employeeImport;
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
	}
}
