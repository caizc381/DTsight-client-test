package com.tijiantest.model.order;

/***********************************************************************
 * Module:  Order.java
 * Author:  Administrator
 * Purpose: Defines the Class Order
 ***********************************************************************/

import java.io.Serializable;
import java.util.Date;

import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.AccountManagerRelSnapshot;
import com.tijiantest.model.account.AccountRelationInCrm;
import com.tijiantest.model.account.AccountSnapshot;
import com.tijiantest.model.account.ManagerSnapshot;
import com.tijiantest.model.card.CardSnapshot;
import com.tijiantest.model.hospital.Hospital;
import com.tijiantest.model.order.snapshot.ExamDateSnapshot;
import com.tijiantest.model.order.snapshot.OrderMealSnapshot;

public class Order implements Serializable {

	private static final long serialVersionUID = 5561015732395521497L;

	private int id;
	
	/**
	 * 订单批次标识
	 */
	private int batchId;

	private String orderNum;

	/**
	 * 获取的对象中只有id, name
	 */
	private Account account;

	/**
	 * 订单状态 0：未付款 1：已支付 2：已预约 3：体检完成 4：未到检 5：已撤销 6：已删除 7：支付中 8:已关闭
	 */
	private int status = 0;

	private Integer orderPrice;

	/**
	 * 订单差额，正常订单此字段为空，用于以下订单，如：使用了隐藏金额的卡
	 * 金额 = 订单价格 － 隐藏卡支付价格
	 */
	private Integer differencePrice;
	
	private Date examDate;

	/**
	 * 预约时间段段
	 */
	private Integer examTimeIntervalId;
	/**
	 * 预约时间段名称
	 */
	private String examTimeIntervalName;
	/**
	 * 获取的对象中只有id, name
	 */
	private Hospital hospital;

	private Double discount;

	/**
	 * false:未导出 true、已导出
	 */
	private Boolean isExport;

	/**
	 * 订单来源 1：mytijian 2:mobile 3:crm
	 */
	private int source;

	private Date insertTime;

	private Date updateTime;

	private Integer entryCardId; // 入口卡
	
	/**
	 * 体检单位id
	 */
	private Integer examCompanyId;


	private Integer accountCompanyId;

	/**
	 * 导出体检项详情快照，对应java类List<ExamItemSnapshot>
	 */
	private String itemsDetail;
	/**
	 * 套餐详情快照，对应java类MealSnapshot
	 */
	private String mealDetail;
	/**
	 * 加项包快照，对应java类ExamItemPackageSnapshot
	 */
	private String packageSnapshotDetail;

	private String remark;
	
	private AccountRelationInCrm accountInCrm;
	/**
	 * 下单人
	 */
	private Integer operatorId;
	/**
	 * 挂账客户经理id
	 */
	private Integer managerId;
	/**
	 * 所属客户经理id
	 */
	private Integer ownerId;
	/**
	 * 订单来源机构id
	 */
	private Integer fromSite;
	
	/**
	 * 平台调整价格
	 */
	private Integer platformAdjustPrice;
	/**
	 * 是否需要字纸报告
	 */
	public Boolean needPaperReport;
	/**
	 * 客户经理账户关系
	 */
	private AccountManagerRelSnapshot accountManagerRel;
	/**
	 * 体检卡快照
	 */
	private CardSnapshot card;
	/**
	 * 订单账户快照
	 */
	private AccountSnapshot orderAccount;
	/**
	 * 客户经理快照
	 */
	private ManagerSnapshot orderManager;              
	/**
	 * 住宿快照
	 */
	private Recidence recidence;
	/**
	 * 订单套餐快照
	 */
	private OrderMealSnapshot orderMealSnapshot;
	/**
	 * 订单体检日期快照
	 */
	private ExamDateSnapshot orderExamDate;
	/**
	 * 订单邮件记录
	 */
	private MaillingRecord maillingRecord;
	/**
	 * 挂账单位快照
	 */
	private AccountCompanySnapshot accountCompany;
	/**
	 * 订单体体检中心快照
	 */
	private HospitalExamCompanySnapshot hospitalCompany;
	/**
	 * 订单体检中心结算快照
	 */
	private HospitalSettleInfoSnapshot hospitalSettleInfo;

