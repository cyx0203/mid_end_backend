package com.gg.midend.utils;

import java.util.ArrayList;
import java.util.Map;

public class MapUtil {
    public static String getMapParamValue(Map<String, Object> paramMap, String listKey, String defVal) {
        if (paramMap.get(listKey) != null) {
            return (String) paramMap.get(listKey);
        } else {
            return defVal;
        }
    }

    /**
     * 描述：用于检验入参必输字段（之所以重写是因为字段值可能出现 int 情况，getMapParamValue 方法存在强转问题）
     * @param needParams 必输字段
     * @param requestMap 入参
     * @throws Exception 抛出异常
     */
    public static void checkParams(String[] needParams, Map<String, Object> requestMap) throws Exception {
        ArrayList<String> lostParams = new ArrayList<>();
        for (String temp : needParams) {
            // 只需要字段存在且不为 null 即可，不需要判断是否为空字符串
            if (!(requestMap.containsKey(temp) && (requestMap.get(temp) != null))) {
                lostParams.add(temp);
            }
        }
        if (lostParams.size() != 0) {
            throw new Exception("缺少字段: " + lostParams);
        }
    }
}
