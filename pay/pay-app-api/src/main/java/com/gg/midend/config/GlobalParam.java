package com.gg.midend.config;

import java.util.List;
import java.util.Map;

/**
 * 系统入参判断对比数据
 * @author 87392
 *
 */
public class GlobalParam {

	public static List<Map<String, Object>> transactionTypeList = null; //系统交易类型
	
	public static List<Map<String, Object>> merchantWithInstList = null; //商户与收单机构关系
	
	public static List<Map<String, Object>> merchantWithChannelList = null; //商户与渠道关系
	
	public static List<Map<String, Object>> merchantWithPayTpeyList = null; //商户关系与支付方式
	
	public static List<Map<String, Object>> hospIdList = null; //系统医院id
	
	public static List<Map<String, Object>> payTypeList = null; //系统支付方式
	
	public static List<Map<String, Object>> payThirdList = null; //thirdid
	
	public static List<Map<String, Object>> alipayParamList = null; //支付宝参数
	
	public static List<Map<String, Object>> wechatParamList = null; //微信参数
	
}
