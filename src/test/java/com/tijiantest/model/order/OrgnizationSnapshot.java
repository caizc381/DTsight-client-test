package com.tijiantest.model.order;

import java.io.Serializable;


public class OrgnizationSnapshot implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1949594110162836882L;
	
	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 机构类型
	 * @see com.mytijian.resource.enums.OrganizationTypeEnum
	 */
	private int organizationType;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(int organizationType) {
		this.organizationType = organizationType;
	}

}
