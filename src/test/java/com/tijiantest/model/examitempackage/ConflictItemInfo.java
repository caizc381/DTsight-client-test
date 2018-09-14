package com.tijiantest.model.examitempackage;

import java.util.List;

import com.tijiantest.model.item.ExamItem;
import com.tijiantest.model.item.ItemSelectException.Conflict;

public class ConflictItemInfo {

	private ExamItem item;// 选中项
	private List<Conflict> conflictList;// 冲突关系集合

	public ExamItem getItem() {
		return item;
	}

	public void setItem(ExamItem item) {
		this.item = item;
	}

	public List<Conflict> getConflictList() {
		return conflictList;
	}

	public void setConflictList(List<Conflict> conflictList) {
		this.conflictList = conflictList;
	}

}
