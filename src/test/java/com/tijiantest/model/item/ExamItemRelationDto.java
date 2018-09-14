package com.tijiantest.model.item;

import java.util.List;

/**
 * 单项关系Dto
 * 
 * @author Administrator
 *
 */
public class ExamItemRelationDto {
	private Integer itemId;// 单项
	private Integer relatedItemId;// 关系单项
	/**
	 * 关系类型
	 * 
	 * @see com.tijiantest.model.item.ExamItemRelationEnum.java
	 */
	private Integer type;
	private String hisItemId;// itemId对应的his_item_id
	private List<ExamItem> examItemGroupList;// 分组
	private List<ExamItem> examItemComposeList;// 合并 父
	private List<ExamItem> examItemBeComposeList;// 合并 子
	private List<ExamItem> examItemConflictList;// 冲突
	private List<ExamItem> examItemDependList;// 依赖
	private List<ExamItem> examItemDependedList;// 被依赖
	private List<ExamItem> examItemFatherList;// 父
	private List<ExamItem> examItemSonList;// 子
	private List<ExamItem> limitItems;// 人数控制

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getRelatedItemId() {
		return relatedItemId;
	}

	public void setRelatedItemId(Integer relatedItemId) {
		this.relatedItemId = relatedItemId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}

	public List<ExamItem> getExamItemGroupList() {
		return examItemGroupList;
	}

	public void setExamItemGroupList(List<ExamItem> examItemGroupList) {
		this.examItemGroupList = examItemGroupList;
	}

	public List<ExamItem> getExamItemComposeList() {
		return examItemComposeList;
	}

	public void setExamItemComposeList(List<ExamItem> examItemComposeList) {
		this.examItemComposeList = examItemComposeList;
	}

	public List<ExamItem> getExamItemBeComposeList() {
		return examItemBeComposeList;
	}

	public void setExamItemBeComposeList(List<ExamItem> examItemBeComposeList) {
		this.examItemBeComposeList = examItemBeComposeList;
	}

	public List<ExamItem> getExamItemConflictList() {
		return examItemConflictList;
	}

	public void setExamItemConflictList(List<ExamItem> examItemConflictList) {
		this.examItemConflictList = examItemConflictList;
	}

	public List<ExamItem> getExamItemDependList() {
		return examItemDependList;
	}

	public void setExamItemDependList(List<ExamItem> examItemDependList) {
		this.examItemDependList = examItemDependList;
	}

	public List<ExamItem> getExamItemDependedList() {
		return examItemDependedList;
	}

	public void setExamItemDependedList(List<ExamItem> examItemDependedList) {
		this.examItemDependedList = examItemDependedList;
	}

	public List<ExamItem> getExamItemFatherList() {
		return examItemFatherList;
	}

	public void setExamItemFatherList(List<ExamItem> examItemFatherList) {
		this.examItemFatherList = examItemFatherList;
	}

	public List<ExamItem> getExamItemSonList() {
		return examItemSonList;
	}

	public void setExamItemSonList(List<ExamItem> examItemSonList) {
		this.examItemSonList = examItemSonList;
	}

	public List<ExamItem> getLimitItems() {
		return limitItems;
	}

	public void setLimitItems(List<ExamItem> limitItems) {
		this.limitItems = limitItems;
	}

}
