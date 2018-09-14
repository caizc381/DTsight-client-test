package com.tijiantest.model.account;

import java.util.List;
/**
 * 
 * @author ChenSijia
 *
 */
public class Confirm {
	private int companyId;
	private List<String> sheetNames;
	private String group;
	private String fileName;
	public Confirm(int companyId,List<String> sheetNames,String group,String fileName){
		this.companyId=companyId;
		this.sheetNames=sheetNames;
		this.group=group;
		this.fileName=fileName;
		
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	
	public List<String> getSheetNames() {
		return sheetNames;
	}
	public void setSheetNames(List<String> sheetNames) {
		this.sheetNames = sheetNames;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
