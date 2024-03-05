package com.gg.midend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger("DateUtils");
    /**
     * 获取两个日期之间的日期
     * @param startDateStr
     * @param endDateStr
     * @return
     * @throws Exception
     */
    public static List<String> getBetweenDate(String startDateStr, String endDateStr,String sr,String pos,String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date start = sdf.parse(startDateStr);// 开始时间
        Date end = sdf.parse(endDateStr);// 结束时间
        sdf = new SimpleDateFormat("yyyyMM");
        Date startDate = sdf.parse(sdf.format(start));
        Date endDate = sdf.parse(sdf.format(end));

        Calendar dd = Calendar.getInstance();// 定义日期实例
        dd.setTime(startDate);// 设置日期起始时间
        List<String> newArrayList = new ArrayList();
        while (dd.getTime().before(endDate)) {// 判断是否到结束日期
            String str = sdf.format(dd.getTime());
            str = pos == "left" ? (sr+str) : (str+sr);
            newArrayList.add(str);// 日期结果
            dd.add(Calendar.MONTH, 1);// 进行当前日期月份加1
        }

        Date now = sdf.parse(sdf.format(new Date()));

        dd.setTime(now);
        if(dd.getTime().equals(endDate)){
            if(sr.endsWith("_"))
                newArrayList.add( sr.substring(0,sr.length()-1) );
            else
                newArrayList.add( sr );
        }else{
            newArrayList.add( pos == "left" ? (sr+sdf.format(end)) : (sdf.format(end)+sr) );
        }
        return newArrayList;
    }


    public static String getMinAfter(String time,int space) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MINUTE,space);
        Date after = now.getTime();
        return format.format(after);
    }


    public static int diff(String now,String after) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTime = format.parse(now).getTime();
        long afterTime = format.parse(after).getTime();
        long diff = (afterTime-currentTime)/1000/60;
        return Integer.parseInt(String.valueOf(diff));
    }

    public static String getMonth(int num, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, num);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

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

    public static void main(String[] args){
        System.out.println(DateUtils.getSpecifiedDayBefore(1));

    }
}
