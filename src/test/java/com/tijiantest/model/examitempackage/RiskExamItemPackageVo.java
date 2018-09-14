package com.tijiantest.model.examitempackage;

import java.io.Serializable;
import java.util.List;

import com.tijiantest.model.item.ExamItem;

//加项包
public class RiskExamItemPackageVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2851725487849780016L;

	private Integer id;
	private Integer hospitalId;
	private Integer type;
	private String name;
	private Integer gender;
	private String description;

	private Integer price;// 售价
	private Integer initPrice;// 原价
	private Integer displayPrice;// 标价
	private Integer adjustPrice;
	private Boolean isShow;
	private Integer disable;
	private Boolean showInitPrice;
	private String pinyin;
	private Integer sequence;
	private List<ExamItem> itemList;
	private AccountRiskItem accountRiskItem;
	
	private List<RiskExamItemPackageVo> packageTags;// 标签列表

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getInitPrice() {
		return initPrice;
	}

	public void setInitPrice(Integer initPrice) {
		this.initPrice = initPrice;
	}

	public Integer getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Integer displayPrice) {
		this.displayPrice = displayPrice;
	}

	public Integer getAdjustPrice() {
		return adjustPrice;
	}

	public void setAdjustPrice(Integer adjustPrice) {
		this.adjustPrice = adjustPrice;
	}

	public Boolean getIsShow() {
		return isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	public Integer getDisable() {
		return disable;
	}

	public void setDisable(Integer disable) {
		this.disable = disable;
	}

	public Boolean getShowInitPrice() {
		return showInitPrice;
	}

	public void setShowInitPrice(Boolean showInitPrice) {
		this.showInitPrice = showInitPrice;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public List<ExamItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ExamItem> itemList) {
		this.itemList = itemList;
	}

	public List<RiskExamItemPackageVo> getPackageTags() {
		return packageTags;
	}

	public void setPackageTags(List<RiskExamItemPackageVo> packageTags) {
		this.packageTags = packageTags;
	}

	public AccountRiskItem getAccountRiskItem() {
		return accountRiskItem;
	}

	public void setAccountRiskItem(AccountRiskItem accountRiskItem) {
		this.accountRiskItem = accountRiskItem;
	}

}
