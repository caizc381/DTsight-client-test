package com.tijiantest.util;


public class ExcelMember{  
	   private String orderNum;
	     
	   private String str;
	   private String hisItemStrs;
	     
	   private String action;
	  
	   public ExcelMember(String orderNum,String str,String hisItemStrs,String action) {  
	    super();  
	    this.orderNum = orderNum;
	    this.hisItemStrs = hisItemStrs;
	    this.action = action;
	    this.str = str;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getHisItemStrs() {
		return hisItemStrs;
	}

	public void setHisItemStrs(String hisItemStrs) {
		this.hisItemStrs = hisItemStrs;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}  
	      
	   
	}  