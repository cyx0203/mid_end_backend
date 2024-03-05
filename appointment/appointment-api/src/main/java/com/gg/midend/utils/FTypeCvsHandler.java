package com.gg.midend.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 描述：ForcedTypeConversionHandler，强制类型转换处理器
 * 作用：进行强制类型转换的类型检验，完善强转安全性，从而防止 unchecked cast 警告
 */
public class FTypeCvsHandler {

    /**
     * 描述：object 转 HashMap
     * @param obj 需转换类型
     * @param clazz1 HashMap Key
     * @param clazz2 HashMap Value
     * @return 安全转换后的 HashMap<K,V>
     * @param <K> 泛型
     * @param <V> 泛型
     */
    public static <K,V> HashMap<K,V> castToHashMap(Object obj, Class<K> clazz1, Class<V> clazz2) {
        HashMap<K,V> result = new HashMap<>();
        if(obj instanceof HashMap<?,?>) {
            for (Object o : ((HashMap<?, ?>) obj).keySet()) {
                result.put(clazz1.cast(o),clazz2.cast(((HashMap<?, ?>) obj).get(o)));
            }
            return result;
        }
        return null;
    }

    /**
     * 描述：object 转 ArrayList
     * @param obj 需转换类型
     * @param clazz ArrayList 元素
     * @return 安全转换后的 ArrayList<T>
     * @param <T> 泛型
     */
    public static <T> ArrayList<T> castToList(Object obj, Class<T> clazz) {
        ArrayList<T> result = new ArrayList<>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (ArrayList<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }
}
