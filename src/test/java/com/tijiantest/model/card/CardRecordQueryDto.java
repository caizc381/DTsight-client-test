package com.tijiantest.model.card;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.tijiantest.util.pagination.Page;

public class CardRecordQueryDto implements Serializable{

	private static final long serialVersionUID = 6347158532321493887L;
	/**
	 * 搜索关键字
	 */
	private String searchKey;
	
	// 分页信息
	private Page page;

	private Integer managerId;
	
	/**
	 * 体检单位id
	 */
	private Integer companyId;
	
	/**
	 * 新体检单位id
	 */
	private Integer newCompanyId;
	
	/**
	 * 机构类型
	 * @see com.mytijian.resource.enums.OrganizationTypeEnum
	 */
	private Integer organizationType;

	/**
	 * 机构id
	 */
	private Integer fromHospital;
	
	//卡状态
	private Integer status;
	
	//卡的使用状态 （0：未使用， 1： 已使用）
	private Integer useStatus;
	
	//不显示0元容量的体检卡
	private boolean hideZeroCapacityCard;
	
	/**
	 * 用户id列表
	 */
	private Set<Integer>accountIds;
	
	/**
	 * 卡批次id列表
	 */
	private Integer[] batchIds;


	/**
	 * 发卡时间
	 */
	private Date  createTimeStart;

	/**
	 * 发卡时间
	 */
	private Date  createTimeEnd;

	/**
	 * 卡号
	 */
	private String cardNum;


	/**
	 * 卡余额是否为0 true  balance==0  false balance > 0
	 * null all
	 */
	private Boolean cardBalanceIsZero;


	/**
	 * 卡面值是否为0 true  capacity==0  false capacity > 0
	 * null all
	 */
	private Boolean cardCapacityIsZero;

	/**
	 * 是否过期
	 */
	private Boolean expired;

	/**
	 * 绑定状态  1.已经绑定  2.未绑定
	 */
	private Integer bindStatus;

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public Integer getFromHospital() {
		return fromHospital;
	}

	public void setFromHospital(Integer fromHospital) {
		this.fromHospital = fromHospital;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public Boolean getCardBalanceIsZero() {
		return cardBalanceIsZero;
	}

	public void setCardBalanceIsZero(Boolean cardBalanceIsZero) {
		this.cardBalanceIsZero = cardBalanceIsZero;
	}

	public Boolean getCardCapacityIsZero() {
		return cardCapacityIsZero;
	}

	public void setCardCapacityIsZero(Boolean cardCapacityIsZero) {
		this.cardCapacityIsZero = cardCapacityIsZero;
	}

	public Integer getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(Integer bindStatus) {
		this.bindStatus = bindStatus;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(Integer useStatus) {
		this.useStatus = useStatus;
	}
	
	public boolean isHideZeroCapacityCard() {
		return hideZeroCapacityCard;
	}

	public void setHideZeroCapacityCard(boolean hideZeroCapacityCard) {
		this.hideZeroCapacityCard = hideZeroCapacityCard;
	}

	public Integer[] getBatchIds() {
		return batchIds;
	}

	public void setBatchIds(Integer[] batchIds) {
		this.batchIds = batchIds;
	}

	public Set<Integer> getAccountIds() {
		return accountIds;
	}

	public void setAccountIds(Set<Integer> accountIds) {
		this.accountIds = accountIds;
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
