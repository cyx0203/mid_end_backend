package com.gg.midend.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

	/**
	 * 格式化时间
	 * 
	 * @param format
	 * @return
	 */
	public static String getDateTime(String format) {
		Date now = new Date();
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(now);
	}

	/**
	 * 格式化前一天
	 * 
	 * @param format
	 * @return
	 */
	public static String getBeforeDateTime(String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); // 得到前一天
		Date date = calendar.getTime();
		SimpleDateFormat sd = new SimpleDateFormat(format);
		return sd.format(date);
	}

	/**
	 * 改变时间字符串的格式
	 * 
	 * @param strTime
	 * @param srcformat
	 * @param desformat
	 * @return
	 */
	public static String formatStringTime(String strTime, String srcformat, String desformat) {
		String now = "";
		try {
			Date date = new SimpleDateFormat(srcformat).parse(strTime);
			now = new SimpleDateFormat(desformat).format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 时间前后比较
	 * 
	 * @param DATE1
	 * @param DATE2
	 * @param dateType
	 * @return
	 */
	public static int compareDate(String DATE1, String DATE2, String dateType) {
		DateFormat df = null;
		if (dateType.equals("dateTime")) {// dateType:dateTime
			df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		} else {// dateType:date
			df = new SimpleDateFormat("yyyy-MM-dd");
		}
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("DATE1在DATE2前");
				return 1;
			} else if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("DATE1在DATE2后");
				return -1;
			} else {
				// System.out.println("时间相同");
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.println(getDateTime("yyyy"));
		// System.out.println(getDateTime("yyyyMMdd"));
		// System.out.println(getDateTime("HHmmss"));
		// System.out.println(getDateTime("yyyy-MM-dd"));
		// System.out.println(getDateTime("yyyyMMddHHmmss"));
		// System.out.println(getDateTime("yyyy-MM-dd HH:mm:ss"));
		// System.out.println(formatStringTime("2019-07-18","yyyy-MM-dd","yyyyMMdd"));
		// System.out.println(formatStringTime("20190718","yyyyMMdd","yyyy-MM-dd"));
		// System.out.println(Long.toString(System.currentTimeMillis()/1000L));
		// System.out.println(getDateTime("yyyy_MM_dd"));
		// System.out.println(getDateTime("yyyyMMddHHmmssSSS"));
	}
}
