package com.tijiantest.model.resource.meal;

import com.tijiantest.util.PinYinUtil;

public class Meal {
 
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
	 * 拼音简写
	 */
	private String pinyin;
	/**
	 * 体检中心id
	 */
	private Integer hospitalId;
	
	/**
	 * 现价
	 */
	private Integer price;
	/**
	 * 原价
	 */
	private Integer initPrice;
	/**
	 * 折扣
	 */
	private Double discount;
	/**
	 * 套餐外折扣
	 */
	private Double externalDiscount;
	/**
	 * 性别 0:男,1:女,2:男女通用
	 */
	private Integer gender;
	/**
	 * 是否可用
	 */
	private Integer disable;
	/**
	 * 热度
	 */
	private Integer hot;
	/**
	 * 关键词
	 */
	private String keyword;
	/**
	 * 注意事项
	 */
	private String tipText;
	
	/**
	 * 套餐类型，1:单位套餐，2：收藏套餐，3：通用套餐
	 */
	private Integer type;
	
	/**
	 * 是否显示套餐、单项原价
	 */
	private MealSetting mealSetting;
	
	/**
	 * 序列
	 */
	private Integer sequence;
	private Integer displayPrice;
	
	public Meal(){}
	
	public Meal(int id){
		this.id = id;
	}
	
	public Meal(int id,String name,int initPrice,int price,String description,
			int hospitalid,double discount,int gender,
			int disable,String keyword,int type,MealSetting mealSetting){
		this.id = id;
		this.name = name;
		this.pinyin = PinYinUtil.getFirstSpell(name);
		this.initPrice = initPrice;
		this.price = price;
		this.description = description;
		this.hospitalId = hospitalid;
		this.discount = discount;
		this.disable = disable;
		this.keyword = keyword;
		this.gender = gender;
		this.type = type;
		this.mealSetting = mealSetting;
	}
	
	public Meal(int id,String name,String description,
			int hospitalid,double discount,int gender,
			int disable,String keyword,int type,MealSetting mealSetting){
		this.id = id;
		this.name = name;
		this.pinyin = PinYinUtil.getFirstSpell(name);
		this.description = description;
		this.hospitalId = hospitalid;
		this.discount = discount;
		this.disable = disable;
		this.keyword = keyword;
		this.gender = gender;
		this.sequence = 0;
		this.type = type;
		this.mealSetting = mealSetting;
	}
	
	public Meal(int id,String name,int gender,int price,int hospitalid,int init_price,int type,double discount){
		this(id, name, gender, price, hospitalid);
		this.initPrice = init_price;
		this.type = type;
		this.discount = discount;	
	}
	
	public Meal(int id,String name,int gender,int price,
			int hospitalid,int init_price,int type,double discount,
			int disable,String pinYin){
		this(id, name, gender, price, hospitalid,init_price,type,discount);	
		this.disable = disable;
		this.pinyin = pinYin;
	}
	
	public Meal(int id,String name,int gender,int price,int hospitalid){
		this.id = id;
		this.name = name;
		this.pinyin = PinYinUtil.getFirstSpell(name);
		this.price = price;
		this.hospitalId = hospitalid;
		this.gender = gender;
	}
	
	public Meal(int id,int hospitalid){
		this.id = id;
		this.hospitalId = hospitalid;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return  PinYinUtil.fullWidth2halfWidth(name);
	}
	public void setName(String name) {
		this.name =  PinYinUtil.fullWidth2halfWidth(name);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Integer getInitPrice() {
		return initPrice;
	}
	public void setInitPrice(Integer initPrice) {
		this.initPrice = initPrice;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getDisable() {
		return disable;
	}
	public void setDisable(Integer disable) {
		this.disable = disable;
	}
	public Integer getHot() {
		return hot;
	}
	public void setHot(Integer hot) {
		this.hot = hot;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getTipText() {
		return tipText;
	}
	public void setTipText(String tipText) {
		this.tipText = tipText;
	}
	public Double getExternalDiscount() {
		return externalDiscount;
	}
	public void setExternalDiscount(Double externalDiscount) {
		this.externalDiscount = externalDiscount;
	}
	public MealSetting getMealSetting() {
		return mealSetting;
	}
	public void setMealSetting(MealSetting mealSetting) {
		this.mealSetting = mealSetting;
	}
	
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getDisplayPrice() {
		return displayPrice;
	}

	public void setDisplayPrice(Integer displayPrice) {
		this.displayPrice = displayPrice;
	}

}
