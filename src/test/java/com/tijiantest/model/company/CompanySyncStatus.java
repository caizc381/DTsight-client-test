package com.tijiantest.model.company;
/**
 * 单位同步状态
 *
 */
public enum CompanySyncStatus {
	creating(0),finish(1),crmexception(2);
	private int status;
	private CompanySyncStatus(int status){
		this.status=status;
	}
	public int getStatus(){
		return status;
	}

}
