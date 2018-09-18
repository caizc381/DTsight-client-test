package com.dtstack.base;

public enum Flag {

	DTUIC(1),
	IDE(2);
	
	private int flagnum;
	
	private Flag(int flagnum){
		this.flagnum = flagnum;
	}
	
	 @Override
	 public String toString() {
	    return String.valueOf (this.flagnum );
	 }

	    
	  public int intValue(){
	    	return this.flagnum;
	 }
}
