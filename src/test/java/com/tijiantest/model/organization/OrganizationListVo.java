package com.tijiantest.model.organization;

import java.util.List;
import java.util.Map;

import com.tijiantest.model.hospital.Hospital;

/**
 * 订单列表vo
 * @author admin
 *
 */
public class OrganizationListVo {

	/**
	 * 医院列表
	 */
	private List<Hospital> hospitals;
	
	/**
	 * 渠道商列表
	 */
	private List<Hospital> channels;
	
	private Map<Integer, String> channelMap;

	public List<Hospital> getHospitals() {
		return hospitals;
	}

	public void setHospitals(List<Hospital> hospitals) {
		this.hospitals = hospitals;
	}

	public List<Hospital> getChannels() {
		return channels;
	}

	public void setChannels(List<Hospital> channels) {
		this.channels = channels;
	}

	public Map<Integer, String> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<Integer, String> channelMap) {
		this.channelMap = channelMap;
	}
	
	
}
