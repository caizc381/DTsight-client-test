package com.dtstack.util.validator;

public class ImportError {
	private Integer key;
	private String description;
	
	public ImportError(Integer key, String description) {
		this.key = key;
		this.description = description;
	}
	
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
