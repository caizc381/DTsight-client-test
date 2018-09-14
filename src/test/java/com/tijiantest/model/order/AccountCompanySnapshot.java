package com.tijiantest.model.order;

import java.io.Serializable;

public class AccountCompanySnapshot implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2844936024839894333L;
	/**
	 * id
	 */
	private Integer id; 
	/**
	 * 挂账单位名称
	 */
	private String name; 
	/**
	 * 挂账类型
	 */
	private Integer type; 
	/**
	 * 组织机构id
	 */
	private Integer orgnizationId;
	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 介绍人名称
	 */
	private String owner;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getOrgnizationId() {
		return orgnizationId;
	}
	public void setOrgnizationId(Integer orgnizationId) {
		this.orgnizationId = orgnizationId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
