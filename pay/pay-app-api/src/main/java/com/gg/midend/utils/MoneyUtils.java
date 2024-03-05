package com.gg.midend.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额工具类
 * 
 * @author 87392
 *
 */
public class MoneyUtils {

	public static DecimalFormat dmFormat = new DecimalFormat("##0.00000000000000000000");

	/**
	 * 金額相加
	 * 
	 * @param value
	 *            原值
	 * @param augend
	 *            被加数
	 * @return
	 */
	public static BigDecimal moneyAdd(BigDecimal value, BigDecimal augend) {
		return value.add(augend);
	}

	/**
	 * 金额相减
	 *
	 * @param value
	 *            基础值
	 * @param subtrahend
	 *            减数
	 * @return BigDecimal
	 */
	public static BigDecimal moneySub(BigDecimal value, BigDecimal subtrahend) {
		return value.subtract(subtrahend);
	}

	/**
	 * 值比较大小
	 * 
	 * @param valueStr
	 * @param compValueStr
	 * @return
	 */
	public static boolean moneyComp(BigDecimal valueStr, BigDecimal compValueStr) {
		// 0:等于 >0:大于 <0:小于
		int result = valueStr.compareTo(compValueStr);
		if (result >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 金额相加
	 *
	 * @param valueStr
	 *            基础值
	 * @param addStr
	 *            被加数
	 * @return String
	 */
	public static String moneyAdd(String valueStr, String addStr) {
		BigDecimal value = new BigDecimal(valueStr);
		BigDecimal augend = new BigDecimal(addStr);
		return dmFormat.format(value.add(augend));
	}

	/**
	 * 金额相减
	 *
	 * @param valueStr
	 *            基础值
	 * @param minusValueStr
	 *            减数
	 * @return String
	 */
	public static String moneySub(String valueStr, String minusValueStr) {
		BigDecimal value = new BigDecimal(valueStr);
		BigDecimal subtrahend = new BigDecimal(minusValueStr);
		return dmFormat.format(value.subtract(subtrahend));
	}

	/**
	 * 值比较大小 <br/>
	 * 如果valueStr大于等于compValueStr,则返回true,否则返回false true 代表可用余额不足
	 *
	 * @param valueStr
	 *            (需要消费金额)
	 * @param compValueStr
	 *            (可使用金额)
	 * @return boolean
	 */
	public static boolean moneyComp(String valueStr, String compValueStr) {
		BigDecimal value = new BigDecimal(valueStr);
		BigDecimal compValue = new BigDecimal(compValueStr);
		// 0:等于 >0:大于 <0:小于
		int result = value.compareTo(compValue);
		if (result >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 两个金额相同返回true
	 * @param valueStr
	 * @param compValueStr
	 * @return
	 */
	public static boolean moneyCompSame(String valueStr, String compValueStr) {
		BigDecimal value = new BigDecimal(valueStr);
		BigDecimal compValue = new BigDecimal(compValueStr);
		// 0:等于 >0:大于 <0:小于
		int result = value.compareTo(compValue);
		if (result == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * valueStr大于compValueStr返回true
	 * @param valueStr
	 * @param compValueStr
	 * @return
	 */
	public static boolean moneyCompMore(String valueStr, String compValueStr) {
		BigDecimal value = new BigDecimal(valueStr);
		BigDecimal compValue = new BigDecimal(compValueStr);
		// 0:等于 >0:大于 <0:小于
		int result = value.compareTo(compValue);
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

}
