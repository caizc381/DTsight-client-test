package com.tijiantest.model.account;


public class ManagerVo {

	/**
	 * accountid
	 */
	private Integer id;

	/**
	 * 用于登录
	 */
	private String username;
	/**
	 * 客户经理姓名
	 */
	private String name;

	private String mobile;

	/**
	 * 挂帐单位
	 */
	private Integer accountCompanyId;

	private Boolean isSitePay;

	private Boolean importWithoutIdcard;

	/**
	 * 专属网址
	 */
	private String identity;

	private Integer hospitalId;

	/**
	 * 搜索词
	 */
	private String searchWord;

	private Integer roleId;

	/**
	 * 直接预约
	 */
	private Boolean orderImmediately;

	/**
	 * 用户登录后，长期保持登录状态
	 */
	private Boolean keepLogin;

	/**
	 * 是否可以移出所有项目 包括必选项
	 */
	private Boolean removeAllItems;

	/**
	 * 散客代预约
	 */
	private Boolean agentReserve;

	/**
	 * C端客户与CRM客户经理关联关系
	 */
	private AccountCustomerManagerRelationVO relation;

	public Boolean getAgentReserve() {
		return agentReserve;
	}

	public void setAgentReserve(Boolean agentReserve) {
		this.agentReserve = agentReserve;
	}

	public Boolean getRemoveAllItems() {
		return removeAllItems;
	}

	public void setRemoveAllItems(Boolean removeAllItems) {
		this.removeAllItems = removeAllItems;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getAccountCompanyId() {
		return accountCompanyId;
	}

	public void setAccountCompanyId(Integer accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public Boolean getIsSitePay() {
		return isSitePay;
	}

	public Boolean getImportWithoutIdcard() {
		return importWithoutIdcard;
	}

	public void setImportWithoutIdcard(Boolean importWithoutIdcard) {
		this.importWithoutIdcard = importWithoutIdcard;
	}

	public void setIsSitePay(Boolean isSitePay) {
		this.isSitePay = isSitePay;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Boolean getOrderImmediately() {
		return orderImmediately;
	}

	public void setOrderImmediately(Boolean orderImmediately) {
		this.orderImmediately = orderImmediately;

	}

	public Boolean getKeepLogin() {
		return keepLogin;
	}

	public void setKeepLogin(Boolean keepLogin) {
		this.keepLogin = keepLogin;
	}

	public AccountCustomerManagerRelationVO getRelation() {
		return relation;
	}

	public void setRelation(AccountCustomerManagerRelationVO relation) {
		this.relation = relation;
	}





}

