package com.gg.midend.utils;

import java.util.*;

import cn.hutool.core.convert.Convert;

/**
 *  Author: 顾泉水
 *  Created time: 2022-01-13 08:00:00 
 *  Description: 生成号源明细规则
 */
public class RuleSourceHandle {

    /**
     * 为一个号源数量规则生成明细号源
     *
     * @param seasonList 时令
     * @param paramMap  请求参数
     * @return List<Map<String, Object>> 明细List
     */
    public static List<Map<String, Object>> createRuleSourceAdvance(List<?> seasonList, Map<String, Object> paramMap) {
        // 组装返回明细
        List<Map<String, Object>> detailList = new ArrayList<>();
 
        if ("0".equals(paramMap.get("isGivenTime").toString())) {//时令规则
            // 组装时令明细
            List<Map<String, Object>> sList = new ArrayList<>();
            for (Object o : seasonList) {
                Map<String, Object> map = Convert.toMap(String.class, Object.class, o); //获取时令规则

                if ("4".equals(paramMap.get("noon").toString())) { //早间
                    map.put("stime", "0000"); //开始时间0点
                    map.put("etime", map.get("astime")); //时令开始时间为结束时间
                } else if ("1".equals(paramMap.get("noon").toString())) { //上午
                    map.put("stime", map.get("astime")); //开始时间
                    map.put("etime", map.get("aetime")); //结束时间
                } else if ("2".equals(paramMap.get("noon").toString())) { //下午
                    map.put("stime", map.get("pstime")); //开始时间
                    map.put("etime", map.get("petime")); //结束时间
                } else if ("3".equals(paramMap.get("noon").toString())) { //午间
                    map.put("stime", map.get("aetime")); //开始时间
                    map.put("etime", map.get("pstime")); //结束时间
                } else if ("5".equals(paramMap.get("noon").toString())) { //午间
                    map.put("stime", map.get("petime")); //开始时间
                    map.put("etime", "2359"); //结束时间
                }
                sList.add(map);
            }
            if ("0".equals(paramMap.get("timeType").toString())) {// 大时段
                for (Map<String, Object> s :sList) {
                    s.put("ruleDetailId", paramMap.get("id"));
                    s.put("ruleSourceId", paramMap.get("ruleSourceId"));
                    s.put("sourceNum", paramMap.get("sourceNum"));
                    detailList.add(s);
                }
            } else {// 半个小时号源数
                for (Map<String, Object> s :sList) {
                    List<Map<String, Object>> list = createAdvance(s.get("stime").toString(),
                            s.get("etime").toString(), paramMap.get("sourceNum").toString());
                    for (Map<String, Object> m : list) {
                        m.put("ruleDetailId", paramMap.get("id"));
                        m.put("ruleSourceId", paramMap.get("ruleSourceId"));
                        m.put("pid", s.get("pid"));// 时令
                        detailList.add(m);
                    }
                }
            }
        } else {// 指定时间规则
            if ("0".equals(paramMap.get("timeType").toString())) {// 大时段
                for (int i = 0; i < 3; i++) {
                    Map<String, Object> m = new HashMap<>();// 全时令
                    m.put("ruleDetailId", paramMap.get("id"));
                    m.put("ruleSourceId", paramMap.get("ruleSourceId"));
                    m.put("pid", String.valueOf(i));// 时令
                    m.put("stime", paramMap.get("stime"));
                    m.put("etime", paramMap.get("etime"));
                    m.put("sourceNum", paramMap.get("sourceNum"));
                    detailList.add(m);
                }
            } else {// 半个小时号源数
                for (int i = 0; i < 3; i++) {
                    List<Map<String, Object>> list = createAdvance(paramMap.get("stime").toString(),
                            paramMap.get("etime").toString(), paramMap.get("sourceNum").toString());
                    for (Map<String, Object> m : list) {
                        m.put("ruleDetailId", paramMap.get("id"));
                        m.put("ruleSourceId", paramMap.get("ruleSourceId"));
                        m.put("pid", String.valueOf(i));// 时令
                        detailList.add(m);
                    }
                }
            }
        }

        return detailList;
    }

    // 生成间隔数据
    public static List<Map<String, Object>> createAdvance(String stime, String etime, String sourceNum) {
        // 组装返回明细
        List<Map<String, Object>> detailList = new ArrayList<>();

        int interval = 30; // 分时段间隔时长（分）
        int startTimeInt = timeFormat(stime);
        int endTimeInt = timeFormat(etime);
        int sumNum = (endTimeInt - startTimeInt) / interval; // 总计时间段数量
        int SOURCE_NUM = Integer.parseInt(sourceNum); // 号源数量

        for (int n = 0; n < sumNum; n++) {
            for (int l = 0; l < SOURCE_NUM; l++) {
                Map<String, Object> iMap = new HashMap<>();
                iMap.put("stime", formatTime(startTimeInt + n * interval));
                iMap.put("etime", formatTime(startTimeInt + (n + 1) * interval));
                iMap.put("sourceNum", sourceNum);
                detailList.add(iMap);
            }
        }
        return detailList;
    }

    private static int timeFormat(String time) {
        return Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(2, 4));
    }

    private static String formatTime(int time) {
        int hour = time / 60;
        String hourStr = hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour);
        int minite = time % 60;
        String miniteStr = minite < 10 ? "0" + Integer.toString(minite) : Integer.toString(minite);
        return hourStr + miniteStr;
    }
}
