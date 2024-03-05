package com.gg.midend.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 计算前一天
     */
    public static String getSpecifiedDayBefore(int lastDays) {
        // SimpleDateFormat simpleDateFormat = new
        // SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - lastDays);
        String dayBefore = new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        return dayBefore;
    }

    public static String getMonth(int num, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, num);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowDate() {
        return getNowDate("yyyyMMdd");
    }

    /**
     * 获取指定格式时间
     *
     * @param pattern
     * @return
     */
    public static String getNowDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
}
