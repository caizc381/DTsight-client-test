package com.tijiantest.model.company;

public enum ChannelGuestCompanyEnum {

	CHANNEL_GUEST_ONLINE(4, "个人网上预约", -103), CHANNEL_GUEST_OFFLINE(5, "散客现场", -104);

	private Integer platformCompanyId;// 对应的平台单位名称，以区分两个散客单位，导出时对应医院单位是【每天健康】
	private String defaultName;// 默认名称
	private Integer tbExamCompanyId;// 自定义的老表主键，数据迁移时用

	private ChannelGuestCompanyEnum(Integer platformCompanyId, String defaultName, Integer tbExamCompanyId) {
		this.platformCompanyId = platformCompanyId;
		this.defaultName = defaultName;
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public Integer getPlatformCompanyId() {
		return platformCompanyId;
	}

	public void setPlatformCompanyId(Integer platformCompanyId) {
		this.platformCompanyId = platformCompanyId;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}
}
