package com.tijiantest.base;

public enum Flag {

	CRM(1),

	MAIN(2),
	MANAGE(3),
	CHANNEL(4),
	MAIN_SECOND(5),
	OPS(6);
	
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
