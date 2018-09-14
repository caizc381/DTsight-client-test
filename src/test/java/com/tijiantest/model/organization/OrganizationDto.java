package com.tijiantest.model.organization;

import com.tijiantest.model.resource.Address;

public class OrganizationDto {

	private Integer id;
	
	private String name;
	
	private String type;
	
	private int organizationType;
	
	private String phone;
	
	private String groupExamTel;
	
	private String serviceTel;
	
	private String technicalTel;
	
	private Address address;
	
	private String addressDetail;
	
	private String longitude;
	
	private String latitude;
	
	private String url;
	
	private String keywords;
	
	private String briefIntro;
	
	private String detailIntro;
	
	private String examNotice;
	
	private Integer siteId;
	
	private Integer defaultManageId;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(int organizationType) {
		this.organizationType = organizationType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGroupExamTel() {
		return groupExamTel;
	}

	public void setGroupExamTel(String groupExamTel) {
		this.groupExamTel = groupExamTel;
	}

	public String getServiceTel() {
		return serviceTel;
	}

	public void setServiceTel(String serviceTel) {
		this.serviceTel = serviceTel;
	}

	public String getTechnicalTel() {
		return technicalTel;
	}

	public void setTechnicalTel(String technicalTel) {
		this.technicalTel = technicalTel;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getBriefIntro() {
		return briefIntro;
	}

	public void setBriefIntro(String briefIntro) {
		this.briefIntro = briefIntro;
	}

	public String getDetailIntro() {
		return detailIntro;
	}

	public void setDetailIntro(String detailIntro) {
		this.detailIntro = detailIntro;
	}

	public String getExamNotice() {
		return examNotice;
	}

	public void setExamNotice(String examNotice) {
		this.examNotice = examNotice;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getDefaultManageId() {
		return defaultManageId;
	}

	public void setDefaultManageId(Integer defaultManageId) {
		this.defaultManageId = defaultManageId;
	}
	
	
}
