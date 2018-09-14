package com.tijiantest.model.hospital;

import com.tijiantest.util.PinYinUtil;

public class HospitalSettings extends OrganizationSettings {

	private static final long serialVersionUID = 2475007826183249973L;

	private Integer basicMealId;

	// 是否需要纸质报告 for main-site
	private Boolean showExamReport;

	/**
	 * 手机现场下单,默认false
	 */
	private Boolean mobileFieldOrder = false;

	/**
	 * 自动确认订单
	 */
	private Boolean autoConfirmOrder;
	/**
	 * 对接单位
	 */
	private String cooperateCompany;
	/**
	 * 对接方式,0:无缝,1:有缝
	 */
	private Integer cooperateType;
	/**
	 * 自动导出订单
	 */
	private Boolean autoExportOrder;

	/**
	 * 支持加项折扣
	 */
	private Boolean supportExtDiscount;

	/**
	 * 散客挂账的体检单位名称
	 */
	private String guestOnlineCompAlias;

	/**
	 * 散客现场付款的体检单位名称
	 */
	private String guestOfflineCompAlias;

	/**
	 * 预约成功是否向客户发送短信 0:否、1：是
	 */
	private Boolean isSendMessage;

	/**
	 * 单位结算方式默认 0:按项目、1：按人数
	 */
	private Integer settlementMode;

	/**
	 * 单位预留默认值 0:仅预留日可约、1：非预留日可约
	 */
	private Boolean reserveDayAvailable;

	/**
	 * 是否自动导出订单到邮件
	 */
	private Boolean isAutoExportOrderToEmails;

	/**
	 * 是否提前导出单位订单
	 */
	private Boolean isAdvanceExportCompanyOrder;

	private Boolean openPrintExamGuid;// 是否开通打印导检单
	private Boolean openQueue;// 是否开通队列

	private Boolean showItemPrice;
	private Integer enableDatePeriod;
	private String calculatorService;

	/**
	 * 是否显示开票
	 */
	private Integer showInvoice;
	/**
	 * 是否开票
	 */
	private Boolean makeOutInvoice;
	/**
	 * 开票要求，0:普通，1:高
	 */
	private Integer invoiceRequired;
	private Integer previousBookDays;
	private String previousBookTime;
	private Integer previousExportDays;
	private Integer vipPrice;
	private Integer autoReleaseDays;

	private String examStartTime;
	private String examEndTime;
	private Boolean onlyLocalePay;

	/**
	 * 是否发送检前短信
	 */
	private Boolean sendExamSms;
	/**
	 * 提前几天发送检前短信
	 */
	private Integer sendExamSmsDays;
	/**
	 * 发送检前短信时间点
	 */
	private String sendExamSmsTime;
	/**
	 * 拒检项目退款
	 */
	private Boolean refundRefusedItem;

	/**
	 * 每天健康散客单位别名：默认每天健康
	 */
	private String mGuestCompAlias;

	/**
	 * 是否可以手动导出订单到体检中心
	 */
	private Boolean manualExportOrder;

	/**
	 * 是否需要导出为xls
	 */
	private Boolean exportWithXls;

	/**
	 * 是否需要确认现场付款
	 */
	private Boolean needLocalPay;
	/**
	 * 单位体检报告是否可见，默认为true
	 */
	private Boolean showCompanyReport;

	/**
	 * 体检中心是否开通同步单位功能
	 */
	private Boolean openSyncCompany;

	/**
	 * 体检中心是否开通同步套餐功能
	 */
	private Boolean openSyncMeal;

	private String previousExportTime;

	/**
	 * 是否开通团检报告
	 */
	private Boolean openGroupExamReport;

	/**
	 * 自动导出订单的邮件地址
	 */
	private String autoExportOrderEmails;

	private Boolean isSmartRecommend;// 智能荐项是否开启，默认关闭

	/**
	 * 提供个性化套餐
	 */
	private Boolean provideIndividuationMeal;

