package com.tijiantest.model.item;

import java.util.List;

public class limitExamItemsVo {
	
	public Integer itemId;
	
	public List<ExamItem> limitItems;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public List<ExamItem> getLimitItems() {
		return limitItems;
	}

	public void setLimitItems(List<ExamItem> limitItems) {
		this.limitItems = limitItems;
	}
}
