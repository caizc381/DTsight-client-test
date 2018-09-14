package com.tijiantest.model.company;

/**
 * 医院初始化创建的3个散客单位
 * 
 * @author admin
 *
 */
public enum HospitalGuestCompanyEnum {
	HOSPITAL_GUEST_ONLINE(1, "个人网上预约", -100), HOSPITAL_GUEST_OFFLINE(2, "现场散客", -101), HOSPITAL_MTJK(3, "每天健康", -102);

	private Integer platformCompanyId;// 对应的平台单位名称
	private String defaultName;// 默认名称
	private Integer tbExamCompanyId;// 自定的老表主键，数据迁移时用

	private HospitalGuestCompanyEnum(Integer platformCompanyId, String defaultName, Integer tbExamCompanyId) {
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
