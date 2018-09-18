package com.dtstack.util.exception;


public interface ExceptionWithCode {
	/**
	 * @return error code
	 */
	int getCode();

	/**
	 * 根据返回值记录日志级别
	 */
	default LogLevel loglevel() {
		return LogLevel.error;
	}
}
