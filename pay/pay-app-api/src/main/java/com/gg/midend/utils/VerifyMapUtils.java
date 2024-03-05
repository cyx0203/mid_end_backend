package com.gg.midend.utils;

import java.util.Map;

public class VerifyMapUtils {

	/**
	 * 校验 map 是否含所有的指定键值，非空时返回 true，否则返回 false
	 * @param req
	 * @param str
	 * @return
	 */
	public static Boolean notEmpty(Map<String, Object> req, String... str) {
		if (req == null) {
			return false;
		}
		for (String strIndex : str) {
			if (req.containsKey(strIndex) && req.get(strIndex) != null) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
}
