//package com.gg.midend.service;
//
//import com.gg.core.service.SqlService;
//import com.gg.midend.config.GlobalConfig;
//import com.gg.midend.utils.DateUtils;
//import net.javacrumbs.shedlock.core.SchedulerLock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@Component
//public class ScheduledService {
//    @Autowired
//    private SqlService sqlService;
//
//    @Value("${printdata.saveDate}")
//    private int saveDate;
//
//
//    /**
//     * TODO
//     *
//     * @description:每日1点10分建立当月的历史表
//     * @author: lufm
//     * @date: 2023/4/3 10:44
//     * @param: []
//     * @return: void
//     **/
//    @Scheduled(cron = "00 10 01 * * ? ")
//    @SchedulerLock(name = "createTask", lockAtMostFor = 60000, lockAtLeastFor = 60000)
//    public void createTrdTable() {
//        GlobalConfig.log_api.info("=========1点10分建立当月的历史表============");
//
//        Map map = new HashMap();
//        map.put("tableName", "trd_record_" + DateUtils.getMonth(0, "yyyyMM"));
//        GlobalConfig.log_api.info("table:"+DateUtils.getMonth(0, "yyyyMM"));
//        sqlService.update("trdRecord", "createTransTable", map);
//    }
//
//    /**
//     * TODO
//     *
//     * @description:当日数据统计、备份
//     * @author: lufm
//     * @date: 2023/4/3 10:49
//     * @param: []
//     * @return: void
//     **/
//    @Scheduled(cron = "00 30 01 * * ? ")
////    @PostConstruct
//    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
//    @SchedulerLock(name = "statTask", lockAtMostFor = 60000, lockAtLeastFor = 60000)
//    public void scheduled() {
//        GlobalConfig.log_api.info("=========1点30分统计定时任务开始============");
//        String lastDate = DateUtils.getSpecifiedDayBefore(1);
//        Map dateMap = new HashMap<>();
//        dateMap.put("lastdate", lastDate);
//        List<Map> dateMapList = sqlService.selectList("trdRecord", "selectDate", dateMap);//查询到的所有天数
//
//        List<String> monthList = new ArrayList<>();//需要备份数据的月份list
//        if (dateMapList.size() > 0) {
//            GlobalConfig.log_api.info("=========开始统计历史数据============");
//            for (Map eachDate : dateMapList) {
//                //取月份集合
//                GlobalConfig.log_api.info("trdDate："+ eachDate.get("trdDate").toString());
//                String month = eachDate.get("trdDate").toString().substring(0, 6);
//                if (!monthList.contains(month)) {
//                    monthList.add(month);
//                }
//                //统计查询
//                Map conditionMap = new HashMap<>();
//                conditionMap.put("enddate", eachDate.get("trdDate"));
//                conditionMap.put("startdate", eachDate.get("trdDate"));
//                List<Map> statisticsByKeys = sqlService.selectList("trdRecord", "statisticsByKeys", conditionMap);//所有医院
//                if (statisticsByKeys.size() > 0) {
//                    Map map = new HashMap();
//                    map.put("list", statisticsByKeys);
//                    sqlService.update("trdStat", "insertSta", map);
//                }
//            }
//        }
//        GlobalConfig.log_api.info("=========开始copy历史数据============");
//        Map histransMap = new HashMap();
//        histransMap.put("enddate", lastDate);
//
//        if (monthList.size() > 0) {
//            for (String month : monthList) {
//                String enddate = month + "99";
//                String startdate = month + "00";
//                histransMap.put("startdate", startdate);
//                if (Integer.valueOf(enddate) > Integer.valueOf(lastDate)) {
//                    histransMap.put("enddate", lastDate);
//                } else {
//                    histransMap.put("enddate", enddate);
//                }
//                histransMap.put("tableName", "trd_record_" + month);
//                sqlService.update("trdRecord", "insertBakHisTrans", histransMap);
//            }
//        }
//        GlobalConfig.log_api.info("=========数据copy完成============");
//
//        GlobalConfig.log_api.info("=========开始清理凭条数据============");
//        String bdays = DateUtils.getSpecifiedDayBefore(saveDate);
//        sqlService.update("printdata", "deletePrintDatas", "enddate", bdays);
//        GlobalConfig.log_api.info("=========清理凭条数据结束============");
//    }
//
//    /**
//     * TODO
//     *
//     * @description:根据设备工作时间，将非工作时间段的设备状态改为2，例行停机，每10分钟执行
//     * @author: lufm
//     * @date: 2023/4/3 11:23
//     * @param: []
//     * @return: void
//     **/
//    @Scheduled(fixedDelay =  10 * 60 * 1000)
//    @SchedulerLock(name = "devTask", lockAtMostFor = 60000, lockAtLeastFor = 60000)
//    public void scheduled1() {
//        List<Map> defaultWorkDevs = null;//默认启用设备列表
////        List<Map> monitorHospList = sqlService.selectList("merhospital", "getDevStatusCtr");//查询所有需要监控的医院
////        if (monitorHospList.isEmpty()) {//如果没有需要监控的医院
////            return;
////        }
////        StringBuilder hospStr = new StringBuilder();
////        for (Map hospMap : monitorHospList) {
////            if (hospStr.length() == 0) {
////                hospStr.append("('").append(hospMap.get("hospitalId")).append("'");
////            } else {
////                hospStr.append(",'").append(hospMap.get("hospitalId")).append("'");
////            }
////        }
////        hospStr.append(")");
////        GlobalConfig.log_api.info("=========需要监控的医院:" + hospStr);
//        GlobalConfig.log_api.info("=========开始检测工作时间段============");
//
////        Map queryWorkDevMap = new HashMap();
////        queryWorkDevMap.put("hosplist", hospStr);
//        List<Map> workDevs = sqlService.selectList("dev", "selectWorkDev");
//        defaultWorkDevs = workDevs;
//
//        Date now = new Date();
//        DateFormat d = new SimpleDateFormat("HHmm");
//        String nowTime = d.format(now);
//        int nowTimetoInt = Integer.parseInt(nowTime);
//        List stopDevs = new ArrayList<>();
//        for (Map devMap : defaultWorkDevs) {
//            Map stopDevMap = new HashMap();
//            if (Integer.parseInt((String) devMap.get("work1")) > nowTimetoInt
//                    || Integer.parseInt((String) devMap.get("work2")) < nowTimetoInt) {//不在工作时间区间内
//                stopDevMap.put("value", "2");
//                stopDevMap.put("stateId", "2001");
//                stopDevMap.put("id", devMap.get("id"));
//                stopDevMap.put("hospitalId", devMap.get("hospitalId"));
//                stopDevs.add(stopDevMap);
//            }
//        }
//        if (stopDevs.size() > 0) {
//            GlobalConfig.log_api.info("=========例行停机设备：============" + stopDevs);
//            int i = sqlService.update("devState", "updateStopDevStates", "list",stopDevs);
//        }
//    }
//
//
//    /**
//     * TODO
//     *
//     * @description:每12分钟查询一次，设备总状态更新时间，如果和上一次设备上送时间相差时间超过15分钟则总状态改为通讯故障
//     * @author: lufm
//     * @date: 2023/4/3 13:25
//     * @param: []
//     * @return: void
//     **/
//    @Scheduled(fixedDelay = 12 * 60 * 1000)
//    @SchedulerLock(name = "desTask", lockAtMostFor = 60000, lockAtLeastFor = 60000)
//    public void scheduled2() throws ParseException {
//        List<Map> defaultWorkDevs = null;//默认启用设备列表
//        List<Map> workDevs = sqlService.selectList("dev", "selectWorkDev");
//        defaultWorkDevs = workDevs;
////        List<Map> monitorHospList = sqlService.selectList("merhospital", "getDevStatusCtr");//查询所有需要监控的医院
////
////        if (monitorHospList.isEmpty()) {//如果没有需要监控的医院
////            return;
////        }
////        StringBuilder hospStr = new StringBuilder();
////        for (Map hospMap : monitorHospList) {
////            if (hospStr.length() == 0) {
////                hospStr.append("('").append(hospMap.get("hospitalId")).append("'");
////            } else {
////                hospStr.append(",'").append(hospMap.get("hospitalId")).append("'");
////            }
////        }
////        hospStr.append(")");
//        GlobalConfig.log_api.info("=========开始查询通信时间============");
//
////        Map queryWorkDevMap = new HashMap();
////        queryWorkDevMap.put("hosplist", hospStr);
//        List<Map> workDevstates = sqlService.selectList("devState", "selectWorkDevs");
//        if (workDevstates != null && workDevstates.size() > 0) {
//            Date now = new Date();
//            DateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
//            String nowTime = d.format(now);
////    		int nowTimetoInt = Integer.parseInt(nowTime);
//            //当前时间用来计算与上次通信时间间隔
//            Long nowTimetoL = d.parse(nowTime).getTime();
//            List failuerDevs = new ArrayList<>();
//            //当前时间用来判断是否字啊工作时间段内
//            DateFormat workd = new SimpleDateFormat("HHmm");
//            String nowTimeHM = workd.format(now);
//            int nowTimetoInt = Integer.parseInt(nowTimeHM);
//            //循环所有非通信故障设备
//            for (Map eachDev : workDevstates) {
//                String lastTime = (String) eachDev.get("dtime");
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//                Long lastTimetoL = sdf.parse(lastTime).getTime();
//                long betweenDays = (long) ((nowTimetoL - lastTimetoL) / (1000 * 60));
//                if (betweenDays > 1) {
//                    //循环所有启用设备
//                    if (defaultWorkDevs != null) {
//                        wordev:
//                        for (Map devMap : defaultWorkDevs) {
//                            if (Integer.parseInt((String) devMap.get("work1")) < nowTimetoInt
//                                    && Integer.parseInt((String) devMap.get("work2")) > nowTimetoInt) {//在工作时间区间内
//                                if (eachDev.get("devId").equals(devMap.get("id"))) {
//                                    Map failurePostDev = new HashMap();
//                                    failurePostDev.put("id", eachDev.get("devId"));
//                                    failurePostDev.put("value", "6");
//                                    failurePostDev.put("hospitalId", eachDev.get("hospitalId"));
//                                    failuerDevs.add(failurePostDev);
//                                    break wordev;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            if (failuerDevs.size() > 0) {
//                GlobalConfig.log_api.info("=========通讯故障设备：============" + failuerDevs);
//                int i = sqlService.update("devState", "updateFailurePostDevStates", "list",failuerDevs);
//            }
//        }
//    }
//}
