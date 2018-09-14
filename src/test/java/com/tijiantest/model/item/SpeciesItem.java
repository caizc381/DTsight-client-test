package com.tijiantest.model.item;

import java.util.List;

public class SpeciesItem {
	private Integer id;
	private Integer groupId;
	private Boolean basic;
	private Boolean enableSelect;
	private Boolean enableCustom;
	private List<Integer> members;
	
	public SpeciesItem() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Boolean getBasic() {
		return basic;
	}
	public void setBasic(Boolean basic) {
		this.basic = basic;
	}
	public Boolean getEnableSelect() {
		return enableSelect;
	}
	public void setEnableSelect(Boolean enableSelect) {
		this.enableSelect = enableSelect;
	}
	public Boolean getEnableCustom() {
		return enableCustom;
	}
	public void setEnableCustom(Boolean enableCustom) {
		this.enableCustom = enableCustom;
	}
	public List<Integer> getMembers() {
		return members;
	}
	public void setMembers(List<Integer> members) {
		this.members = members;
	}
	
	
}
