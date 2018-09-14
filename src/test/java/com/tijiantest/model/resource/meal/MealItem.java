package com.tijiantest.model.resource.meal;

public class MealItem {
	
	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 详情
	 */
	private String detail;
	/**
	 * 适宜人群
	 */
	private String fitPeople;
	/**
	 * 不适宜人群
	 */
	private String unfitPeople;
	/**
	 * 性别 0:男,1:女,2:男女通用
	 */
	private Integer gender;
	/**
	 * 拼音简写
	 */
	private String pinyin;
	/**
	 * 体检中心id
	 */
	private Integer hospitalId;
	/**
	 * 套餐id
	 */
	private Integer mealId;
	/**
	 * 现价
	 */
	private Integer price;
	/**
	 * 组项目id
	 */
	private Integer groupId;
	
	private boolean isDiscount;
	
	public MealItem() {}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getFitPeople() {
		return fitPeople;
	}

	public void setFitPeople(String fitPeople) {
		this.fitPeople = fitPeople;
	}

	public String getUnfitPeople() {
		return unfitPeople;
	}

	public void setUnfitPeople(String unfitPeople) {
		this.unfitPeople = unfitPeople;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
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

	public boolean isDiscount() {
		return isDiscount;
	}

	public void setDiscount(boolean discount) {
		this.isDiscount = discount;
	}

	public boolean isEnableCustom() {
		return enableCustom;
	}

	public void setEnableCustom(boolean enableCustom) {
		this.enableCustom = enableCustom;
	}

	public String getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}

	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 是否可打折
	 */
	@SuppressWarnings("unused")
	private boolean discount;
	/**
	 * 是否是必选项
	 */
	private boolean basic;
	/**
	 * 是否显示
	 */
	private boolean show;
	/**
	 * 如果是必须选项，下拉框是否可以选
	 */
	private boolean enableSelect;
	/**
	 * 是否可以自定义小项
	 */
	private boolean enableCustom;
	/**
	 * 顺序, 相同groupId的项目sequence应该一致
	 */
	private Integer sequence;

	/**
	 * 是否在套餐中被选中
	 */
	private boolean selected;

	/**
	 * His系统编号
	 */
	private String hisItemId;

	/**
	 * 类型：1.体检中心项目 2.自有 3.外送 4.小项 5.人数控制 6.废除项目
	 * 以resource_ddl.sql为准
	 * @see com.mytijian.resource.enums.ExamItemTypeEnum
	 */
	private Integer itemType;
	
	/**
	 * 拒检标记 1-拒检,2-提交
	 *//*
	private String refuseStatus = String.valueOf(RefuseStatusEnum.examed.getCode());*/
	
	/**
	 * 是否是重点关注项目
	 */
	private boolean focus;
	
	public MealItem(int id,int mealId,boolean basic,boolean enableSelect,int gender,boolean selected,int sequence,boolean show){
		this.id = id;
		this.mealId = mealId;
		this.basic = basic;
		this.enableSelect = enableSelect;
		this.gender = gender;
		this.selected = selected;
		this.sequence =sequence;
		this.show = show;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public boolean isBasic() {
		return basic;
	}

	public void setBasic(boolean basic) {
		this.basic = basic;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public boolean getEnableSelect() {
		return enableSelect;
	}

	public void setEnableSelect(boolean enableSelect) {
		this.enableSelect = enableSelect;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/*public String getRefuseStatus() {
		return refuseStatus;
	}

	public void setRefuseStatus(String refuseStatus) {
		this.refuseStatus = refuseStatus;
	}
	*/


	@Override
	public String toString() {
		return "[id=" + id + ", gender=" + gender + ", mealId=" + mealId 
				+ ", basic=" + basic + ", show=" + show + ", enableSelect=" + enableSelect
				+ ", sequence=" + sequence + ", selected=" + selected + "]";
	}
}