	/**
	 * 订单额外信息扩展
	 */
	private OrderExtInfoSnapshot orderExtInfo;
	/**
	 * 体检中心快照
	 */
	@Deprecated // TODO 暂时不使用，快照实现方式需要重新设计
	private HospitalSnapshot orderHospital;
	/**
	 * 订单渠道商快照
	 */
	private ChannelSnapshot channel;
	/**
	 * 订单渠道商结算快照
	 */
	private ChannelSettleInfoSnapshot channelSettleInfo; 
	/**
	 * 订单渠道商客户经理快照
	 */
	private ManagerSnapshot channelManager;
	/**
	 * 订单渠道商单位快照
	 */
	private ChannelExamCompanySnapshot channelCompany;
	/**
	 * 订单是否允许改期
	 */
	private Boolean isChangeDate;
	/**
	 * 隐价订单
	 */
	private Boolean isHidePrice;
	/**
	 * 订单现场付款
	 */
	private Boolean isLocalePay;
	/**
	 * 是允许减项
	 */
	private Boolean isReduceItem;
	/**
	 * 扩展属性
	 */
	private String extAttr;
	/**
	 * 扩展属性类型
	 * @see com.mytijian.order.enums.ExtAttrTypEnum
	 */
	private Integer extAttrType;
	/**
	 * 订单原价，打折前价格
	 */
	private Integer orderOriginalPrice;
	/**
	 * 下单场景
	 * @see com.mytijian.order.enums.OrderSceneEnum
	 */
	private Integer orderScene;
	/**
	 * 导出订单
	 */
	private OrderExportExtInfoSnapshot orderExportExtInfo;
	/**
	 * 导引单信息
	 */
	private OrderGuideInfoSnapshot orderGuideInfo;
	
    /**
     * '机构类型 1：体检中心 2:健康管理中心'
     */
    private Integer fromSiteOrgType;

	private Integer oldExamCompanyId;
	
	private String hisItemIds;
	
	private String xlsItemIds; //xls退单管理中使用的itemIds
	
	public OrderExtInfoSnapshot getOrderExtInfo() {
		return orderExtInfo;
	}

	public void setOrderExtInfo(OrderExtInfoSnapshot orderExtInfo) {
		this.orderExtInfo = orderExtInfo;
	}

	public Boolean getNeedPaperReport() {
		return needPaperReport;
	}

	public void setNeedPaperReport(Boolean needPaperReport) {
		this.needPaperReport = needPaperReport;
	}
	/**
	 * 客户经理
	 */
	public Integer getOwnerId() {
		return ownerId;
	}
	/**
	 * 客户经理
	 */
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getAccountCompanyId() {
		return accountCompanyId;
	}

	public void setAccountCompanyId(Integer accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}

	public Integer getExamCompanyId() {
		return examCompanyId;
	}

	public void setExamCompanyId(Integer examCompanyId) {
		this.examCompanyId = examCompanyId;
	}

	public Integer getExamTimeIntervalId() {
		return examTimeIntervalId;
	}

