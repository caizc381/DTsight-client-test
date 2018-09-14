package com.tijiantest.model.order;

import com.tijiantest.util.pagination.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 订单查询参数
 *
 * @create 2016年7月29日 上午11:41:16
 * @author tangyi
 * @version
 */
public class OrderQueryParams implements Serializable{
	private static final long serialVersionUID = -4990105936844524558L;

	//排序字段
	private String sortField;
	//排序方向：降序值：Sort.Direction.DESC.name()   升序值：Sort.Direction.ASC.name()
	private String sortDirection;
	//分页信息
	private Page page;
	//模糊查询字段集合【此类中的哪个查询字段值不为空且想进行模糊查询（只针对字符串String类型的）的可以放入此结合中，比如想对accountName进行模糊查询可以把accountName放入fuzzyQueryList】
	private List<String>  fuzzyQueryList = new ArrayList<>();


	//订单IDS
	private List<Integer> orderIds;
	//订单编号
	private List<String> orderNums;
	//用户IDS
	private List<Integer> accountIds;
	//预约人IDS
	private List<Integer> ownerIds;
	//客户经理ID
	private List<Integer> managerIds;
	//体检中心id列表
	private List<Integer> hospitalIds;
	//单位ID
	private List<Integer> examCompanyIds;
	//渠道单位ID
	private List<Integer> channelCompanyIds;
	//订单状态多条件查询，注意不能区分已导出/已预约
	private List<Integer> orderStatuses;
	//订单导出状态
	private List<Integer> exportStatuses;
	// 结算标记状态ID
	private List<Integer> settleSigns;
	//机构ID
	private List<Integer> fromSites;
	//机构类型
	private List<Integer> fromSiteOrgTypes;
	//体检人的账户类型（tb_account.type 账户类型 1:mytijian注册生成 2:crm生成 3:体检人 4:演示账户 5客户经理和体检中心操作员）
	private List<Integer> accountTypes;
	//批次ID
	private List<Integer> batchIds;

	//体检开始时间
	private Date          examStartDate;
	//体检结束时间
	private Date          examEndDate;
	//下单开始时间
	private Date          insertStartDate;
	//下单结束时间
	private Date          insertEndDate;
	//体检中心ID
	private Integer       hospitalId;
	//订单状态
	private Integer       orderStatus;
	//是否已导出
	private Boolean       isExport;
	//现场应付是否为0
	private Boolean       isOfflinePayMoneyZero;
	//现场支付未付款是否为0
	private Boolean       isOfflineUnpayMoneyZero;
	//字符是否为0
	private Boolean       isSelfMoneyZero;
	//是否是管理后台
	private boolean       isManage;
	//仅显示可导
	private Boolean       showExportable;
	//是否是立即导出
	private Boolean       exportImmediately;
	//结算批次
	private String		  settleBatch;
	//医院结算标记
	private Integer		  settleSign;
	//渠道结算标记
	private List<Integer>  channelSettlementStatus;


	//是否显示即时导入订单（3000年的订单）
	private Boolean 	  showImmediatelyImpOrder;
	//hisbm
	private String        hisbm;
	//客户经理
	private Integer 	  managerId;
	//from-site
	private Integer       fromSite;
	//机构类型：1：医院，2：渠道
	private Integer       fromSiteOrgType;
	//体检人姓名
	private String        accountName;
	//体检人姓名拼音
	private String        accountSpellName;
	//体检人电话号码
	private String        mobile;
	//体检人性别
	private Integer       gender;
	//体检人身份证号码
	private String        idCard;
	//查询关键字：姓名首字母 or 身份证 or 姓名
	private String        keyWord;
	//挂账单位名称
	private String        accountCompanyName;
	//客户经理名称
	private String        managerName;
	//体检单位名称
	private String        examCompanyName;
	//操作人名称
	private String        operatorName;
	//发票管理查询标示（杭疗）【uncommitted:只显示可开票订单,committed：已提交的开票申请】
	private String        invoiceManagerQueryFlag;


	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public List<Integer> getHospitalIds() {
		return hospitalIds;
	}

	public void setHospitalIds(List<Integer> hospitalIds) {
		this.hospitalIds = hospitalIds;
	}

	public Date getExamStartDate() {
		return examStartDate;
	}

	public void setExamStartDate(Date examStartDate) {
		this.examStartDate = examStartDate;
	}

	public Date getExamEndDate() {
		return examEndDate;
	}

