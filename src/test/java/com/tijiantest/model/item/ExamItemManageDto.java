package com.tijiantest.model.item;

import java.util.List;
import java.util.Map;

public class ExamItemManageDto {
	private List<ExamItem> itemList;
	
	private Map<Integer, String> relationMap;//单项的关系列表

	public List<ExamItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ExamItem> itemList) {
		this.itemList = itemList;
	}

	public Map<Integer, String> getRelationMap() {
		return relationMap;
	}

	public void setRelationMap(Map<Integer, String> relationMap) {
		this.relationMap = relationMap;
	}
	
	
	
}
