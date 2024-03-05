package com.gg.midend.utils;

import java.util.Map;

public class MapUtil {
    public static String getMapParamValue(Map paramMap, String listKey, String defVal) {
        if (paramMap.get(listKey) != null) {
            return (String) paramMap.get(listKey);
        } else {
            return defVal;
        }
    }
}
