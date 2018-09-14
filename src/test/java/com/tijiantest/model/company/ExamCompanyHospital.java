package com.tijiantest.model.company;

import java.io.Serializable;

public class ExamCompanyHospital implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6991776234390098150L;
	
	private Integer hospitalId;
	private Double discount;
	private boolean employeeImport;//是否员工号导入
	private boolean showReport;//是否显示纸质报告选项
	private boolean showInvoice;//是否显示发票信息
	private boolean supportAnytimeImport;//是否支持即时导入：1-是；0-否	
	
	public ExamCompanyHospital() {
		super();
	}
	public ExamCompanyHospital(Double discount, boolean employeeImport, boolean showReport, boolean showInvoice,
			boolean supportAnytimeImport) {
		super();
		this.discount = discount;
		this.employeeImport = employeeImport;
		this.showReport = showReport;
		this.showInvoice = showInvoice;
		this.supportAnytimeImport = supportAnytimeImport;
	}
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
