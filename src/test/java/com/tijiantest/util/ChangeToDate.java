package com.tijiantest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangeToDate {
	
	public static Date formatDate(String time){
		SimpleDateFormat sdf =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public static Date fromISODate(String time){
		if(!time.matches("//d{4}-//d{2}-//d{2}T//d{2}://d{2}://d{2}.//d{3}Z")){
			return null;
		}
		time=time.replaceFirst("T", " ").replaceFirst(".//d{3}Z", "");
		Date date=formatDate(time);
		// 1、取得本地时间：
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
		return cal.getTime();
	}
    
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Date dateUtc=fromISODate("2014-02-28T05:01:38.904Z");
		System.out.println(dateUtc);
    }

}