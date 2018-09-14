package com.tijiantest.util.exception;

/**
 *
 * @author linzhihao
 */
public enum LogLevel {
	ignore(0),
	info(1), 
	warn(2), 
	error(3);
	
	int value;
	LogLevel(int level) {
		value = level;
	}
}
