package com.gg.midend.service;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: 樊亦村
 * Created time: 2022-01-13 08:00:00
 * Description: 定时生成号源排班数据类
 */
@Component
public class CreateScheduleService {

    @Autowired
    private SqlService sqlService;

    @Value("${config.scheduleDay}")
    private int scheduleDay;
    
    /**
     * 每天定时为<DAYS_LATER>天后生成号源排班
     */
    //@PostConstruct
    @Scheduled(cron = "00 00 01 * * ?")
    @Transactional(rollbackFor = {Exception.class})
    public void scheduledTask() throws ExceptionCenter {
        GlobalConfig.log_api.info("=====开始生成" + scheduleDay + "天内的号源排班=====");
        // 查询所有院区
        List hosList = sqlService.selectList("com", "selectHospital");
        for (Object hosItem : hosList) {
            Map map = (Map) hosItem;
            String hospitalId = map.get("id").toString();
            GlobalConfig.log_api.info("=====开始为院区：" + hospitalId + "生成号源排班=====");
            createScheduleForOneHospital(hospitalId);
        }
    }

    //为每家医院生成号源排班

    /**
     * 为每家医院生成号源排班
     *
     * @param hospitalId 医院编号
     * @throws ExceptionCenter 异常
     */
    private void createScheduleForOneHospital(String hospitalId) throws ExceptionCenter {
        //查询从今天起，往后已经生成的号源日期表记录(src_date)
        Map<String, String> queryCondition = new HashMap<>();
        queryCondition.put("businessType", "1");
        queryCondition.put("hospitalId", hospitalId);
        List srcDateList = sqlService.selectList("srcDate", "selectFromToday", queryCondition);
        //查询从今天起，往后N天的尚未生成号源排班的日期
        for (int i = 0; i < scheduleDay; i++) {
            //获取从今天起，i天后的日期，格式yyyyMMdd
            String tempDate = getAnyDate(i);
            boolean findFlag = false;
            for (Object item : srcDateList) {
                Map srcDateMap = (Map) item;
                if (tempDate.equals(srcDateMap.get("date"))) {
                    //tempDate这一天已经生成了号源排班，继续找下一天
                    findFlag = true;
                    break;
                }
            }
            if (!findFlag) {
                //tempDate这一天没有生成号源排班，开始生成
                createScheduleForOneDay(tempDate, hospitalId);
            }

        }
    }

    /**
     * 为一天生成号源排班
     *
     * @param date       生成号源排班的这一天
     * @param hospitalId 医院编号
     * @throws ExceptionCenter 异常
     */
    private void createScheduleForOneDay(String date, String hospitalId) throws ExceptionCenter {
        try {
            //判断date是星期几
            String dayOfWeek = getWeekByDate(date);
            //先插入src_date表，获取自增id
            GlobalConfig.log_api.info("=====开始生成" + date + "(星期" + dayOfWeek + ")的号源日期=====");
            Map<String, String> insertDateMap = new HashMap<>();
            insertDateMap.put("businessType", "1");
            insertDateMap.put("hospitalId", hospitalId);
            insertDateMap.put("date", date);
            int ret = sqlService.update("srcDate", "insert", insertDateMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("=====生成" + date + "(星期" + dayOfWeek + ")的号源日期失败=====");
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
            GlobalConfig.log_api.info("=====已生成" + date + "(星期" + dayOfWeek + ")的号源日期=====");
            //再插入src_schedule表
            GlobalConfig.log_api.info("=====开始生成" + date + "(星期" + dayOfWeek + ")的号源排班=====");
            Map<String, Object> insertScheduleMap = new HashMap<>();
            insertScheduleMap.put("srcDateId", insertDateMap.get("id"));
            insertScheduleMap.put("dayOfWeek", dayOfWeek);
            ret = sqlService.update("srcSchedule", "insertByTask", insertScheduleMap);
            GlobalConfig.log_api.info("=====生成" + date + "(星期" + dayOfWeek + ")的号源排班=" + ret + "行=====");

        } catch (Exception e) {
            GlobalConfig.log_api.info("生成号源排班失败，原因：" + e);
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    //获取从今天起，days天后的日期，格式yyyyMMdd
    private String getAnyDate(int days) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(date.getTime() + (long) (1000 * 60 * 60 * 24 * days)));
    }

    //判断某一天是星期几：1星期一 2星期二 3星期三 4星期四 5星期五 6星期六 7星期日
    private String getWeekByDate(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        dayOfWeek = dayOfWeek == 0 ? 7 : dayOfWeek;
        return Integer.toString(dayOfWeek);
    }

}
