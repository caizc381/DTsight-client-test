package com.tijiantest.model.account;

public class Manag_List {
	private int hospitalId;
	private String searchWord;
	public Manag_List(int hospitalId,String searchWord){
		this.hospitalId=hospitalId;
		this.searchWord=searchWord;
	}
	public int getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getSearchWord() {
		return searchWord;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
}
