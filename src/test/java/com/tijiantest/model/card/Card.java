package com.tijiantest.model.card;

import java.io.Serializable;
import java.util.Date;
import com.tijiantest.util.DateUtils;

/**
 * @author admin
 *
 */
public class Card implements Serializable {

	private static final long serialVersionUID = 7492708813946520443L;

	private Integer id;

	/**
	 * 体检须知id
	 */
	private Integer examNoteId;

	/**
	 * 卡号
	 */
	private String cardNum;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 面值
	 */
	private Long capacity;

	/**
	 * 余额
	 */
	private Long balance;

	/**
	 * 类型 1：虚拟卡，2：实体卡
	 */
	private Integer type;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 卡的来源 体检中心标志 默认为空
	 */
	private Integer fromHospital;

	/**
	 * 充值时间
	 */
	private Date rechargeTime;

	/**
	 * 可用时间
	 */
	private Date availableDate;

	/**
	 * 有效期
	 */
	private Date expiredDate;

	/**
	 * 创建日期
	 */
	private Date createDate;

	/**
	 * 账户id
	 */
	private Integer accountId;

	/**
	 * 卡名
	 */
	private String cardName;

	/**
	 * 客户经理id
	 */
	private Integer managerId;

	/**
	 * 单位id
	 */
	private Integer companyId;
	
	/**
	 * 新体检单位id
	 */
	private Integer newCompanyId;
	
	/**
	 * 机构类型
	 */
	private Integer organizationType;

	/**
	 * 是否是主卡
	 */
	private boolean isPrimary = false;

	/**
	 * 父卡标识
	 */
	private Integer parentCardId;

	/**
	 * 卡设置
	 */
	private CardSetting cardSetting;

	/**
	 * 批次id
	 */
	private Integer batchId;
	
	/**
	 * 可回收余额
	 */
	private Long recoverableBalance;
	/**
	 * 是否删除(0:未删除,1:已删除)
	 */
	private Integer isDeleted;
	
	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getTradeAccountId() {
		return tradeAccountId;
	}

	public void setTradeAccountId(Integer tradeAccountId) {
		this.tradeAccountId = tradeAccountId;
	}

	public Long getFreezeBalance() {
		return freezeBalance;
	}

	public void setFreezeBalance(Long freezeBalance) {
		this.freezeBalance = freezeBalance;
	}

	public Integer getHospitalSettlementStatus() {
		return hospitalSettlementStatus;
	}

	public void setHospitalSettlementStatus(Integer hospitalSettlementStatus) {
		this.hospitalSettlementStatus = hospitalSettlementStatus;
	}

	public String getSettlementBatchSn() {
		return settlementBatchSn;
	}

	public void setSettlementBatchSn(String settlementBatchSn) {
		this.settlementBatchSn = settlementBatchSn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * 总账户ID
	 */
	private Integer tradeAccountId;
	/**
	 * 冻结金额
	 */
	private Long freezeBalance;
	/**
	 * 医院结算批次审核标志位
	 */
	private Integer hospitalSettlementStatus;
	/**
	 * 该体检卡对应的结算批次号
	 */
	private String settlementBatchSn;
	

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = DateUtils.toDayLastSecod(expiredDate);
	}

	public Integer getFromHospital() {
		return fromHospital;
	}

	public void setFromHospital(Integer fromHospital) {
		this.fromHospital = fromHospital;
	}

	public Date getRechargeTime() {
		return rechargeTime;
	}

	public void setRechargeTime(Date rechargeTime) {
		this.rechargeTime = rechargeTime;
	}

	public Date getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Integer getExamNoteId() {
		return examNoteId;
	}

	public void setExamNoteId(Integer examNoteId) {
		this.examNoteId = examNoteId;
	}

	public Integer getParentCardId() {
		return parentCardId;
	}

	public void setParentCardId(Integer parentCardId) {
		this.parentCardId = parentCardId;
	}

	public CardSetting getCardSetting() {
		return cardSetting;
	}

	public void setCardSetting(CardSetting cardSetting) {
		this.cardSetting = cardSetting;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Long getRecoverableBalance() {
		return recoverableBalance;
	}

	public void setRecoverableBalance(Long recoverableBalance) {
		this.recoverableBalance = recoverableBalance;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}

}
