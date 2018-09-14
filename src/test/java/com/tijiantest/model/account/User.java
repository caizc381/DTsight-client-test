package com.tijiantest.model.account;

import java.io.Serializable;

public class User implements Serializable{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = -876326801868756543L;
	private int id ;
	private int account_id;
	private String name;
	private String username;
	private String password;
	private String source;
	private String url;
	private Integer system;
	
	public User() {
	}
	
	public User(String username){
		this.username = username;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSystem() {
		return system;
	}

	public void setSystem(Integer system) {
		this.system = system;
	}
	
}