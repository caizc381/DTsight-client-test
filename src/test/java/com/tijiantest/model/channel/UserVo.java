package com.tijiantest.model.channel;
/**
 * 用户登录
 * 包含用户名，密码，验证码
 * @author Administrator
 *
 */
public class UserVo {
	private String userName;
	private String password;
	private String validationCode;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getValidationCode() {
		return validationCode;
	}
	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}
	
	
}
