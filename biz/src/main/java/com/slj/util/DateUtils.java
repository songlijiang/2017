/**
 * Copyright (c) 2010-2015 by Shanghai HanTao Information Co., Ltd.
 * All rights reserved.
 */
package com.slj.util;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 *
 * @author yan.liu
 *
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static final String RFC822_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	public static final FastDateFormat RFC822_FORMATTER = FastDateFormat.getInstance(RFC822_FORMAT, Locale.US);

	public static final FastDateFormat DEFAULT_DAY_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd");

	public static final FastDateFormat CHINESE_DAY_FORMATTER = FastDateFormat.getInstance("yyyy年MM月dd日");

	public static final FastDateFormat CHINESE_DAY_FORMATTER_ALL = FastDateFormat.getInstance("yyyy年MM月dd日HH:mm");


	private static final String[] CHINESE_MONTHS = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };

	public static final FastDateFormat MonthMarkFormat = FastDateFormat.getInstance("yyMM");

	public  static final long DayTime = 24*60*60*1000;

	/**
	 * get current date
	 *
	 * @return
	 */
	public static Date currentDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * get duration in second between to dates
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static long getDurationInSecond(Date from, Date to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to must not be null.");
		}
		return Math.abs(from.getTime() - to.getTime()) / MILLIS_PER_SECOND;
	}

	public static long getSecondsFromNow(Date date) {
		return getDurationInSecond(currentDate(), date);
	}

	public static Date parseUseDefaultFormat(String date) {
		return parse(date, getDayFormatter());
	}

	public static Date parse(String date, DateFormat df) {
		try {
			return df.parse(date);
		} catch (ParseException e) {
			e.getStackTrace();
			return new Date();
		}
	}

	public static List<Date> allMonthFrom(Date start, Date end) {
		List<Date> dates = new ArrayList<>();
		DateTime dateTime = new DateTime(start).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime endTime = new DateTime(end);
		while (!dateTime.isAfter(endTime)){
			dates.add(dateTime.withTimeAtStartOfDay().toDate());
			dateTime = dateTime.plusMonths(1);
		}
		return dates;
	}

	public static String format(Date date, DateFormat df) {
		if (date == null) {
			return "";
		} else if (df != null) {
			return df.format(date).toLowerCase();
		} else {
			return DEFAULT_DAY_FORMATTER.format(date);
		}
	}

	public static String getDayTime(Date start) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		return simpleDateFormat.format(start);
	}

	public static String range(Date start, Date end) {
		return getDayTime(start) + " - " + getDayTime(end);
	}

	public static String getChineseMonth(int month) {
		if (month >= 1 && month <= 12) {
			return CHINESE_MONTHS[month - 1];
		}
		throw new IllegalArgumentException("Month must between 1 and 12, but is " + month);
	}

	/**
	 * get previous month mark for the input month mark, as 1006=>1005
	 *
	 * @param monthMark
	 * @return
	 */
	public static String getPrevMonthMark(String monthMark) {
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyMM");
		Date monthDate = null;
		try {
			monthDate = monthFormat.parse(monthMark);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid month mark[" + monthMark + "].", e);
		}
		Date prevMonthDate = org.apache.commons.lang3.time.DateUtils.addMonths(monthDate, -1);
		return MonthMarkFormat.format(prevMonthDate);
	}

	/**
	 * 返回RFC822格式的日期字符串,使用系统默认Locale
	 *
	 * @param date
	 * @return
	 */
	public static String rfc822Format(Date date) {
		return FastDateFormat.getInstance(RFC822_FORMAT).format(date);
	}

	public static String rfc822Format(Date date, Locale locale) {
		return FastDateFormat.getInstance(RFC822_FORMAT, locale).format(date);
	}

	/**
	 * 得到某一天的该星期的第一日 00:00:00
	 *
	 * @param date
	 * @param firstDayOfWeek
	 *            本周一个星期的第一天为星期几
	 *
	 * @return
	 */

	public static Date getFirstDayOfWeek(Date date, int firstDayOfWeek) {
		DateTime dateTime = new DateTime(date);
		if(dateTime.getDayOfWeek() >= firstDayOfWeek){
			return dateTime.withDayOfWeek(firstDayOfWeek).withMillisOfDay(0).toDate();
		}else {
			return new Date(dateTime.withDayOfWeek(firstDayOfWeek).withMillisOfDay(0).toDate().getTime()-7*DayTime);
		}
	}


	/**
	 * 得到某一天的该星期的第一日 00:00:00
	 *
	 * @param date
	 * @param
	 *
	 *
	 * @return
	 */

	public static Date getDayStart(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.withMillisOfDay(0).toDate();
	}

	/*
	*参考与2015年1月2日  到  2015 年1月9日算第一周
		return 上周
	 */
	public static long getRefWeek(Date now){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ref = null;
		try {
			ref = simpleDateFormat.parse("2015-01-02");
		}catch (ParseException e){
			e.printStackTrace();
		}
		if(ref == null){
			return -1;
		}else {
			long duration  =getDurationInSecond(getFirstDayOfWeek(now, 5), ref);
			 return duration*1000/(DayTime*7);
		}
	}

	/*
	*参考与2015年1月1日  到  2015 年1月31日算第一月
		return 当前月
	 */
	public static int getRefMonth(Date now){
		if(now == null){
			return -1;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ref = null;
		try {
			ref = simpleDateFormat.parse("2015-01-01");
		}catch (ParseException e){
			e.printStackTrace();
		}
		if(ref == null){
			return -1;
		}else {
			return allMonthFrom(ref,now).size();
		}
	}

	public static Date getStartOfMonth(Date date){
		 return new DateTime(date).withDayOfMonth(1).withTimeAtStartOfDay().toDate();
	}

	public static Date getPreMonth(Date date ,int pre){
		return new DateTime(date).minusMonths(pre).toDate();
	}


	public static Date getDate(String simpleDate){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date ref = null;
		try {
			ref = simpleDateFormat.parse(simpleDate);
		}catch (ParseException e){
			e.printStackTrace();
		}
		return ref;
	}



	public static void main (String args[]){

	//	DateTime dateTime = new DateTime();
		System.out.println(getFirstDayOfWeek(new Date(),1));
		System.out.println(addWeeks(getFirstDayOfWeek(new Date(),1),1));

		//	System.out.println(getPreMonth(new Date(),2));
	/*	List<Date> list = allMonthFrom(ref,new Date());
		System.out.println(getFirstDayOfWeek(new Date(),5));
		//DateTime dateTime = new DateTime(new Date());
		System.out.println(dateTime.withDayOfWeek(1));*/
	}

	public static SimpleDateFormat getDayFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static SimpleDateFormat getMinuteFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	public static SimpleDateFormat getMonthFormatter() {
		return new SimpleDateFormat("yyyy-MM");
	}

	public static SimpleDateFormat getSecondFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static SimpleDateFormat getMilliSecondFormatter() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
	}

	public static SimpleDateFormat getTimeFormatter() {
		return new SimpleDateFormat("HH:mm");
	}

	public static SimpleDateFormat getNoYearDateFormatter() {
		return new SimpleDateFormat("MM-dd");
	}

	public static SimpleDateFormat getNoYearTimeFormatter() {
		return new SimpleDateFormat("MM-dd HH:mm");
	}

	public static SimpleDateFormat getYearFormatter() {
		return new SimpleDateFormat("yyyy");
	}
	public static SimpleDateFormat getSpecialFormatter() {
		return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	}

}