	/**
	 * 手机支付时的提示文字
	 */
	private String payTipText;

	private String promptPageUrl;// 提示页面网址

	private Boolean exportWithNoExamDate;// 是否支持导出无体检日期订单：0 不支持；1 支持

	private Integer examreportIntervalTime;// 体检报告设置间隔时间对用户可见,
											// 0:立即，1：1天，2：2天，以此类推

	private String bookPromptText;// 前端预约温馨提示

	private Boolean fastbookCanPrintChecklist;// 极速预约能否打印预检单
	private Boolean canPrintChecklist;// 体检中心能否打印预检单

	public Boolean getAdvanceExportCompanyOrder() {
		return isAdvanceExportCompanyOrder;
	}

	public void setAdvanceExportCompanyOrder(Boolean autoExportCompanyOrder) {
		isAdvanceExportCompanyOrder = autoExportCompanyOrder;
	}

	public Boolean getOpenPrintExamGuid() {
		return openPrintExamGuid;
	}

	public void setOpenPrintExamGuid(Boolean openPrintExamGuid) {
		this.openPrintExamGuid = openPrintExamGuid;
	}

	public Boolean getOpenQueue() {
		return openQueue;
	}

	public void setOpenQueue(Boolean openQueue) {
		this.openQueue = openQueue;
	}

	public Integer getBasicMealId() {
		return basicMealId;
	}

	public void setBasicMealId(Integer basicMealId) {
		this.basicMealId = basicMealId;
	}

	public Boolean getShowExamReport() {
		return showExamReport;
	}

	public void setShowExamReport(Boolean showExamReport) {
		this.showExamReport = showExamReport;
	}

	public Boolean getAutoConfirmOrder() {
		return autoConfirmOrder;
	}

	public void setAutoConfirmOrder(Boolean autoConfirmOrder) {
		this.autoConfirmOrder = autoConfirmOrder;
	}

	public String getCooperateCompany() {
		return cooperateCompany;
	}

	public void setCooperateCompany(String cooperateCompany) {
		this.cooperateCompany = cooperateCompany;
	}

	public Integer getCooperateType() {
		return cooperateType;
	}

	public void setCooperateType(Integer cooperateType) {
		this.cooperateType = cooperateType;
	}

	public Boolean getAutoExportOrder() {
		return autoExportOrder;
	}

	public void setAutoExportOrder(Boolean autoExportOrder) {
		this.autoExportOrder = autoExportOrder;
	}

	public Boolean getIsAutoExportOrderToEmails() {
		return isAutoExportOrderToEmails;
	}

	public void setIsAutoExportOrderToEmails(Boolean isAutoExportOrderToEmails) {
		this.isAutoExportOrderToEmails = isAutoExportOrderToEmails;
	}

	public Boolean getSupportExtDiscount() {
		return supportExtDiscount == null ? false : supportExtDiscount;
	}

	public void setSupportExtDiscount(Boolean supportExtDiscount) {
		this.supportExtDiscount = supportExtDiscount;
	}

	public String getGuestOnlineCompAlias() {
		return PinYinUtil.fullWidth2halfWidth(guestOnlineCompAlias);
	}

	public void setGuestOnlineCompAlias(String guestOnlineCompAlias) {
		this.guestOnlineCompAlias = PinYinUtil.fullWidth2halfWidth(guestOnlineCompAlias);
	}

	public String getGuestOfflineCompAlias() {
		return PinYinUtil.fullWidth2halfWidth(guestOfflineCompAlias);
	}

	public void setGuestOfflineCompAlias(String guestOfflineCompAlias) {
		this.guestOfflineCompAlias = PinYinUtil.fullWidth2halfWidth(guestOfflineCompAlias);
	}

	public Boolean getIsSendMessage() {
		return isSendMessage;
	}

	public void setIsSendMessage(Boolean isSendMessage) {
		this.isSendMessage = isSendMessage;
	}

