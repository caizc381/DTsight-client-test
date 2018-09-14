package com.tijiantest.model.account;

import java.util.List;
import java.util.Map;

public class TitleMatcher {
	
	/**
	 * 表单名称
	 */
	private String sheetName;
	
	/**
	 * 前几行预览数据
	 */
	private List<Map<Integer, String>> previewImportAccount;
	/**
	 * 表头字段和所在列号的对应关系
	 */
	private Map<Integer, String> columnMap;
	
	public List<Map<Integer, String>> getPreviewImportAccount() {
		return previewImportAccount;
	}
	public void setPreviewImportAccount(List<Map<Integer, String>> previewImportAccount) {
		this.previewImportAccount = previewImportAccount;
	}
	public Map<Integer, String> getColumnMap() {
		return columnMap;
	}
	public void setColumnMap(Map<Integer, String> columnMap) {
		this.columnMap = columnMap;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
}
