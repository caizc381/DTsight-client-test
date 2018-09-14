package com.tijiantest.model.account;

public class Manag_Save {
	private int accountCompanyId;
	private int hospitalId;
	private int id;
	private String identity;
	private boolean  importWithoutIdcard;
	private boolean  isSitePay;
	private boolean  keepLogin;
	private String mobile;
	private String name;
	private boolean  orderImmediately;
	private boolean  removeAllItems;
	private int roleId;
	private String username;
	public Manag_Save(int accountCompanyId,int hospitalId,int id,String identity,boolean  importWithoutIdcard,
			boolean  isSitePay,boolean  keepLogin,String mobile,String name,boolean  orderImmediately,
			boolean  removeAllItems,int roleId,String username){
		this.accountCompanyId=accountCompanyId;
		this.hospitalId=hospitalId;
		this.id=id;
		this.identity=identity;
		this.importWithoutIdcard=importWithoutIdcard;
		this.isSitePay=isSitePay;
		this.keepLogin=keepLogin;
		this.mobile=mobile;
		this.name=name;
		this.orderImmediately=orderImmediately;
		this.removeAllItems=removeAllItems;
		this.roleId=roleId;
		this.username=username;
		
	}
	public int getAccountCompanyId() {
		return accountCompanyId;
	}
	public void setAccountCompanyId(int accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}
	public int getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public boolean isImportWithoutIdcard() {
		return importWithoutIdcard;
	}
	public void setImportWithoutIdcard(boolean importWithoutIdcard) {
		this.importWithoutIdcard = importWithoutIdcard;
	}
	public boolean isSitePay() {
		return isSitePay;
	}
	public void setSitePay(boolean isSitePay) {
		this.isSitePay = isSitePay;
	}
	public boolean isKeepLogin() {
		return keepLogin;
	}
	public void setKeepLogin(boolean keepLogin) {
		this.keepLogin = keepLogin;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isOrderImmediately() {
		return orderImmediately;
	}
	public void setOrderImmediately(boolean orderImmediately) {
		this.orderImmediately = orderImmediately;
	}
	public boolean isRemoveAllItems() {
		return removeAllItems;
	}
	public void setRemoveAllItems(boolean removeAllItems) {
		this.removeAllItems = removeAllItems;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
