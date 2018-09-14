package com.tijiantest.model.order;

import java.util.ArrayList;
import java.util.List;

import com.tijiantest.model.item.ExamItem;

public class ItemsInOrder {
	
	//套餐内初始项目
	List<ExamItem> itemsInMeal = new ArrayList<ExamItem>();
	
	//套餐外加的项目
	List<ExamItem> addedItem = new ArrayList<ExamItem>();
	
	//套餐内减的项目
	List<ExamItem> reducedItems = new ArrayList<ExamItem>();
	
	//所有项目包括套餐内项目、减项、加项
	List<ExamItem> allRelatedItems = new ArrayList<ExamItem>();
	
	//最终预约的单项
	List<ExamItem> finalItems = new ArrayList<ExamItem>();
	
	//最终套餐内项目，不包括减项
	List<ExamItem> finalItemsInMeal = new ArrayList<ExamItem>();

	//重复项
	List<ExamItem> duplicateItems = new ArrayList<ExamItem>();
	
	public List<ExamItem> getItemsInMeal() {
		return itemsInMeal;
	}
	public void setItemsInMeal(List<ExamItem> itemsInMeal) {
		this.itemsInMeal = itemsInMeal;
	}
	public List<ExamItem> getAddedItem() {
		return addedItem;
	}
	public void setAddedItem(List<ExamItem> addedItem) {
		this.addedItem = addedItem;
	}
	public List<ExamItem> getReducedItems() {
		return reducedItems;
	}
	public void setReducedItems(List<ExamItem> reducedItems) {
		this.reducedItems = reducedItems;
	}
	public List<ExamItem> getAllRelatedItems() {
		return allRelatedItems;
	}
	public void setAllRelatedItems(List<ExamItem> allRelatedItems) {
		this.allRelatedItems = allRelatedItems;
	}
	public List<ExamItem> getFinalItems() {
		return finalItems;
	}
	public void setFinalItems(List<ExamItem> finalItems) {
		this.finalItems = finalItems;
	}
	public List<ExamItem> getFinalItemsInMeal() {
		return finalItemsInMeal;
	}
	public void setFinalItemsInMeal(List<ExamItem> finalItemsInMeal) {
		this.finalItemsInMeal = finalItemsInMeal;
	}
	public List<ExamItem> getDuplicateItems() {
		return duplicateItems;
	}
	public void setDuplicateItems(List<ExamItem> duplicateItems) {
		this.duplicateItems = duplicateItems;
	}
	
	
}
