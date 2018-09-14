package com.tijiantest.model.hospital;

import java.io.Serializable;

public class Hospital implements Serializable {

	/**
	 * 体检中心启用
	 */
	public static final int STATUS_ENABLE = 1;
	/**
	 * 体检中心停用
	 */
	public static final int STATUS_DISABLE = 0;
	private static final long serialVersionUID = -3535812094971041348L;

	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 简介
	 */
	private String briefIntro;
	/**
	 * 详细介绍 this will only load while get single hospital
	 */
	private String detailIntro;
	/**
	 * 提醒信息
	 */
	private String tipText;
	/**
	 * 提醒信息
	 */
	private String keywords;
	/**
	 * 体检须知 this will only load while get single hospital
	 */
	private String examNotice;
	/**
	 * 医院类型
	 */
	private String type;
	/**
	 * 地址信息
	 */
	private Address address;
	/**
	 * 医院设置 this will only load while get single hospital
	 */
	private HospitalSettings settings;
	/**
	 * 热度
	 */
	private Integer hot;
	/**
	 * 点击次数
	 */
	private Integer clickCount;
	/**
	 * 订单数
	 */
	private Integer orderCount;
	/**
	 * 体检卡数量
	 */
	private Integer cardCount;
	private int sequence;

	/**
	 * 机构类型
	 * 
	 * @see com.mytijian.resource.enums.OrganizationTypeEnum
	 */
	private int organizationType;

	private Integer enable;
	/**
	 * 机构缺省客户经理
	 */
	private Integer defaultManagerId;

	/**
	 * 是否在列表中显示
	 **/
	private int showInList;

	/**
	 * 经度
	 */
	private String longitude;
	/**
	 * 纬度
	 */
	private String latitude;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBriefIntro() {
		return briefIntro;
	}

	public void setBriefIntro(String briefIntro) {
		this.briefIntro = briefIntro;
	}

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}

	public String getDetailIntro() {
		return detailIntro;
	}

	public void setDetailIntro(String detailIntro) {
		this.detailIntro = detailIntro;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getClickCount() {
		return clickCount;
	}

	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}

	public String getExamNotice() {
		return examNotice;
	}

	public void setExamNotice(String examNotice) {
		this.examNotice = examNotice;
	}

	public Integer getCardCount() {
		return cardCount;
	}

	public void setCardCount(Integer cardCount) {
		this.cardCount = cardCount;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public HospitalSettings getSettings() {
		return settings;
	}

	@SuppressWarnings("unchecked")
	public <T extends HospitalSettings> T inferSettings() {
		return (T) settings;
	}

	public void setSettings(HospitalSettings settings) {
		this.settings = settings;
	}

	public String getTipText() {
		return tipText;
	}

	public void setTipText(String tipText) {
		this.tipText = tipText;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public int getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(int organizationType) {
		this.organizationType = organizationType;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getDefaultManagerId() {
		return defaultManagerId;
	}

	public void setDefaultManagerId(Integer defaultManagerId) {
		this.defaultManagerId = defaultManagerId;
	}

	public int getShowInList() {
		return showInList;
	}

	public void setShowInList(int showInList) {
		this.showInList = showInList;
	}

	public Hospital(int id, String name, HospitalSettings os) {
		this.id = id;
		this.name = name;
		this.settings = os;
	}

	public Hospital(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Hospital() {
		// TODO Auto-generated constructor stub
	}

	public Hospital(int id) {
		this.id = id;
	}

	public Hospital(Integer hospitalId) {
		this.id = hospitalId;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

}
