package com.tijiantest.model.card;

import java.io.Serializable;
import java.util.Date;

public class CardSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8890796321765542994L;

	private Integer id;
	
	/**
	 * 卡号
	 */
	private String cardNum;
	
	/**
	 *面值
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
	 * 卡的来源机构标志 默认为空
	 */
	private Integer fromOrgId;					
	/**
	 * 卡的机构类型
	 */
	private Integer fromOrgType;					
	
	/**
	 * 可用时间
	 */
	private Date availableDate;
	
	/**
	 * 有效期
	 */
	private Date expiredDate;
	
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
	 * 是否是主卡
	 */
	private Boolean isPrimary;
	
	/**
	 * 该卡是否仅仅自己使用 ，1：是 0：否
	 */
	private Boolean isPrivate;		
	
	/**
	 * 隐藏卡金额及套餐价，1：是 0：否
	 */
	private Boolean isHiddenPrice;
	
	/**
	 * 只允许支付套餐内费用，1：是 0：否
	 */
	private Boolean isPayMealCost;
	
	/**
	 * 支付一次即冻结，1：是 0：否
	 */
	private Boolean isPayFreeze;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
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

	public Integer getFromOrgId() {
		return fromOrgId;
	}

	public void setFromOrgId(Integer fromOrgId) {
		this.fromOrgId = fromOrgId;
	}

	public Integer getFromOrgType() {
		return fromOrgType;
	}

	public void setFromOrgType(Integer fromOrgType) {
		this.fromOrgType = fromOrgType;
	}

	public Date getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
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

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Boolean getIsHiddenPrice() {
		return isHiddenPrice;
	}

	public void setIsHiddenPrice(Boolean isHiddenPrice) {
		this.isHiddenPrice = isHiddenPrice;
	}

	public Boolean getIsPayMealCost() {
		return isPayMealCost;
	}

	public void setIsPayMealCost(Boolean isPayMealCost) {
		this.isPayMealCost = isPayMealCost;
	}

	public Boolean getIsPayFreeze() {
		return isPayFreeze;
	}

	public void setIsPayFreeze(Boolean isPayFreeze) {
		this.isPayFreeze = isPayFreeze;
	}

	
}
