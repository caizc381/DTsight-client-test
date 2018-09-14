package com.tijiantest.model.organization;

import java.util.List;

public class Module {

	private Integer id;
	private String name;
	private Integer type;
	private String description;
    private String alias;
    private List<? extends TemplateResource> resources;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public List<? extends TemplateResource> getResources() {
		return resources;
	}
	public void setResources(List<? extends TemplateResource> resources) {
		this.resources = resources;
	}
    
    
}
