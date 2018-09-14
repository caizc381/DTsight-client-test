package com.tijiantest.model.order;

/***********************************************************************
 * Module:  Recidence.java
 * Author:  Administrator
 * Purpose: Defines the Class Recidence
 ***********************************************************************/

import java.io.Serializable;

public class Recidence implements Serializable {

	private static final long serialVersionUID = -359139230652554183L;

	private Integer id;

	private String name;

	private Integer price;

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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

}