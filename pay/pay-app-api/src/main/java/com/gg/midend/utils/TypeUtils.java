package com.gg.midend.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换
 * 
 * @author 87392
 *
 */
public class TypeUtils {

	/**
	 * Object转Map
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> cla = obj.getClass();
		Field[] fields = cla.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String keyName = field.getName();
			Object value = field.get(obj);
			if (value == null)
				value = "";
			map.put(keyName, value);
		}
		return map;
	}


}
