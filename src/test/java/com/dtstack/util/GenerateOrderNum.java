package com.dtstack.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class GenerateOrderNum {
	private static final AtomicLong account = new AtomicLong();
	private static final String dtMilliseconds = "yyyyMMddHHmmssSSS";
	
	/**
	 * 返回系统当前时间（精确到毫秒）,作为一个唯一的订单编号
	 * @return
	 * 以yyyyMMddHHmmss为格式的当前系统时间
	 */
	public static String getOrderNum() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dtMilliseconds);
		if (account.get()==9999) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
			date = new Date();
			account.set(0);
		}
		return String.format("%s%04d%s", df.format(date),account.incrementAndGet(),MsgValidateCode.getRandomNum(2));
	}
	
	public static String  getEntryCardnum(int i) {
		return String.format("%07d", i);
				
	}
}
