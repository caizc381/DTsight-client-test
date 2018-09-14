package com.tijiantest.model.card;

import java.util.Date;

import com.tijiantest.util.DateUtils;

public class EditExpiredDateDto {
	private Integer[] cardIds;
	private Date expiredDate;
	public Integer[] getCardIds() {
		return cardIds;
	}
	public void setCardIds(Integer[] cardIds) {
		this.cardIds = cardIds;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = DateUtils.toDayLastSecod(expiredDate);
	}
	
	
}
