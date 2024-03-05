package com.gg.midend.service.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.config.GlobalParam;
import com.gg.midend.utils.JsonUtils;
import com.gg.midend.utils.VerifyMapUtils;

import cn.hutool.core.convert.Convert;

/**
 * 订单退款
 * @author 87392
 *
 */
@Service
public class OrderRefundService {
	
	@Autowired
	private SqlService sqlService;
	@Autowired
	private AliPayService aliPayService;
	@Autowired
	private WechatPayService wechatPayService;

	/**
	 * 订单退款
	 * @param requestMap
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> refundOrder(Map<String, Object> requestMap) throws ApiException {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
		Map<String, Object> tradeParam = new HashMap<String, Object>();// tradeParam里面的数据
		resultMap.put("returnCode", "0000");
		try {
			if (!fieldCheck(requestMap)) {// 上送报文字段校验
				resultMap.put("returnCode", "1010");
				return resultMap;
			}
			tradeParam = Convert.toMap(String.class, Object.class, requestMap.get("tradeParam"));
			List<Map<String, Object>> recordList = existOrder(tradeParam);
			if (recordList.size() != 1) {
				resultMap.put("returnCode", "2009");
				return resultMap;
			} else {
				tradeParam.putAll(orderInfoQuery(recordList));
			}
			if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
				if (!tradeParam.containsKey("secOrderId")) {
					resultMap.put("returnCode", "1010");
					return resultMap;
				}
				List<Map<String, Object>> secRecordList = existSecOrder(tradeParam);
				if (secRecordList.size() != 1) {
					resultMap.put("returnCode", "2009");
					return resultMap;
				} else {
					tradeParam.putAll(secOrderInfoQuery(secRecordList));
				}
			}
			if (!tradeTypeCheck(requestMap)) {// 判断交易码是否正确
				resultMap.put("returnCode", "0004");
				return resultMap;
			}
			resultMap = orderPayInfoQuery(tradeParam);// 查询原始订单中的订单金额
			if (!"0000".equals(resultMap.get("returnCode"))) {
				return resultMap;
			}
			tradeParam.put("orderAmt", resultMap.get("orderAmt"));
			resultMap = verifyCommunication(tradeParam);
			Map<String, Object> outdata = new HashMap<String, Object>();//data节点
			outdata.put("orderId", tradeParam.get("orderId"));
			outdata.put("refundorderId", tradeParam.get("refundorderId"));
			if (tradeParam.containsKey("secOrderId"))
				outdata.put("secOrderId", tradeParam.get("secOrderId"));
			if (tradeParam.containsKey("secRefundorderId"))
				outdata.put("secRefundorderId", tradeParam.get("secRefundorderId"));
			resultMap.put("data", outdata);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("returnCode", "9999");
			throw new ApiException(resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 报文字段校验
	 * 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	private boolean fieldCheck(Map<String, Object> requestMap) throws Exception {
		if (!VerifyMapUtils.notEmpty(requestMap, new String[] { "tradeType", "tradeParam" })) {
			return false;
		} else {
			Map<String, Object> tradeParam = Convert.toMap(String.class, Object.class, requestMap.get("tradeParam"));
			if (!VerifyMapUtils.notEmpty(tradeParam, new String[] { "refundorderId", "orderId", "refundFlag" }))
				return false;
		}
		return true;
	}
	
	/**
	 * 判断数据库是否插入了相应的退费订单记录
	 * 且未确认状态
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectRefundOrder", tradeParam);
		return recordList;
	}
	
	/**
	 * 退费订单信息查询
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> orderInfoQuery(List<Map<String, Object>> recordList) throws Exception {
		Map<String, Object> tradeParam = new HashMap<String, Object>();
		for (Object o : recordList) {
			Map<Object, Object> oo = (Map) o;
			String merchantId = Convert.convert(String.class, oo.get("merchant_id"));
			String payType = Convert.convert(String.class, oo.get("pay_type_id"));
			String thirdId = Convert.convert(String.class, oo.get("third_id"));
			String channelId = Convert.convert(String.class, oo.get("channel_id"));
			String orderTrace = Convert.convert(String.class, oo.get("order_trace"));
			String refundReason = Convert.convert(String.class, oo.get("refund_reason"));
			tradeParam.put("realMerchantId", merchantId);
			tradeParam.put("payType", payType);
			tradeParam.put("thirdId", thirdId);
			tradeParam.put("chanelType", channelId);
			tradeParam.put("orderTrace", orderTrace);
			tradeParam.put("refundReason", refundReason);
			if("01".equals(thirdId)||"02".equals(thirdId)) {
				String refundAmt = Convert.convert(String.class, oo.get("order_amt"));
				tradeParam.put("refundAmt", refundAmt);
			}
		}
		return tradeParam;
	}
	
	/**
	 * 判断数据库是否插入了拼付的退费订单记录
	 * 且未确认状态
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existSecOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectSecRefundOrder", tradeParam);
		return recordList;
	}
	
	/**
	 * 拼付退费订单信息查询
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> secOrderInfoQuery(List<Map<String, Object>> recordList) throws Exception {
		Map<String, Object> tradeParam = new HashMap<String, Object>();
		for (Object o : recordList) {
			Map<Object, Object> oo = (Map) o;
			String merchantId = Convert.convert(String.class, oo.get("merchant_id"));
			String payType = Convert.convert(String.class, oo.get("pay_type_id"));
			String thirdId = Convert.convert(String.class, oo.get("third_id"));
			tradeParam.put("realSecMerchantId", merchantId);
			tradeParam.put("secPayType", payType);
			tradeParam.put("secThirdId", thirdId);
		}
		return tradeParam;
	}
	
	/**
	 * 查询订单金额
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> orderPayInfoQuery(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
		resultMap.put("returnCode", "0000");
		List<Map<String, Object>> payOrderList = sqlService.selectList("pay_order", "selectOrderPay", tradeParam);
		if (payOrderList.size() != 1) {//pay_order没有对应记录			
			resultMap.put("returnCode", "2001");
			return resultMap;
		}
		Map<String, Object> payOrderInfo = payOrderList.get(0);
		String thisOrderAmt = Convert.convert(String.class, payOrderInfo.get("order_amt"));
		resultMap.put("orderAmt", thisOrderAmt);
		return resultMap;
	}
	
	/**
	 * 判断交易码是否正确
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean tradeTypeCheck(Map<String, Object> requestMap) throws Exception {
		String tradeType = (String) requestMap.get("tradeType");
		if (!"0006".equals(tradeType)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 商品名称获取
	 * 
	 * @param tradeParam
	 * @return
	 */
	private String goodNameGet(Map<String, Object> tradeParam) {
		String orderType = Convert.convert(String.class, tradeParam.get("orderType"));
		String thisGoodName = " ";
		if (orderType == null || GlobalParam.transactionTypeList == null)// 防止空指针
			return " ";
		for (Object o : GlobalParam.transactionTypeList) {
			Map<Object, Object> oo = (Map) o;
			String thisGoodId = Convert.convert(String.class, oo.get("id"));
			thisGoodName = Convert.convert(String.class, oo.get("name"));
			if (orderType.equalsIgnoreCase(thisGoodId)) {
				break;
			}
		}
		return thisGoodName;
	}
	
	/**
	 * 联机交易
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> verifyCommunication(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回数据
		Map<String, Object> outdata = new HashMap<String, Object>();// 返回数据data节点
		Map<String, Object> resp = new HashMap<String, Object>();// 与第三方网关交互返回
		Map<String, Object> subresp = new HashMap<String, Object>();// 与第三方网关交互返回子集
		try {
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			resultMap.put("returnCode", "0000");
			if ("01".equals(thirdId)) {//支付宝
				String appId = aliPayService.alipayAppIdGet(tradeParam);
				tradeParam.put("appId", appId);
				tradeParam.put("subject", goodNameGet(tradeParam));// 获取商品名称
				resp = aliPayService.f2fRefundTradePay(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderRefundService 01 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"0000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0010");//继续查询
				}else {
					resultMap.put("returnCode", "0000");//继续查询
				}
			} else if ("02".equals(thirdId)) {//微信
				resp = wechatPayService.Refund(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderRefundService 02 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"000000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0010");//继续查询
				}else {
					resultMap.put("returnCode", "0000");//继续查询
				}
			}
		} catch (Exception e) {// 防止联机交易后异常传导到上层事务
			resultMap.put("returnCode", "9999");
			return resultMap;
		}
		return resultMap;
	}
}
