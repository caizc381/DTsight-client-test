package com.tijiantest.model.company;

public class ChannelManagerVO {

	private Integer id;

	private String name;

	private Integer organizationId;

	private String organizationName;
	
	/**
	 * 1 :新增
	 * -1:删除
	 */
	private Integer addOrRemove;

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

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Integer getAddOrRemove() {
		return addOrRemove;
	}

	public void setAddOrRemove(Integer addOrRemove) {
		this.addOrRemove = addOrRemove;
	}
	
}
