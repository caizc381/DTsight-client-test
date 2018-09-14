package com.tijiantest.model.organization;

public class SiteHtmlTemplate {

	private Integer id;
	
	private String name;
	
	private Integer type;
	
	/**
	 * 默认色调
	 */
	private Integer defaultCssId;

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

	public Integer getDefaultCssId() {
		return defaultCssId;
	}

	public void setDefaultCssId(Integer defaultCssId) {
		this.defaultCssId = defaultCssId;
	}
}
	
