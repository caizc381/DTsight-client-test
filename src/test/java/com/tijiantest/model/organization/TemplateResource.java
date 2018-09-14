package com.tijiantest.model.organization;

import java.io.Serializable;

public abstract class TemplateResource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -452006330062482723L;
	
	protected String name;
	protected String values;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	

}
