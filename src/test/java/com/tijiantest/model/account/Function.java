package com.tijiantest.model.account;

import java.io.Serializable;

public class Function extends DomainObjectBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1715987286183258407L;

	private Integer id;
	private String code;
	private String name;
	private Integer status;// 1-启用；0-停用

	/**
	 * 
	 */
	public Function() {
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

}
