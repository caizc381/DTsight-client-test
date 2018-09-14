package com.tijiantest.model.company;

import java.io.Serializable;

public class AccountCompany implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4052459678430710514L;
	private Integer id; //主键
	private String name;//挂账单位名称
	private Integer type;//挂账类型
	
	private String owner;//介绍人
	private String description;//描述
	private Integer hospitalId;//体检中心标志
	private String settlementName;//结算单位
	private Integer status=0;

	public AccountCompany() {
		super();
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getSettlementName() {
		return settlementName;
	}

	public void setSettlementName(String settlementName) {
		this.settlementName = settlementName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	

}
