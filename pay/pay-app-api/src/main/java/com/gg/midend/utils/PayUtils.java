package com.gg.midend.utils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.gg.midend.config.GlobalConfig;


public class PayUtils {

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	public static String payMD5(String data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	public static String payMD5Low(String data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	/**
	 * 生成订单流水号
	 * 
	 * @return
	 */
	public static String generatePipeline() {
		return DateUtils.getDateTime("yyyyMMddHHmmss") + CreateRandomNumber(6);
	}

	/**
	 * 生成订单流水号
	 *
	 * @return
	 */
	public static String generatePipeline(String type) {
		return DateUtils.getDateTime("yyyyMMddHHmmss") + type + CreateRandomNumber(6);
	}

	/**
	 * 国光支付宝网关签名计算
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String aligenerateSignature(Map<String, Object> data, String key) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(JsonUtils.MapToJson(data));
		sb.append(key);
		GlobalConfig.log_api.info("aligenerateSignature payMD5 indata：{}", sb.toString());
		GlobalConfig.log_api.info("aligenerateSignature payMD5 key：{}", key);
		return payMD5(sb.toString()).toUpperCase();
	}

	/**
	 * 国光微信网关签名计算
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String wxgenerateSignature(Map<String, Object> data, String key) throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals("sign")) {
				continue;
			}
			// if (((String) data.get(k)).trim().length() > 0) // 参数值为空，则不参与签名
			// sb.append(k).append("=").append(((String) data.get(k)).trim()).append("&");
			sb.append(k).append("=").append(((String) data.get(k)).trim()).append("&");
		}
		sb.append("key=").append(key);
		System.out.println("wxgenerateSignature payMD5 indata = " + sb.toString());
		return payMD5(sb.toString()).toUpperCase();
	}

	/**
	 * 在金额前面加上负号
	 * 
	 * @param amt
	 * @return
	 */
	public static String minusAmt(String amt) {
		return "-" + amt;
	}

	/**
	 * 创建一个指定长度的随机数字字符串
	 * 
	 * @param numLength
	 * @return
	 */
	public static String CreateRandomNumber(int numLength) {
		if (numLength < 1) {
			numLength = 10;
		}
		StringBuilder result = new StringBuilder();
		int a[] = new int[numLength];
		for (int i = 0; i < numLength; i++) {
			a[i] = (int) (10 * (Math.random()));
			result.append(a[i]);
		}
		return result.toString();
	}

	/**
	 * 将单位为分的金额转换为单位为元
	 * 
	 * @param fen
	 *            单位为分的字符型值
	 * @return
	 */
	public static String fen2Yuan(String fen) {
		BigDecimal var1 = new BigDecimal(fen);
		BigDecimal var2 = new BigDecimal(100);
		BigDecimal var3 = var1.divide(var2);
		return var3.stripTrailingZeros().toPlainString();
	}

	/**
	 * 将单位为元的金额转换为单位为分
	 * 
	 * @param yuan
	 *            单位为元的字符型值
	 * @return
	 */
	public static String yuan2Fen(String yuan) {
		BigDecimal var1 = new BigDecimal(yuan);
		BigDecimal var2 = new BigDecimal(100);
		BigDecimal var3 = var1.multiply(var2);
		return var3.stripTrailingZeros().toPlainString();
	}

}
