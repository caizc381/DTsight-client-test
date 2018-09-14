package com.tijiantest.model.account;

public class UserAccount extends Account{
	public String username;
	public String password;
	public String source;
	public String url;
	public Integer userSystem;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getUserSystem() {
		return userSystem;
	}
	public void setUserSystem(Integer userSystem) {
		this.userSystem = userSystem;
	}
}
