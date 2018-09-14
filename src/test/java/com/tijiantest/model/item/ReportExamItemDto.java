package com.tijiantest.model.item;

public class ReportExamItemDto extends ReportExamItem {
	private String crmItemName;
	private String hisItemId;
	private boolean matchStatus;

	private Integer hospitalId;
	private String searchWord;

	public ReportExamItemDto(int hospitalId, boolean matchStatus, String searchWord) {
		super();
		this.hospitalId = hospitalId;
		this.matchStatus = matchStatus;
		this.searchWord = searchWord;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}

	public boolean isMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(boolean matchStatus) {
		this.matchStatus = matchStatus;
	}

	public String getCrmItemName() {
		return crmItemName;
	}

	public void setCrmItemName(String crmItemName) {
		this.crmItemName = crmItemName;
	}

	public String getHisItemId() {
		return hisItemId;
	}

	public void setHisItemId(String hisItemId) {
		this.hisItemId = hisItemId;
	}
}
