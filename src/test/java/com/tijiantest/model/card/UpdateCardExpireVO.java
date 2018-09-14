package com.tijiantest.model.card;

import java.util.Date;
import java.util.List;

import com.tijiantest.util.DateUtils;

public class UpdateCardExpireVO {
	private List<Integer> cardIds;

	private Date newDate;

	public List<Integer> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<Integer> cardIds) {
		this.cardIds = cardIds;
	}

	public Date getNewDate() {
		return newDate;
	}

	public void setNewDate(Date newDate) {
//		this.newDate = DateUtils.getEndTime(newDate);
		this.newDate = newDate;
	}
}