	public void setExamTimeIntervalId(Integer examTimeIntervalId) {
		this.examTimeIntervalId = examTimeIntervalId;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 请使用AccountSnapshot
	 */
	@Deprecated
	public Account getAccount() {
		return account;
	}

	/**
	 * 请使用AccountSnapshot
	 */
	@Deprecated
	public void setAccount(Account account) {
		this.account = account;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Boolean getIsExport() {
		return isExport;
	}

	public void setIsExport(Boolean isExport) {
		this.isExport = isExport;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getItemsDetail() {
		return itemsDetail;
	}

	/**
	 * 已经废弃取,可以通过setOrderMealSnapshot(OrderMealSnapshot orderMealSnapshot)方法设置
	 */
	public void setItemsDetail(String itemsDetail) {
		this.itemsDetail = itemsDetail;
	}

	/**
	 * 使用mongoMeal查询装载
	 */
	public String getMealDetail() {
		return mealDetail;
	}

	/**
	 * 使用mongoMeal查询装载
	 */
	public void setMealDetail(String mealDetail) {
		this.mealDetail = mealDetail;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getEntryCardId() {
		return entryCardId;
	}

	public void setEntryCardId(Integer entryCardId) {
		this.entryCardId = entryCardId;
	}

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	/**
	 * 请参考accountManagerRel属性
	 */
	@Deprecated
	public AccountRelationInCrm getAccountInCrm() {
		return accountInCrm;
	}

	/**
	 * 请参考accountManagerRel属性
	 */
	@Deprecated
	public void setAccountInCrm(AccountRelationInCrm accountInCrm) {
		this.accountInCrm = accountInCrm;
	}
	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	/**
	 * 挂账客户经理
	 */
	public Integer getManagerId() {
		return managerId;
	}

	/**
	 * 挂账客户经理
	 */
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	/**
	 * 已经废弃请参考orderExamDate属性
	 */
	@Deprecated
	public String getExamTimeIntervalName() {
		return examTimeIntervalName;
	}

	/**
	 * 已经废弃请参考orderExamDate属性
	 */
	@Deprecated
	public void setExamTimeIntervalName(String examTimeIntervalName) {
		this.examTimeIntervalName = examTimeIntervalName;
	}

	public Integer getDifferencePrice() {
		return differencePrice;
	}

	public void setDifferencePrice(Integer differencePrice) {
		this.differencePrice = differencePrice;
	}

	public Integer getFromSite() {
		return fromSite;
	}

	public void setFromSite(Integer fromSite) {
		this.fromSite = fromSite;
	}

	public Integer getPlatformAdjustPrice() {
		return platformAdjustPrice;
	}

	public void setPlatformAdjustPrice(Integer platformAdjustPrice) {
		this.platformAdjustPrice = platformAdjustPrice;
	}

	/**
	 * 使用mongoMeal查询装载
	 */
    public String getPackageSnapshotDetail() {
        return packageSnapshotDetail;
    }

    public void setPackageSnapshotDetail(String packageSnapshotDetail) {
        this.packageSnapshotDetail = packageSnapshotDetail;
    }

	public CardSnapshot getCard() {
		return card;
	}

	public void setCard(CardSnapshot card) {
		this.card = card;
	}

	public AccountSnapshot getOrderAccount() {
		return orderAccount;
	}

	public void setOrderAccount(AccountSnapshot orderAccount) {
		this.orderAccount = orderAccount;
	}

	public ManagerSnapshot getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(ManagerSnapshot orderManager) {
		this.orderManager = orderManager;
	}

	public Recidence getRecidence() {
		return recidence;
	}

	public void setRecidence(Recidence recidence) {
		this.recidence = recidence;
	}

	public OrderMealSnapshot getOrderMealSnapshot() {
		return orderMealSnapshot;
	}

	public void setOrderMealSnapshot(OrderMealSnapshot orderMealSnapshot) {
		this.orderMealSnapshot = orderMealSnapshot;
	}

	public ExamDateSnapshot getOrderExamDate() {
		return orderExamDate;
	}

	public void setOrderExamDate(ExamDateSnapshot orderExamDate) {
		this.orderExamDate = orderExamDate;
	}

	public MaillingRecord getMaillingRecord() {
		return maillingRecord;
	}

	public void setMaillingRecord(MaillingRecord maillingRecord) {
		this.maillingRecord = maillingRecord;
	}

	public AccountCompanySnapshot getAccountCompany() {
		return accountCompany;
	}

	public void setAccountCompany(AccountCompanySnapshot accountCompany) {
		this.accountCompany = accountCompany;
	}

	public HospitalExamCompanySnapshot getHospitalCompany() {
		return hospitalCompany;
	}

	public void setHospitalCompany(HospitalExamCompanySnapshot hospitalCompany) {
		this.hospitalCompany = hospitalCompany;
	}

	public HospitalSettleInfoSnapshot getHospitalSettleInfo() {
		return hospitalSettleInfo;
	}

	public void setHospitalSettleInfo(HospitalSettleInfoSnapshot hospitalSettleInfo) {
		this.hospitalSettleInfo = hospitalSettleInfo;
	}

	public AccountManagerRelSnapshot getAccountManagerRel() {
		return accountManagerRel;
	}

	public void setAccountManagerRel(AccountManagerRelSnapshot accountManagerRel) {
		this.accountManagerRel = accountManagerRel;
	}

	/**
	 * 快照实现方式需要重新考量，暂时还是使用hospital属性
	 * @return
	 */
	@Deprecated
	public HospitalSnapshot getOrderHospital() {
		return orderHospital;
	}

	/**
	 * 快照实现方式需要重新考量，暂时还是使用hospital属性
	 * @return
	 */
	@Deprecated
	public void setOrderHospital(HospitalSnapshot orderHospital) {
		this.orderHospital = orderHospital;
	}

	public ChannelSnapshot getChannel() {
		return channel;
	}

	public void setChannel(ChannelSnapshot channel) {
		this.channel = channel;
	}

	public ChannelSettleInfoSnapshot getChannelSettleInfo() {
		return channelSettleInfo;
	}

	public void setChannelSettleInfo(ChannelSettleInfoSnapshot channelSettleInfo) {
		this.channelSettleInfo = channelSettleInfo;
	}

	public ManagerSnapshot getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(ManagerSnapshot channelManager) {
		this.channelManager = channelManager;
	}

	public ChannelExamCompanySnapshot getChannelCompany() {
		return channelCompany;
	}

	public void setChannelCompany(ChannelExamCompanySnapshot channelCompany) {
		this.channelCompany = channelCompany;
	}
	
	public Boolean getIsChangeDate() {
		return isChangeDate;
	}

	public void setIsChangeDate(Boolean isChangeDate) {
		this.isChangeDate = isChangeDate;
	}

	public Boolean getIsHidePrice() {
		return isHidePrice;
	}

	public void setIsHidePrice(Boolean isHidePrice) {
		this.isHidePrice = isHidePrice;
	}

	public Boolean getIsLocalePay() {
		return isLocalePay;
	}

	public void setIsLocalePay(Boolean isLocalePay) {
		this.isLocalePay = isLocalePay;
	}

	public Boolean getIsReduceItem() {
		return isReduceItem;
	}

	public void setIsReduceItem(Boolean isReduceItem) {
		this.isReduceItem = isReduceItem;
	}

	public String getExtAttr() {
		return extAttr;
	}

	public void setExtAttr(String extAttr) {
		this.extAttr = extAttr;
	}

	public Integer getExtAttrType() {
		return extAttrType;
	}

	public void setExtAttrType(Integer extAttrType) {
		this.extAttrType = extAttrType;
	}

	public Integer getOrderOriginalPrice() {
		return orderOriginalPrice;
	}

	public void setOrderOriginalPrice(Integer orderOriginalPrice) {
		this.orderOriginalPrice = orderOriginalPrice;
	}

	public Integer getOrderScene() {
		return orderScene;
	}

	public void setOrderScene(Integer orderScene) {
		this.orderScene = orderScene;
	}

	public OrderExportExtInfoSnapshot getOrderExportExtInfo() {
		return orderExportExtInfo;
	}

	public void setOrderExportExtInfo(OrderExportExtInfoSnapshot orderExportExtInfo) {
		this.orderExportExtInfo = orderExportExtInfo;
	}

	public OrderGuideInfoSnapshot getOrderGuideInfo() {
		return orderGuideInfo;
	}

	public void setOrderGuideInfo(OrderGuideInfoSnapshot orderGuideInfo) {
		this.orderGuideInfo = orderGuideInfo;
	}

	public Integer getFromSiteOrgType() {
		return fromSiteOrgType;
	}

	public void setFromSiteOrgType(Integer fromSiteOrgType) {
		this.fromSiteOrgType = fromSiteOrgType;
	}

	public Integer getOldExamCompanyId() {
		return oldExamCompanyId;
	}

	public void setOldExamCompanyId(Integer oldExamCompanyId) {
		this.oldExamCompanyId = oldExamCompanyId;
	}

	public String getHisItemIds() {
		return hisItemIds;
	}

	public void setHisItemIds(String hisItemIds) {
		this.hisItemIds = hisItemIds;
	}

	public String getXlsItemIds() {
		return xlsItemIds;
	}

	public void setXlsItemIds(String xlsItemIds) {
		this.xlsItemIds = xlsItemIds;
	}
	
}