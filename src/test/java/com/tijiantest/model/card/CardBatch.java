package com.tijiantest.model.card;


import java.util.Date;

import com.tijiantest.util.DateUtils;
public class CardBatch {

	private Integer id;
	
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
	 */
	private Integer organizationType;
	
	private String cardName;
	/**
	 * 面值
	 */
	private Long capacity;
	/**
	 * 发卡总数
	 */
	private Integer amount;
	/**
	 * 发卡人id
	 */
	private Integer operatorId;
	
	/**
	 * 发卡人
	 */
	private String operatorName;
	/**
	 * 发卡时间
	 */
	private Date createTime;
	
	/**
	 * 下单期限
	 */
	private Date bookingDeadline;

	/**
	 * 发送下单短信
	 */
	private Boolean isSendBookingMsg;
	/**
	 * 发送发卡短信
	 */
	private Boolean isSendCardMsg;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 缺省套餐id
	 */
	private Integer defaultMealId;
	
	/**
	 * 体检须知
	 */
	private String examNote;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
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

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Long getCapacity() {
		return capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getOperatorId() {
		return operatorId;
	}
	
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getBookingDeadline() {
		return bookingDeadline;
	}

	public void setBookingDeadline(Date bookingDeadline) {
		if(bookingDeadline != null){
			this.bookingDeadline = DateUtils.toDayStartSecond(bookingDeadline);
		}
	}

	public Boolean getIsSendBookingMsg() {
		if(isSendBookingMsg != null){
			isSendBookingMsg = false;
		}
		return isSendBookingMsg;
	}

	public void setIsSendBookingMsg(Boolean isSendBookingMsg) {
		this.isSendBookingMsg = isSendBookingMsg;
	}

	public Boolean getIsSendCardMsg() {
		return isSendCardMsg;
	}

	public void setIsSendCardMsg(Boolean isSendCardMsg) {
		this.isSendCardMsg = isSendCardMsg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDefaultMealId() {
		return defaultMealId;
	}

	public void setDefaultMealId(Integer defaultMealId) {
		this.defaultMealId = defaultMealId;
	}

	public String getExamNote() {
		return examNote;
	}

	public void setExamNote(String examNote) {
		this.examNote = examNote;
	}
	
}