	public Boolean getReserveDayAvailable() {
		return reserveDayAvailable;
	}

	public void setReserveDayAvailable(Boolean reserveDayAvailable) {
		this.reserveDayAvailable = reserveDayAvailable;
	}

	public Boolean getMobileFieldOrder() {
		return mobileFieldOrder;
	}

	public void setMobileFieldOrder(Boolean mobileFieldOrder) {
		this.mobileFieldOrder = mobileFieldOrder;
	}

	public Integer getSettlementMode() {
		return settlementMode;
	}

	public void setSettlementMode(Integer settlementMode) {
		this.settlementMode = settlementMode;
	}

	public Integer getEnableDatePeriod() {
		return enableDatePeriod;
	}

	public void setEnableDatePeriod(Integer enableDatePeriod) {
		this.enableDatePeriod = enableDatePeriod;
	}

	public String getCalculatorService() {
		return calculatorService;
	}

	public void setCalculatorService(String calculatorService) {
		this.calculatorService = calculatorService;
	}

	public Boolean getShowItemPrice() {
		return showItemPrice;
	}

	public void setShowItemPrice(Boolean showItemPrice) {
		this.showItemPrice = showItemPrice;
	}

	public Integer getShowInvoice() {
		return showInvoice;
	}

	public void setShowInvoice(Integer showInvoice) {
		this.showInvoice = showInvoice;
	}

	public Integer getPreviousBookDays() {
		return previousBookDays;
	}

	public void setPreviousBookDays(Integer previousBookDays) {
		this.previousBookDays = previousBookDays;
	}

	public String getPreviousBookTime() {
		return previousBookTime;
	}

	public void setPreviousBookTime(String previousBookTime) {
		this.previousBookTime = previousBookTime;
	}

	public Integer getPreviousExportDays() {
		return previousExportDays;
	}

	public void setPreviousExportDays(Integer previousExportDays) {
		this.previousExportDays = previousExportDays;
	}

	public Integer getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(Integer vipPrice) {
		this.vipPrice = vipPrice;
	}

	public Integer getAutoReleaseDays() {
		return autoReleaseDays;
	}

	public void setAutoReleaseDays(Integer autoReleaseDays) {
		this.autoReleaseDays = autoReleaseDays;
	}

