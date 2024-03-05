package com.gg.midend.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringUtils {

	/**
	 * 去掉字符串中的\t\r\n符号
	 * @param str
	 * @return
	 */
	public static String replaceAllBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	// str - 原始字符串，direction - 方向 L - 左 R - 右
	public static String addEqualString(String str, String direction, int totalLength) //
	{
		int len = str.getBytes().length;
		if (len >= totalLength)
			return str;
		if (direction.equalsIgnoreCase("R")) {
			for (int i = 0; i < (totalLength - len); i++) {
				str += "0";
			}
		} else if (direction.equalsIgnoreCase("L")) {
			for (int i = 0; i < (totalLength - len); i++) {
				str = "0" + str;
			}
		}
		return str;
	}
	
}