	public void setExamEndDate(Date examEndDate) {
		this.examEndDate = examEndDate;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public List<Integer> getExamCompanyIds() {
		return examCompanyIds;
	}

	public void setExamCompanyIds(List<Integer> examCompanyIds) {
		this.examCompanyIds = examCompanyIds;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public List<Integer> getOrderStatuses() {
		return orderStatuses;
	}

	public void setOrderStatuses(List<Integer> orderStatuses) {
		this.orderStatuses = orderStatuses;
	}

	public List<Integer> getExportStatuses() {
		return exportStatuses;
	}

	public void setExportStatuses(List<Integer> exportStatuses) {
		this.exportStatuses = exportStatuses;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}


	public List<Integer> getAccountIds() {
		return accountIds;
	}

	public void setAccountIds(List<Integer> accountIds) {
		this.accountIds = accountIds;
	}

	public List<Integer> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Integer> orderIds) {
		this.orderIds = orderIds;
	}

	public List<Integer> getSettleSigns() {
		return settleSigns;
	}

	public void setSettleSigns(List<Integer> settleSigns) {
		this.settleSigns = settleSigns;
	}

	public List<Integer> getManagerIds() {
		return managerIds;
	}

	public void setManagerIds(List<Integer> managerIds) {
		this.managerIds = managerIds;
	}

	public List<Integer> getFromSites() {
		return fromSites;
	}

	public void setFromSites(List<Integer> fromSites) {
		this.fromSites = fromSites;
	}

	public List<Integer> getFromSiteOrgTypes() {
		return fromSiteOrgTypes;
	}

	public void setFromSiteOrgTypes(List<Integer> fromSiteOrgTypes) {
		this.fromSiteOrgTypes = fromSiteOrgTypes;
	}

	public List<Integer> getOwnerIds() {
		return ownerIds;
	}

	public void setOwnerIds(List<Integer> ownerIds) {
		this.ownerIds = ownerIds;
	}

	public Boolean getIsExport() {
		return isExport;
	}

	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	public Date getInsertStartDate() {
		return insertStartDate;
	}

	public Integer getFromSite() {
		return fromSite;
	}

	public void setFromSite(Integer fromSite) {
		this.fromSite = fromSite;
	}

	public Integer getFromSiteOrgType() {
		return fromSiteOrgType;
	}

	public void setFromSiteOrgType(Integer fromSiteOrgType) {
		this.fromSiteOrgType = fromSiteOrgType;
	}

	public void setInsertStartDate(Date insertStartDate) {
		this.insertStartDate = insertStartDate;
	}

	public Date getInsertEndDate() {
		return insertEndDate;
	}

	public void setInsertEndDate(Date insertEndDate) {
		this.insertEndDate = insertEndDate;
	}

	public Boolean getIsOfflinePayMoneyZero() {
		return isOfflinePayMoneyZero;
	}

	public void setIsOfflinePayMoneyZero(Boolean isOfflinePayMoneyZero) {
		this.isOfflinePayMoneyZero = isOfflinePayMoneyZero;
	}

	public List<Integer> getAccountTypes() {
		return accountTypes;
	}

	public void setAccountTypes(List<Integer> accountTypes) {
		this.accountTypes = accountTypes;
	}

	public List<Integer> getBatchIds() {
		return batchIds;
	}

	public void setBatchIds(List<Integer> batchIds) {
		this.batchIds = batchIds;
	}

	public Boolean getExportImmediately() {
		return exportImmediately;
	}

	public void setExportImmediately(Boolean exportImmediately) {
		this.exportImmediately = exportImmediately;
	}

	public String getAccountCompanyName() {
		return accountCompanyName;
	}

	public void setAccountCompanyName(String accountCompanyName) {
		this.accountCompanyName = accountCompanyName;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getExamCompanyName() {
		return examCompanyName;
	}

	public void setExamCompanyName(String examCompanyName) {
		this.examCompanyName = examCompanyName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public boolean isManage() {
		return isManage;
	}

	public void setManage(boolean isManage) {
		this.isManage = isManage;
	}

	public Boolean getShowExportable() {
		return showExportable;
	}

	public void setShowExportable(Boolean showExportable) {
		this.showExportable = showExportable;
	}

	public Boolean getIsSelfMoneyZero() {
		return isSelfMoneyZero;
	}

	public void setIsSelfMoneyZero(Boolean isSelfMoneyZero) {
		this.isSelfMoneyZero = isSelfMoneyZero;
	}

	public String getSettleBatch() {
		return settleBatch;
	}

	public void setSettleBatch(String settleBatch) {
		this.settleBatch = settleBatch;
	}

	public Integer getSettleSign() {
		return settleSign;
	}

	public void setSettleSign(Integer settleSign) {
		this.settleSign = settleSign;
	}

	public Boolean getShowImmediatelyImpOrder() {
		return showImmediatelyImpOrder;
	}

	public void setShowImmediatelyImpOrder(Boolean showImmediatelyImpOrder) {
		this.showImmediatelyImpOrder = showImmediatelyImpOrder;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Boolean getIsOfflineUnpayMoneyZero() {
		return isOfflineUnpayMoneyZero;
	}

	public void setIsOfflineUnpayMoneyZero(Boolean isOfflineUnpayMoneyZero) {
		this.isOfflineUnpayMoneyZero = isOfflineUnpayMoneyZero;
	}

	public List<Integer> getChannelCompanyIds() {
		return channelCompanyIds;
	}

	public void setChannelCompanyIds(List<Integer> channelCompanyIds) {
		this.channelCompanyIds = channelCompanyIds;
	}

	public List<String> getOrderNums() {
		return orderNums;
	}

	public void setOrderNums(List<String> orderNums) {
		this.orderNums = orderNums;
	}

	public List<String> getFuzzyQueryList() {
		return fuzzyQueryList;
	}

	public void setFuzzyQueryList(List<String> fuzzyQueryList) {
		this.fuzzyQueryList = fuzzyQueryList;
	}

	public String getAccountSpellName() {
		return accountSpellName;
	}

	public void setAccountSpellName(String accountSpellName) {
		this.accountSpellName = accountSpellName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHisbm() {
		return hisbm;
	}

	public void setHisbm(String hisbm) {
		this.hisbm = hisbm;
	}

	public String getInvoiceManagerQueryFlag() {
		return invoiceManagerQueryFlag;
	}

	public void setInvoiceManagerQueryFlag(String invoiceManagerQueryFlag) {
		this.invoiceManagerQueryFlag = invoiceManagerQueryFlag;
	}

	public List<Integer> getChannelSettlementStatus() {
		return channelSettlementStatus;
	}

	public void setChannelSettlementStatus(List<Integer> channelSettlementStatus) {
		this.channelSettlementStatus = channelSettlementStatus;
	}
}
