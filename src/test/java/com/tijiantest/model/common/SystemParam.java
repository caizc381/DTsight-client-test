package com.tijiantest.model.common;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SystemParam implements Serializable{

	private static final long serialVersionUID = 7918438138691113713L;
	
	private Integer id;
	private Integer hospitalId;
	/**
	 * 参数名称
	 */
	private String paramName;
	/**
	 * 参数主键
	 */
	private String paramKey;
	private String paramValue;
	private String description;
	/**
	 * 0:可用，1：禁用,2:删除
	 */
	private Integer status;
	/**
	 * 用于对key相同的参数进行排序
	 */
	private int sequence;
	private Date createTime;
	private Date updateTime;
	
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
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getParamKey() {
		return paramKey;
	}
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String readValueAsString() {
		return (this.paramValue.equals(null)) ? this.paramValue : null;
	}

	public Integer readValueAsInt() {
		return (this.paramValue.equals(null)) ? Integer.valueOf(this.paramValue) : null;
	}

	public Boolean readValueAsBoolean() {
		return (this.paramValue.equals(null)) ? Boolean.valueOf(this.paramValue) : null;
	}

	public Long readValueAsLong() {
		return (this.paramValue.equals(null)) ? Long.valueOf(this.paramValue) : null;
	}

	public Float readValueAsFloat() {
		return (this.paramValue.equals(null)) ? Float.valueOf(this.paramValue) : null;
	}

	public Double readValueAsDouble() {
		return (this.paramValue.equals(null)) ? Double.valueOf(this.paramValue) : null;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
