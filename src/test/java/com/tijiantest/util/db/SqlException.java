package com.tijiantest.util.db;

public class SqlException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SqlException(String message){
		super(message);
	}
	
	public SqlException(String message, Throwable cause){
		super(message, cause);
	}
}
