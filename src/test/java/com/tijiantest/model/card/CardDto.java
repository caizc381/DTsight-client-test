package com.tijiantest.model.card;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tijiantest.model.hospital.HostpitalMealDto;

public class CardDto {

	/**
	 * 发卡账户列表
	 */
	private List<Integer> accountIdList;
	
	
	/**
	 * 主账户id和总账户id映射集合
	 */
	@SuppressWarnings("unused")
	private Map<Integer, Integer> accountIdMapping;
	
	/**
	 * 代发卡信息
	 */
	private Card card;
	
	/**
	 * 体检中心套餐关系
	 */
	private List<HostpitalMealDto> cardMealList;
	
	/**
	 * 套餐价格
	 */
	private Integer mealPrice;
	
	/**
	 * 下单期限
	 */
	private Date bookingDeadline;
	/**
	 * 发送发卡短信
	 */
	private Boolean isSendCardMsg;
	/**
	 * 发送下单短信
	 */
	private Boolean isSendBookingMsg;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 体检须知
	 */
	private String examNote;
	
	/**
	 * 缺省套餐id
	 */
	private Integer defaultMealId;
	
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
	
	private Integer batchId;
	
	private String smsMsgTemplate;
	/**
	 * 发卡客户筛选条件
	 */
	private String queryCondition;
	/**
	 * 实体卡,发卡张数
	 */
	private Integer  entryCardTotal;
	
	/**
	 * 实体卡号段<start,end>
	 */
	private String startSegment;
	private String endSegment;
	
	public String getSmsMsgTemplate() {
		return smsMsgTemplate;
	}
	public void setSmsMsgTemplate(String smsMsgTemplate) {
		this.smsMsgTemplate = smsMsgTemplate;
	}
	public String getQueryCondition() {
		return queryCondition;
	}
	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}
	public Integer getEntryCardTotal() {
		return entryCardTotal;
	}
	public void setEntryCardTotal(Integer entryCardTotal) {
		this.entryCardTotal = entryCardTotal;
	}
	public String getStartSegment() {
		return startSegment;
	}
	public void setStartSegment(String startSegment) {
		this.startSegment = startSegment;
	}
	public String getEndSegment() {
		return endSegment;
	}
	public void setEndSegment(String endSegment) {
		this.endSegment = endSegment;
	}
	public List<Integer> getAccountIdList() {
		return accountIdList;
	}
	public void setAccountIdList(List<Integer> accountIdList) {
		this.accountIdList = accountIdList;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public List<HostpitalMealDto> getCardMealList() {
		return cardMealList;
	}
	public void setCardMealList(List<HostpitalMealDto> cardMealList) {
		this.cardMealList = cardMealList;
	}
	public Integer getMealPrice() {
		return mealPrice;
	}
	public void setMealPrice(Integer mealPrice) {
		this.mealPrice = mealPrice;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public Date getBookingDeadline() {
		return bookingDeadline;
	}
	public void setBookingDeadline(Date bookingDeadline) {
		this.bookingDeadline = bookingDeadline;
	}
	public Boolean getIsSendCardMsg() {
		return isSendCardMsg;
	}
	public void setIsSendCardMsg(Boolean isSendCardMsg) {
		this.isSendCardMsg = isSendCardMsg;
	}
	public Boolean getIsSendBookingMsg() {
		return isSendBookingMsg;
	}
	public void setIsSendBookingMsg(Boolean isSendBookingMsg) {
		this.isSendBookingMsg = isSendBookingMsg;
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
