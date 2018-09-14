package com.tijiantest.model.order;


import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.order.snapshot.ExamItemSnapshot;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MongoOrder extends Order implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 6521802999617346998L;


	private ObjectId objectId;

	/**
	 * 客户经理账户关系
	 */
	private AccountRelationInCrm accountRelation;
	
	private String birthDate;
	private Integer accountType;
	private String genderLabel;
	private String groupName;
	private String retireLabel;
	private String marriageStatusLabel;
	private String exportExamDate;
	private String isFamily;
	private String payType;
	private String mealName;
	private Integer adjustPrice;
	private Integer exportAdjustPrice;
	private String vip;
	private String offlinePayMoney;
	private Integer offlineUnpayMoney;
	private String selfMoney;
	private Double exportDiscount;
	private String exportSelfMoney;
	private String exportOrderPrice;
	private String hisItemIds;
	private String manager;
	private String examCompany;
	private String accountManager;
	private InvoiceApplySnapshot invoiceApply;
	private String exportAccountName;
	private Integer exportAccountId;
	private Date exportTime;
	private Integer settleSign;
	private String settleBatch;
	private String guideInfo;
	private String operator;
	private String operatorMobile;
	private Boolean exportImmediately;
	private String exportFailedMsg;
	private Boolean isFastBook;
	private Boolean isProxyCard;
	private String hisbm;
	/**
	 * 回单分类的项目（包括：加项，拒绝，未检项目，正常已体检的不在其中）和 订单详情表的refundItemsClassify保持一致
	 */
	private List<ExamItemSnapshot> refundItemsClassify;
	/**
	 * 订单导出状态，与isExport字段不同（mysql里没有此字段,对接需要）
	 */
	private Integer exportState;
	/**
	 * 订单导出信息（mysql里没有此字段,对接需要）
	 */
	private String exportMsg;
	/**
	 * his订单金额（mysql里没有此字段,对接需要）
	 */
	private Double hisOrderPrice;


	//以下字段是视图扩展字段（在页面展示使用），不在mongoOrder中存储  -------start
	private Boolean hasSettlementOpen;
	//以上字段是视图扩展字段（在页面展示使用），不在mongoOrder中存储  -------end

	public Boolean getProxyCard() {
		return isProxyCard;
	}

	public void setProxyCard(Boolean proxyCard) {
		isProxyCard = proxyCard;
	}

	public Boolean getFastBook() {
		return isFastBook;
	}

	public void setFastBook(Boolean fastBook) {
		isFastBook = fastBook;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getExportFailedMsg() {
		return exportFailedMsg;
	}

	public void setExportFailedMsg(String exportFailedMsg) {
		this.exportFailedMsg = exportFailedMsg;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	public String getOfflinePayMoney() {
		return offlinePayMoney;
	}

	public void setOfflinePayMoney(String offlinePayMoney) {
		this.offlinePayMoney = offlinePayMoney;
	}

	public String getExportOrderPrice() {
		return exportOrderPrice;
	}

	public void setExportOrderPrice(String exportOrderPrice) {
		this.exportOrderPrice = exportOrderPrice;
	}

	public AccountRelationInCrm getAccountRelation() {
		return accountRelation;
	}
	public void setAccountRelation(AccountRelationInCrm accountRelation) {
		this.accountRelation = accountRelation;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public Integer getAccountType() {
		return accountType;
	}
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
	public String getGenderLabel() {
		return genderLabel;
	}
	public void setGenderLabel(String genderLabel) {
		this.genderLabel = genderLabel;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getRetireLabel() {
		return retireLabel;
	}
	public void setRetireLabel(String retireLabel) {
		this.retireLabel = retireLabel;
	}
	public String getMarriageStatusLabel() {
		return marriageStatusLabel;
	}
	public void setMarriageStatusLabel(String marriageStatusLabel) {
		this.marriageStatusLabel = marriageStatusLabel;
	}
	public String getExportExamDate() {
		return exportExamDate;
	}
	public void setExportExamDate(String exportExamDate) {
		this.exportExamDate = exportExamDate;
	}
	public String getIsFamily() {
		return isFamily;
	}
	public void setIsFamily(String isFamily) {
		this.isFamily = isFamily;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getMealName() {
		return mealName;
	}
	public void setMealName(String mealName) {
		this.mealName = mealName;
	}
	public Integer getAdjustPrice() {
		return adjustPrice;
	}
	public void setAdjustPrice(Integer adjustPrice) {
		this.adjustPrice = adjustPrice;
	}
	public Integer getExportAdjustPrice() {
		return exportAdjustPrice;
	}
	public void setExportAdjustPrice(Integer exportAdjustPrice) {
		this.exportAdjustPrice = exportAdjustPrice;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public Integer getOfflineUnpayMoney() {
		return offlineUnpayMoney;
	}
	public void setOfflineUnpayMoney(Integer offlineUnpayMoney) {
		this.offlineUnpayMoney = offlineUnpayMoney;
	}
	public String getSelfMoney() {
		return selfMoney;
	}
	public void setSelfMoney(String selfMoney) {
		this.selfMoney = selfMoney;
	}
	public Double getExportDiscount() {
		return exportDiscount;
	}
	public void setExportDiscount(Double exportDiscount) {
		this.exportDiscount = exportDiscount;
	}
	public String getExportSelfMoney() {
		return exportSelfMoney;
	}
	public void setExportSelfMoney(String exportSelfMoney) {
		this.exportSelfMoney = exportSelfMoney;
	}
	public String getHisItemIds() {
		return hisItemIds;
	}
	public void setHisItemIds(String hisItemIds) {
		this.hisItemIds = hisItemIds;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getExamCompany() {
		return examCompany;
	}
	public void setExamCompany(String examCompany) {
		this.examCompany = examCompany;
	}
	public String getAccountManager() {
		return accountManager;
	}
	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}
	public InvoiceApplySnapshot getInvoiceApply() {
		return invoiceApply;
	}
	public void setInvoiceApply(InvoiceApplySnapshot invoiceApply) {
		this.invoiceApply = invoiceApply;
	}
	public String getExportAccountName() {
		return exportAccountName;
	}
	public void setExportAccountName(String exportAccountName) {
		this.exportAccountName = exportAccountName;
	}
	public Integer getExportAccountId() {
		return exportAccountId;
	}
	public void setExportAccountId(Integer exportAccountId) {
		this.exportAccountId = exportAccountId;
	}
	public Date getExportTime() {
		return exportTime;
	}
	public void setExportTime(Date exportTime) {
		this.exportTime = exportTime;
	}
	public Integer getSettleSign() {
		return settleSign;
	}
	public void setSettleSign(Integer settleSign) {
		this.settleSign = settleSign;
	}
	public String getSettleBatch() {
		return settleBatch;
	}
	public void setSettleBatch(String settleBatch) {
		this.settleBatch = settleBatch;
	}
	public String getGuideInfo() {
		return guideInfo;
	}
	public void setGuideInfo(String guideInfo) {
		this.guideInfo = guideInfo;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperatorMobile() {
		return operatorMobile;
	}
	public void setOperatorMobile(String operatorMobile) {
		this.operatorMobile = operatorMobile;
	}
	public Boolean getExportImmediately() {
		return exportImmediately;
	}
	public void setExportImmediately(Boolean exportImmediately) {
		this.exportImmediately = exportImmediately;
	}

	public Integer getExportState() {
		return exportState;
	}

	public void setExportState(Integer exportState) {
		this.exportState = exportState;
	}

	public String getExportMsg() {
		return exportMsg;
	}

	public void setExportMsg(String exportMsg) {
		this.exportMsg = exportMsg;
	}

	public Double getHisOrderPrice() {
		return hisOrderPrice;
	}

	public void setHisOrderPrice(Double hisOrderPrice) {
		this.hisOrderPrice = hisOrderPrice;
	}

	public List<ExamItemSnapshot> getRefundItemsClassify() {
		return refundItemsClassify;
	}

	public void setRefundItemsClassify(List<ExamItemSnapshot> refundItemsClassify) {
		this.refundItemsClassify = refundItemsClassify;
	}

	public String getHisbm() {
		return hisbm;
	}

	public void setHisbm(String hisbm) {
		this.hisbm = hisbm;
	}

	public Boolean getHasSettlementOpen() {
		return hasSettlementOpen;
	}

	public void setHasSettlementOpen(Boolean hasSettlementOpen) {
		this.hasSettlementOpen = hasSettlementOpen;
	}
}
