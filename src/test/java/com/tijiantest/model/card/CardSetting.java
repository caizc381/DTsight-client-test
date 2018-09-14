package com.tijiantest.model.card;
import java.util.Date;

public class CardSetting {

	/**
	 * 卡标识
	 */
	private Integer cardId;
	
	/**
	 * 该卡是否仅仅自己使用 ，1：是 0：否
	 */
	private Boolean isPrivate;		
	
	/**
	 * 隐藏卡金额及套餐价，1：是 0：否
	 */
	private Boolean isShowCardMealPrice;
	
	/**
	 * 只允许支付套餐内费用，1：是 0：否
	 */
	private Boolean isPayMealCost;
	
	/**
	 * 支付一次即冻结，1：是 0：否
	 */
	private Boolean isPayFreeze;
	
	/**
	 * 可回收余额期限
	 **/
	private Date recoverableBalTime;
	
	/**
	 * 可撤销时间
	 **/
	private Date revocableTime;

	public CardSetting(){}
	
	public CardSetting(int cardId){
		this.cardId = cardId;
	}
	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public Boolean isShowCardMealPrice() {
		return isShowCardMealPrice;
	}

	public void setShowCardMealPrice(Boolean isShowCardMealPrice) {
		this.isShowCardMealPrice = isShowCardMealPrice;
	}

	public Boolean isPayMealCost() {
		return isPayMealCost;
	}

	public void setPayMealCost(Boolean isPayMealCost) {
		this.isPayMealCost = isPayMealCost;
	}

	public Boolean isPayFreeze() {
		return isPayFreeze;
	}

	public void setPayFreeze(Boolean isPayFreeze) {
		this.isPayFreeze = isPayFreeze;
	}

	public Date getRecoverableBalTime() {
		return recoverableBalTime;
	}

	public void setRecoverableBalTime(Date recoverableBalTime) {
		this.recoverableBalTime = recoverableBalTime;
	}

	public Date getRevocableTime() {
		return revocableTime;
	}

	public void setRevocableTime(Date revocableTime) {
		this.revocableTime = revocableTime;
	}
	
	//卡付金额不大于套餐金额，1：是 0：否
	//private boolean isCardCostLessMealCost;
	
}
