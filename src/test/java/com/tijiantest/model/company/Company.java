package com.tijiantest.model.company;

import com.tijiantest.util.PinYinUtil;

public class Company {
	private int id;
	private String name;
	private String pinYin;
	// 体检单位员工号前缀
	private String prefix;
	// P单位地区
	private String description;
	// 是否员工号导入
	private boolean employeeImport;

	private Integer hospitalId;
	private Double discount;
	private int type;
	// 体检报告对客户是否可见
	private boolean showReport;

	// 是否显示发票信息
	private boolean showInvoice;
	// 是否支持即时导入 1：是；0：否
	private boolean supportAnytimeImport;
	// 是否当作挂账单位
	private boolean asAccountCompany;
	// 结算方式，0：按项目 ； 1：按人数
	private Integer settlementMode;
	// 单位别名
	private String companyAlias;
	// 联系人
	private String contactName;
	// 联系电话
	private String contactTel;
	// 被n家医院引用
	private Integer referenceCountByHosp;
	// his单位名称
	private String hisName;

	/**
	 * 状态 获取from tb_hospital_exam_company_relation
	 */
	private int status = 1;

	/**
	 * 单位是否可以提前导出订单 得看医院是否深对接
	 */
	private Boolean advanceExportOrder;

    /**
    * 是否发送检前短信
    */
   private Boolean sendExamSms;
   /**
    * 提前几天发送检前短信
    */
   private Integer sendExamSmsDays;
   
   /**
	 * 体检报告设置间隔时间对用户可见
	 */
	private Integer examreportIntervalTime;
	/**
	 * 体检地址
	 */
	private String examinationAddress;   
	
	public Company() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Company(int id, String name, int type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public Company(String name, boolean employeeImport, Integer hospitalId, Double discount, int type,
			boolean showReport) {
		super();
		this.name = name;
		this.employeeImport = employeeImport;
		this.hospitalId = hospitalId;
		this.discount = discount;
		this.type = type;
		this.showReport = showReport;
		this.sendExamSms = true;
		this.sendExamSmsDays = 1;
	}

	public Company(String name, boolean employeeImport, Integer hospitalId, Double discount, int type,
			boolean showReport, boolean sendExamSms, Integer sendExamSmsDays) {
		super();
		this.name = name;
		this.employeeImport = employeeImport;
		this.hospitalId = hospitalId;
		this.discount = discount;
		this.type = type;
		this.showReport = showReport;
		this.sendExamSms = sendExamSms;
		this.sendExamSmsDays = sendExamSmsDays;
	}

	public Company(int id, String name, String pinYin, boolean employeeImport, Double discount, int type,
			boolean showReport, boolean showInvoice, boolean supportAnytimeImport, boolean asAccountCompany,
			Integer settlementMode, String companyAlias, Integer referenceCountByHosp) {
		super();
		this.id = id;
		this.name = name;
		this.pinYin = pinYin;
		this.employeeImport = employeeImport;
		this.discount = discount;
		this.type = type;
		this.showReport = showReport;
		this.showInvoice = showInvoice;
		this.supportAnytimeImport = supportAnytimeImport;
		this.asAccountCompany = asAccountCompany;
		this.settlementMode = settlementMode;
		this.companyAlias = companyAlias;
		this.referenceCountByHosp = referenceCountByHosp;
	}

	public Company(int id, String name, String pinYin, String description, boolean employeeImport, int type,
			boolean showReport, boolean showInvoice, boolean supportAnytimeImport, boolean asAccountCompany,
			Integer referenceCountByHosp) {
		super();
		this.id = id;
		this.name = name;
		this.pinYin = pinYin;
		this.description = description;
		this.employeeImport = employeeImport;
		this.type = type;
		this.showReport = showReport;
		this.showInvoice = showInvoice;
		this.supportAnytimeImport = supportAnytimeImport;
		this.asAccountCompany = asAccountCompany;
		this.referenceCountByHosp = referenceCountByHosp;
	}

	public Company(String name, String description, int type) {
		super();
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return PinYinUtil.fullWidth2halfWidth(name);
	}

	public void setName(String name) {
		this.name = PinYinUtil.fullWidth2halfWidth(name);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPinYin() {
		return pinYin;
	}

	public void setPinYin(String pinYin) {
		this.pinYin = pinYin;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEmployeeImport() {
		return employeeImport;
	}

	public void setEmployeeImport(boolean employeeImport) {
		this.employeeImport = employeeImport;
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

	public boolean isAsAccountCompany() {
		return asAccountCompany;
	}

	public void setAsAccountCompany(boolean asAccountCompany) {
		this.asAccountCompany = asAccountCompany;
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

	public Integer getReferenceCountByHosp() {
		return referenceCountByHosp;
	}

	public void setReferenceCountByHosp(Integer referenceCountByHosp) {
		this.referenceCountByHosp = referenceCountByHosp;
	}

	public String getHisName() {
		return hisName;
	}

	public void setHisName(String hisName) {
		this.hisName = hisName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public Integer getExamreportIntervalTime() {
		return examreportIntervalTime;
	}

	public void setExamreportIntervalTime(Integer examreportIntervalTime) {
		this.examreportIntervalTime = examreportIntervalTime;
	}

	public String getExaminationAddress() {
		return examinationAddress;
	}

	public void setExaminationAddress(String examinationAddress) {
		this.examinationAddress = examinationAddress;
	}

}
