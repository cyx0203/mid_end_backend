package com.gg.midend.utils;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
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

    public static String foramtLocaldate(String date) {
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
    }

    public static String parseDate(String date, String pattern){
        String format = date;
        try{
            Date d = DateUtil.parseDate(date);
            format = DateUtil.format(d, pattern);
        }catch (Exception e){

        }
        return format;

    }
}
