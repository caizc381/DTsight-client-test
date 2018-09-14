package com.tijiantest.model.item;

public class ExamItemSpeciesDto {
	private int id;
	//名称
	private String name;
	//性别：0-男；1-女；2-男女通用
	private Integer gender;
	//拼音简写
	private String pinyin;
	//是否显示
	private boolean show = true;
	//体检中心ID
	private Integer hospitalId;
	//现价
	private Integer price;
	//组项目ID
	private Integer groupId;
	//顺序：相同groupId的项目sequence应该一致
	private Integer sequence;
	//His 系统编号
	private String hisItemId;
	private Integer type;
	//所属类别
	private Integer speciesId;
	//类别名字
	private String speciesName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getHisItemId() {
		return hisItemId;
	}
	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSpeciesId() {
		return speciesId;
	}
	public void setSpeciesId(Integer speciesId) {
		this.speciesId = speciesId;
	}
	public String getSpeciesName() {
		return speciesName;
	}
	public void setSpeciesName(String speciesName) {
		this.speciesName = speciesName;
	}
	
}
