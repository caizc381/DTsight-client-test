package com.tijiantest.model.item;

import java.io.Serializable;

/**
 * 体检单项标签
 * @author tangsir
 *
 */
public class ExamItemTag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5765629432714707351L;
	private Integer id;
	private Integer hospitalId;
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
