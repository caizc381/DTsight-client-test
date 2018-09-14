package com.tijiantest.model.channel;

import java.util.List;

import com.tijiantest.model.account.Account;
import com.tijiantest.model.account.Function;
import com.tijiantest.model.account.Role;

public class UserLoginVo {

	private Account account;//账户信息
	private List<Role> roles;//角色
	private List<Function> functions;//权限
	private Integer channelId;//渠道商ID
	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Function> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
	public Integer getChannelId() {
		return channelId;
	}
	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}
	
	
}
