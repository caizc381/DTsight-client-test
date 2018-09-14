package com.tijiantest.model.company;

import java.io.Serializable;

/**
 * 医院单位VO
 * @author admin
 *
 */
public class HospitalCompanyVO extends CompanyVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3930988404766002091L;

	private Integer platformCompanyId;//平台单位id
	private Integer organizationId;//机构id
	private String organizationName;//机构名称
	private Integer organizationType;//机构类型
	
	private Double discount;//折扣
	private Boolean showReport;//是否展示报告
	private Boolean employeeImport;//是否支持员工导入
	private Integer settlementMode;//结算方式
	private String hisName;//his单位名称
	private Boolean advanceExportOrder;//是否提前导出
	
	private Boolean sendExamSms;//是否发生检前短信
	private Integer sendExamSmsDays;//检前短信提前发送天数
	private String pinyin;//拼音
	private Boolean deleted;//删除标记，status映射到该字段
	private String employeePrefix;//员工号前缀
	private String examinationAddress;//体检地址
	private Integer examreportIntervalTime;//体检报告设置间隔时间对用户可见
	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}
	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}
	public Integer getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public Integer getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Boolean getShowReport() {
		return showReport;
	}
	public void setShowReport(Boolean showReport) {
		this.showReport = showReport;
	}
	public Boolean getEmployeeImport() {
		return employeeImport;
	}
	public void setEmployeeImport(Boolean employeeImport) {
		this.employeeImport = employeeImport;
	}
	public Integer getSettlementMode() {
		return settlementMode;
	}
	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}
	public String getHisName() {
		return hisName;
	}
	public void setHisName(String hisName) {
		this.hisName = hisName;
	}
	public Boolean getAdvanceExportOrder() {
		return advanceExportOrder;
	}
	public void setAdvanceExportOrder(Boolean advanceExportOrder) {
		this.advanceExportOrder = advanceExportOrder;
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
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public String getEmployeePrefix() {
		return employeePrefix;
	}
	public void setEmployeePrefix(String employeePrefix) {
		this.employeePrefix = employeePrefix;
	}
	public String getExaminationAddress() {
		return examinationAddress;
	}
	public void setExaminationAddress(String examinationAddress) {
		this.examinationAddress = examinationAddress;
	}
	public Integer getExamreportIntervalTime() {
		return examreportIntervalTime;
	}
	public void setExamreportIntervalTime(Integer examreportIntervalTime) {
		this.examreportIntervalTime = examreportIntervalTime;
	}
	
	
}
