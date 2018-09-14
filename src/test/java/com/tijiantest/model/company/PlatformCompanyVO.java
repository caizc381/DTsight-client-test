package com.tijiantest.model.company;

import java.util.List;

public class PlatformCompanyVO {
	private PlatformCompany platformCompany;

	/**
	 * 申请的体检中心
	 */
	private List<Integer> hospitalList;
	
	/**
	 * 选择的渠道商
	 */
	private List<Integer> channelList;
	
	/**
	 * 渠道客户经理列表
	 */
	private List<ChannelManagerVO> channelManagerList;

	public PlatformCompany getPlatformCompany() {
		return platformCompany;
	}

	public void setPlatformCompany(PlatformCompany platformCompany) {
		this.platformCompany = platformCompany;
	}

	public List<Integer> getHospitalList() {
		return hospitalList;
	}

	public void setHospitalList(List<Integer> hospitalList) {
		this.hospitalList = hospitalList;
	}

	public List<Integer> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Integer> channelList) {
		this.channelList = channelList;
	}

	public List<ChannelManagerVO> getChannelManagerList() {
		return channelManagerList;
	}

	public void setChannelManagerList(List<ChannelManagerVO> channelManagerList) {
		this.channelManagerList = channelManagerList;
	}

}
