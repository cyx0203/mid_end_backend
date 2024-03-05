package com.gg.midend.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author txy
 * @create 2023-02-01-8:37
 */
public class MyUtils {

    /**
     * 组前端charts，api要求的数据格式
     * @param countNum 某个选项的结果统计值
     * @param optionName 选项名称
     * @param countPers 参评人次
     * @return
     */
    public static Map getOptionMapForCharts(Integer countNum, String optionName, Integer countPers) {
        Map optionMap = new HashMap<>();
//        String optionName = (String) reqList.get(index);
        Double num = (countNum + 0.0) / countPers * 100;
        optionMap.put("type", optionName);
        optionMap.put("value", num.intValue());
        return optionMap;
    }

    /**
     * @param countNum 某个选项的结果统计值
     * @param reqList 选项集合
     * @param countPers 参评人次
     * @param index 索引，用于根据选项结果A、B、C...索引出真正的选项值
     * @return
     */
    public static Map getOptionMapForTable(Integer countNum, List reqList, Integer countPers, Integer index) {
        Map optionMap = new HashMap<>();
        String optionName = (String) reqList.get(index);
        String keyName = "";
        switch (optionName){
            case "满意":
                keyName = "option1";
                break;
            case "基本满意":
                keyName = "option2";
                break;
            case "不满意":
                keyName = "option3";
                break;
        }
//        Double num = (countNum + 0.0) / countPers * 100;
        optionMap.put(keyName, countNum);
        return optionMap;
    }


    /**
     * @param countNum 某个选项的结果统计值
     * @param optionName 选项名称
     * @return
     */
    public static Map getOptionMap(Integer countNum, String optionName) {
        Map optionMap = new HashMap<>();
//        String optionName = (String) reqList.get(index);
        String keyName = "other";
        switch (optionName){
            case "满意":
                keyName = "option1";
                break;
            case "基本满意":
                keyName = "option2";
                break;
            case "不满意":
                keyName = "option3";
                break;
        }
        optionMap.put(keyName, countNum);
        return optionMap;
    }

    /**
     * 统计list中，相同元素的个数
     * @param list
     * @return
     */
    public static Map getOptionMapForMultilple(List<String> list){
        Map<String,Integer> map = new HashMap<>();
        for(String str:list){
            Integer i = 1;           //定义一个计数器，用来记录重复数据的个数
            if(map.get(str) != null){
                i = map.get(str)+1;
            }
            if(map.get(str) == null){
                i = 1;
            }
            map.put(str,i);
        }
        return map;
    }

    /**
     * Interge list 求平均数
     * @param list
     * @return
     */
    public static int intArrage(List list) {
        int result = 0;
        for(int i = 0;i < list.size(); i++) {
            result += ((int)list.get(i));
        }
        return result;
    }
}
