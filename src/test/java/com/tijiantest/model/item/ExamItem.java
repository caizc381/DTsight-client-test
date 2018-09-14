package com.tijiantest.model.item;

import com.tijiantest.util.PinYinUtil;

public class ExamItem {
 private int id;
 private String name;
 private String description;
 private String detail;
 private String fitPeople;
 private String unfitPeople;
 private Integer gender;
 private String pinyin;
 private Integer hospitalId;
 private Integer mealId;
 private Integer price;
 private Integer groupId;
 private boolean discount = true;
 private boolean basic = false;
 private boolean show = true;
 private boolean enableSelect = true;
 private boolean enableCustom = false;
 private Integer sequence;
 private boolean selected = true;
 private String hisItemId;
 private Integer itemType;
 private String refuseStatus;
 private boolean focus;
 private Integer departmentId;
 private boolean syncPrice;
 private String tagName;
 private String warning;
 private Boolean showWarning;
 private boolean bottleneck;
 public ExamItem() {
     this.refuseStatus = String.valueOf(RefuseStatusEnum.examed.getCode());
 }

 public int getId() {
     return this.id;
 }

 public void setId(int id) {
     this.id = id;
 }

 public String getName() {
     return PinYinUtil.fullWidth2halfWidth(this.name);
 }

 public void setName(String name) {
     this.name = PinYinUtil.fullWidth2halfWidth(name);
 }

 public String getDescription() {
     return this.description;
 }

 public void setDescription(String description) {
     this.description = description;
 }

 public Integer getGender() {
     return this.gender;
 }

 public void setGender(Integer gender) {
     this.gender = gender;
 }

 public String getPinyin() {
     return this.pinyin;
 }

 public void setPinyin(String pinyin) {
     this.pinyin = pinyin;
 }

 public boolean isDiscount() {
     return this.discount;
 }

 public void setDiscount(boolean discount) {
     this.discount = discount;
 }

 public boolean isBasic() {
     return this.basic;
 }

 public void setBasic(boolean basic) {
     this.basic = basic;
 }

 public boolean isShow() {
     return this.show;
 }

 public void setShow(boolean show) {
     this.show = show;
 }

 public Integer getHospitalId() {
     return this.hospitalId;
 }

 public void setHospitalId(Integer hospitalId) {
     this.hospitalId = hospitalId;
 }

 public Integer getPrice() {
     return this.price;
 }

 public void setPrice(Integer price) {
     this.price = price;
 }

 public Integer getGroupId() {
     return this.groupId;
 }

 public void setGroupId(Integer groupId) {
     this.groupId = groupId;
 }

 public Integer getMealId() {
     return this.mealId;
 }

 public void setMealId(Integer mealId) {
     this.mealId = mealId;
 }

 public boolean getEnableSelect() {
     return this.enableSelect;
 }

 public void setEnableSelect(boolean enableSelect) {
     this.enableSelect = enableSelect;
 }

 public Integer getSequence() {
     return this.sequence;
 }

 public void setSequence(Integer sequence) {
     this.sequence = sequence;
 }

 public String getType() {
     return "item";
 }

 public String getFitPeople() {
     return this.fitPeople;
 }

 public void setFitPeople(String fitPeople) {
     this.fitPeople = fitPeople;
 }

 public String getUnfitPeople() {
     return this.unfitPeople;
 }

 public void setUnfitPeople(String unfitPeople) {
     this.unfitPeople = unfitPeople;
 }

 public String getDetail() {
     return this.detail;
 }

 public void setDetail(String detail) {
     this.detail = detail;
 }

 public boolean isEnableCustom() {
     return this.enableCustom;
 }

 public void setEnableCustom(boolean enableCustom) {
     this.enableCustom = enableCustom;
 }

 public boolean isSelected() {
     return this.selected;
 }

 public void setSelected(boolean selected) {
     this.selected = selected;
 }

 public String getHisItemId() {
     return this.hisItemId;
 }

 public void setHisItemId(String hisItemId) {
     this.hisItemId = hisItemId;
 }

 public Integer getItemType() {
     return this.itemType;
 }

 public void setItemType(Integer itemType) {
     this.itemType = itemType;
 }

 public String getRefuseStatus() {
     return this.refuseStatus;
 }

 public void setRefuseStatus(String refuseStatus) {
     this.refuseStatus = refuseStatus;
 }

 public boolean isFocus() {
     return this.focus;
 }

 public void setFocus(boolean focus) {
     this.focus = focus;
 }

 public Integer getDepartmentId() {
     return this.departmentId;
 }

 public void setDepartmentId(Integer departmentId) {
     this.departmentId = departmentId;
 }

 public boolean isSyncPrice() {
	return syncPrice;
}

public void setSyncPrice(boolean syncPrice) {
	this.syncPrice = syncPrice;
}

public String getTagName() {
	return tagName;
}

public void setTagName(String tagName) {
	this.tagName = tagName;
}

public String getWarning() {
	return warning;
}

public void setWarning(String warning) {
	this.warning = warning;
}

public Boolean getShowWarning() {
	return showWarning;
}

public void setShowWarning(Boolean showWarning) {
	this.showWarning = showWarning;
}

public boolean isBottleneck() {
	return bottleneck;
}

public void setBottleneck(boolean bottleneck) {
	this.bottleneck = bottleneck;
}

public String toString() {
     return "ExamItem [id=" + this.id + ", name=" + this.name + ", gender=" + this.gender + ", hospitalId=" + this.hospitalId + ", mealId=" + this.mealId + ", price=" + this.price + ", groupId=" + this.groupId + ", discount=" + this.discount + ", basic=" + this.basic + ", show=" + this.show + ", enableSelect=" + this.enableSelect + ", enableCustom=" + this.enableCustom + ", sequence=" + this.sequence + ", selected=" + this.selected + ", hisItemId=" + this.hisItemId + ", itemType=" + this.itemType + ",syncPrice="+this.syncPrice+"]";
 }
}
