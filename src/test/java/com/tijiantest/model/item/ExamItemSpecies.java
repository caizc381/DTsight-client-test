package com.tijiantest.model.item;

import java.util.List;

public class ExamItemSpecies {
	
	private Integer id;
	//项目类别名称
	private String name;
	//类型：1-按官方分类；2- 按身体部位
	private int type =1;
	//医院ID
	private Integer hospitalId;
	//类别描述
	private String description;
	//排序顺序
	private int sequence =0;
	//类别中的项目集合
	private List<SpeciesItem> examItemList;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public List<SpeciesItem> getExamItemList() {
		return examItemList;
	}
	public void setExamItemList(List<SpeciesItem> examItemList) {
		this.examItemList = examItemList;
	}
	
	
}
