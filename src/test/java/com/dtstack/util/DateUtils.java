package com.dtstack.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ren
 *
 */
public class DateUtils {

	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	/**
	 * 获取日期间隔天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int daysBetween(Date startDate, Date endDate) {
		final int DAY = 1000 * 60 * 60 * 24;
		return Math.round((endDate.getTime() - startDate.getTime()) / DAY);
	}

	/**
	 * 日期 => 字符串
	 * 
	 * @param format
	 * @param date
	 * @return
	 */
	public static String format(String format, Date date) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 将日期字符串解析成日期对象
	 * 
	 * @param format
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String format, String dateStr) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(dateStr);
	}

	/**
	 * 获得当前时间之后offsetMonth个月的时间
	 * 
	 * @param offsetDay
	 * @return
	 */
	public static Date offsetMonth(int offsetMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.add(Calendar.MONTH, offsetMonth);
		return calendar.getTime();
	}

	/**
	 * 获得当前时间之后offsetDay天数的时间
	 * 
	 * @param offsetDay
	 * @return
	 */
	public static Date offsetDay(int offsetDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		calendar.add(Calendar.DATE, offsetDay);

		calendar.add(Calendar.HOUR_OF_DAY, 23);
		calendar.add(Calendar.MINUTE, 59);
		calendar.add(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 去除日期的十分秒
	 * 
	 * @param date
	 * @return
	 */
	public static Date toDayStartSecond(Date date) {
		if (date == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * 将日期设置成最后一秒
	 * 
	 * @param date
	 * @return
	 */
	public static Date toDayLastSecod(Date date) {
		if (date == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param date
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 */
	public static int dayForWeek(Date date) {
		if (date == null) {
			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	public static Integer dayForMonth(Date date) {
		if (date == null) {
			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DATE);
	}

	public static Date theFirstDayOfMonth(int month) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, month);// 为0 ，则设置当前月
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		return c.getTime();
	}

	public static Date theLastDayOfMonth(int month) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	/**
	 * 一天的开始时间
	 * 
	 * @return
	 */
	public static Date getStartTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 一天的结束时间
	 * 
	 * @return
	 */
	public static Date getEndTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 时间偏移
	 * 
	 * @param offDate
	 * @return
	 */
	public static Date offDate(int offDate) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, offDate);// 把日期往后增加一天.整数往后推,负数往前移动
		return calendar.getTime();
	}

	/**
	 * 去掉最后的.0
	 * 
	 * @param date
	 * @return
	 */
	public static String parse(String date) {
		int lastIndex = date.lastIndexOf(".");
		return date.substring(0, lastIndex);
	}

	/**
	 * 获取当前时间之前或者之后的几分钟时间
	 * 
	 * @param minute
	 * @return
	 */
	public static String getTimeByMinute(int minute) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MINUTE, minute);

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

	}

	/**
	 * 获取目标时间之后offsetDay天数的时间，之前的参数则传负数
	 * 
	 * @param destDate
	 * @param offsetDay
	 * @return
	 */
	public static Date offsetDestDay(Date destDate, int offsetDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(destDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		calendar.add(Calendar.DATE, offsetDay);

		if (offsetDay > 0) {
			calendar.add(Calendar.HOUR_OF_DAY, 23);
			calendar.add(Calendar.MINUTE, 59);
			calendar.add(Calendar.SECOND, 59);
		}

		return calendar.getTime();
	}

	/**
	 * 获取GMT时间
	 * 
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public static String getGMTDateString(Object obj) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = obj.toString();
		if (s.contains("UTC")) {
			// sdf.setTimeZone(TimeZone.getTimeZone(envConf.getValue(ConfDefine.PUBLIC,ConfDefine.TIMEZONE)));
			return sdf.format(new Date(s));
		} else if (s.contains("CST")){
//			System.out.println("CST时间.."+s);
//			DateFormat df = new SimpleDateFormat("EEE, d-MMM-yyyy", Locale.ENGLISH);
//			df.setTimeZone(TimeZone.getTimeZone("GMT"));
//			System.out.println("CST时间.."+sdf.format(obj));
//			return sdf.format(df.parse(df.format(obj)));
			return sdf.format(obj);
		}
		else if (s.contains("GMT")) {
			SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
			return sdf.format(sf.parse(s));
		} else
			return sdf.format(obj);
	}
	
	public static List<Integer> daysForWeek(Date startDate,Date endDate){
		List<Integer> days = new ArrayList<>();
		Date tmpDate = startDate;
		while(tmpDate.getTime()!=endDate.getTime()){
			Integer day = dayForWeek(tmpDate);
			days.add(day);
			Calendar c = Calendar.getInstance();
			c.setTime(tmpDate);
			c.add(Calendar.DAY_OF_MONTH, 1);// +1天
			tmpDate = c.getTime();
		}
		return days;
	}
}
