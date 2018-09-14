package com.tijiantest.model.resource;

import java.io.Serializable;

/**
 * province city district will save in one table, the others saved in hospital
 * table
 * 
 * @author twu
 *
 */
public class Address implements Serializable {
	
	private static final long serialVersionUID = 4343281961591189903L;
	
	private Integer id;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;
	/**
	 * 区
	 */
	private String district;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 地图信息,纬度
	 */
	private String latitude;
	/**
	 * 地图信息,经度
	 */
	private String longitude;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getFullAddress() {
		StringBuilder sb = new StringBuilder();
		if (this.province != null) {
			sb.append(this.province);
		}
		if (this.city != null && (this.province != null && !this.city.equals(this.province))) {
			sb.append(this.city);
		}
		if (this.district != null) {
			sb.append(this.district);
		}
		if (this.address != null) {
			sb.append(this.address);
		}
		return sb.toString();
	}

}
