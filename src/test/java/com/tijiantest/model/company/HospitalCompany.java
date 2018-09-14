package com.tijiantest.model.company;

import java.io.Serializable;

/**
 * 医院单位
 * @author yuefengyang
 *
 */
public class HospitalCompany extends BaseCompany implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4651845821349953410L;
	/**
	 * 平台单位ID
	 */
	private Integer platformCompanyId;
	
	/**
	 * 折扣
	 */
	private Double discount;
	/**
	 * 是否展示报告
	 */
	private Boolean showReport;

	/**
	 * 是否支持员工号导入
	 */
	@Deprecated
	private Boolean employeeImport;
	
	/**
	 * 结算方式
	 */
	private Integer settlementMode;

	/**
	 * his单位名
	 */
	private String hisName;
	/**
	 * 是否提前导出
	 */
	private Boolean advanceExportOrder;
	/**
	 * 是否发送检前短信
	 */
	private Boolean sendExamSms;
	
	/**
	 * 检前信息提前发送天数
	 */
	private Integer sendExamSmsDays;
	/**
	 * 拼音
	 */
	private String pinyin;
	/**
	 * 删除标记，status映射到该字段
	 */
	private Boolean deleted;

	/**
	 * 员工号前缀，使用单位id作为前缀
	 */
	@Deprecated
	private String employeePrefix;
	
	/**
	 * 体检地址
	 */
	private String examinationAddress;
	
	/**
	 * 体检报告设置间隔时间对用户可见
	 */
	private Integer examreportIntervalTime;
	
	/**
	 * 联系人
	 */
	private String contactName;
	
	/**
	 * 联系方式
	 */
	private String contactTel;

	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}

	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	
}