	public String getExamStartTime() {
		return examStartTime;
	}

	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}

	public String getExamEndTime() {
		return examEndTime;
	}

	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}

	public Boolean getOnlyLocalePay() {
		return onlyLocalePay;
	}

	public void setOnlyLocalePay(Boolean onlyLocalePay) {
		this.onlyLocalePay = onlyLocalePay;
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

	public String getSendExamSmsTime() {
		return sendExamSmsTime;
	}

	public void setSendExamSmsTime(String sendExamSmsTime) {
		this.sendExamSmsTime = sendExamSmsTime;
	}

	public Boolean getRefundRefusedItem() {
		return refundRefusedItem;
	}

	public void setRefundRefusedItem(Boolean refundRefusedItem) {
		this.refundRefusedItem = refundRefusedItem;
	}

	public String getmGuestCompAlias() {
		return PinYinUtil.fullWidth2halfWidth(mGuestCompAlias);
	}

	public void setmGuestCompAlias(String mGuestCompAlias) {
		this.mGuestCompAlias = PinYinUtil.fullWidth2halfWidth(mGuestCompAlias);
	}

	public Boolean getManualExportOrder() {
		return manualExportOrder;
	}

	public void setManualExportOrder(Boolean manualExportOrder) {
		this.manualExportOrder = manualExportOrder;
	}

	public Boolean getExportWithXls() {
		return exportWithXls;
	}

	public void setExportWithXls(Boolean exportWithXls) {
		this.exportWithXls = exportWithXls;
	}

	public Boolean getMakeOutInvoice() {
		return makeOutInvoice;
	}

	public void setMakeOutInvoice(Boolean makeOutInvoice) {
		this.makeOutInvoice = makeOutInvoice;
	}

	public Integer getInvoiceRequired() {
		return invoiceRequired;
	}

	public void setInvoiceRequired(Integer invoiceRequired) {
		this.invoiceRequired = invoiceRequired;
	}

	public Boolean getNeedLocalPay() {
		return needLocalPay;
	}

	public void setNeedLocalPay(Boolean needLocalPay) {
		this.needLocalPay = needLocalPay;
	}

	public Boolean getShowCompanyReport() {
		return showCompanyReport;
	}

	public void setShowCompanyReport(Boolean showCompanyReport) {
		this.showCompanyReport = showCompanyReport;
	}

	public String getPreviousExportTime() {
		return previousExportTime;
	}

	public void setPreviousExportTime(String previousExportTime) {
		this.previousExportTime = previousExportTime;
	}

	public Boolean getOpenGroupExamReport() {
		return openGroupExamReport;
	}

	public void setOpenGroupExamReport(Boolean openGroupExamReport) {
		this.openGroupExamReport = openGroupExamReport;
	}

	public String getAutoExportOrderEmails() {
		return autoExportOrderEmails;
	}

	public void setAutoExportOrderEmails(String autoExportOrderEmails) {
		this.autoExportOrderEmails = autoExportOrderEmails;
	}

	public Boolean getIsSmartRecommend() {
		return isSmartRecommend;
	}

	public void setIsSmartRecommend(Boolean isSmartRecommend) {
		this.isSmartRecommend = isSmartRecommend;
	}

	public Boolean getOpenSyncCompany() {
		return openSyncCompany;
	}

	public void setOpenSyncCompany(Boolean openSyncCompany) {
		this.openSyncCompany = openSyncCompany;
	}

	public Boolean getOpenSyncMeal() {
		return openSyncMeal;
	}

	public void setOpenSyncMeal(Boolean openSyncMeal) {
		this.openSyncMeal = openSyncMeal;
	}

	public Boolean getIsAdvanceExportCompanyOrder() {
		return isAdvanceExportCompanyOrder;
	}

	public void setIsAdvanceExportCompanyOrder(Boolean isAdvanceExportCompanyOrder) {
		this.isAdvanceExportCompanyOrder = isAdvanceExportCompanyOrder;
	}

	public Boolean getProvideIndividuationMeal() {
		return provideIndividuationMeal;
	}

	public void setProvideIndividuationMeal(Boolean provideIndividuationMeal) {
		this.provideIndividuationMeal = provideIndividuationMeal;
	}

	public String getPayTipText() {
		return payTipText;
	}

	public void setPayTipText(String payTipText) {
		this.payTipText = payTipText;
	}

	public String getPromptPageUrl() {
		return promptPageUrl;
	}

	public void setPromptPageUrl(String promptPageUrl) {
		this.promptPageUrl = promptPageUrl;
	}

	public Boolean getExportWithNoExamDate() {
		return exportWithNoExamDate;
	}

	public void setExportWithNoExamDate(Boolean exportWithNoExamDate) {
		this.exportWithNoExamDate = exportWithNoExamDate;
	}

	public String getBookPromptText() {
		return bookPromptText;
	}

	public void setBookPromptText(String bookPromptText) {
		this.bookPromptText = bookPromptText;
	}

	public HospitalSettings(Integer basicMealId) {
		this.basicMealId = basicMealId;
	}

	public Integer getExamreportIntervalTime() {
		return examreportIntervalTime;
	}

	public void setExamreportIntervalTime(Integer examreportIntervalTime) {
		this.examreportIntervalTime = examreportIntervalTime;
	}

	public Boolean getFastbookCanPrintChecklist() {
		return fastbookCanPrintChecklist;
	}

	public void setFastbookCanPrintChecklist(Boolean fastbookCanPrintChecklist) {
		this.fastbookCanPrintChecklist = fastbookCanPrintChecklist;
	}

	public Boolean getCanPrintChecklist() {
		return canPrintChecklist;
	}

	public void setCanPrintChecklist(Boolean canPrintChecklist) {
		this.canPrintChecklist = canPrintChecklist;
	}

	public HospitalSettings(int hospitalId, Boolean isSendMessage, int settlementMode, Boolean reserveDayAvailable,
			Boolean showCompanyReport) {
		// TODO Auto-generated constructor stub
		this.hospitalId = hospitalId;
		this.isSendMessage = isSendMessage;
		this.settlementMode = settlementMode;
		this.reserveDayAvailable = reserveDayAvailable;
		this.showCompanyReport = showCompanyReport;
	}

	public HospitalSettings(Integer hospitalId, boolean showItemPrice, boolean showExamReport, Integer showInvoice,
			boolean makeOutInvoice, boolean mobileFieldOrder, Integer invoiceRequired, Integer previousBookDays,
			String previousBookTime, Integer previousExportDays, Integer vipPrice, Integer autoReleaseDays,
			boolean onlyLocalePay, boolean sendExamSms, Integer sendExamSmsDays, String sendExamSmsTime,
			boolean supportExtDiscount, String guestOnlineCompAlias, String guestOfflineCompAlias,
			boolean acceptOfflinePay, String mGuestCompAlias, boolean needLocalPay, boolean secondSiteSwitch,
			String payTipText) {
		// TODO Auto-generated constructor stub
		this.hospitalId = hospitalId;
		this.showItemPrice = showItemPrice;
		this.showExamReport = showExamReport;
		this.showInvoice = showInvoice;
		this.makeOutInvoice = makeOutInvoice;
		this.mobileFieldOrder = mobileFieldOrder;
		this.invoiceRequired = invoiceRequired;
		this.previousBookDays = previousBookDays;
		this.previousBookTime = previousBookTime;
		this.previousExportDays = previousExportDays;
		this.vipPrice = vipPrice;
		this.autoReleaseDays = autoReleaseDays;
		this.onlyLocalePay = onlyLocalePay;
		this.sendExamSms = sendExamSms;
		this.sendExamSmsDays = sendExamSmsDays;
		this.sendExamSmsTime = sendExamSmsTime;
		this.supportExtDiscount = supportExtDiscount;
		this.guestOnlineCompAlias = guestOnlineCompAlias;
		this.guestOfflineCompAlias = guestOfflineCompAlias;
		this.acceptOfflinePay = acceptOfflinePay;
		this.mGuestCompAlias = mGuestCompAlias;
		this.needLocalPay = needLocalPay;
		this.secondSiteSwitch = secondSiteSwitch;
		this.payTipText = payTipText;
	}

	public HospitalSettings(String examEndTime, String examStartTime, int hospitalId, String serviceTel,
			String technicalTel) {
		// TODO Auto-generated constructor stub
		this.examEndTime = examEndTime;
		this.examStartTime = examStartTime;
		this.hospitalId = hospitalId;
		this.serviceTel = serviceTel;
		this.technicalTel = technicalTel;
	}

	public HospitalSettings() {
		// TODO Auto-generated constructor stub
	}

	public HospitalSettings(boolean autoConfirmOrder, boolean autoExportOrder, String cooperateCompany,
			Integer cooperateType, String guestOfflineCompAlias, String guestOnlineCompAlias, int hospitalId,
			String mGuestCompAlias, boolean refundRefusedItem) {
		// TODO Auto-generated constructor stub
		this.autoConfirmOrder = autoConfirmOrder;
		this.autoExportOrder = autoExportOrder;
		this.cooperateCompany = cooperateCompany;
		this.cooperateType = cooperateType;
		this.guestOfflineCompAlias = guestOfflineCompAlias;
		this.guestOnlineCompAlias = guestOnlineCompAlias;
		this.hospitalId = hospitalId;
		this.mGuestCompAlias = mGuestCompAlias;
		this.refundRefusedItem = refundRefusedItem;
	}
}
