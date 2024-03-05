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
import com.gg.midend.utils.DateUtils;
import com.gg.midend.utils.JsonUtils;
import com.gg.midend.utils.VerifyMapUtils;

import cn.hutool.core.convert.Convert;

@Service
public class OrderQueryService {

	@Autowired
	private SqlService sqlService;
	@Autowired
	private AliPayService aliPayService;
	@Autowired
	private WechatPayService wechatPayService;

	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> queryOrder(Map<String, Object> requestMap) throws ApiException {
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
				resultMap.put("returnCode", "0014");// 订单未创建
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
					resultMap.put("returnCode", "0014");// 订单未创建
					return resultMap;
				} else {
					tradeParam.putAll(secOrderInfoQuery(secRecordList));
				}
			}
			if (!tradeTypeCheck(requestMap)) {// 判断交易码是否正确
				resultMap.put("returnCode", "0004");
				return resultMap;
			}
			resultMap = verifyCommunication(tradeParam);// 联机交易
			if (!"0000".equals(resultMap.get("returnCode"))) {// 联机交易不成功或还没完全确认
				return resultMap;
			}
			resultMap = updateOrderStatus(tradeParam);//更新pay_order、pay_order_pay记录
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
			if (!VerifyMapUtils.notEmpty(tradeParam, new String[] { "orderId" }))
				return false;
		}
		return true;
	}

	/**
	 * 判断数据库是否插入了相应的订单记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectOrder", tradeParam);
		return recordList;
	}

	/**
	 * 订单信息查询
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
			tradeParam.put("realMerchantId", merchantId);
			tradeParam.put("payType", payType);
			tradeParam.put("thirdId", thirdId);
			tradeParam.put("chanelType", channelId);
			tradeParam.put("orderTrace", orderTrace);
		}
		return tradeParam;
	}

	/**
	 * 判断数据库是否插入了拼付的订单记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existSecOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectSecOrder", tradeParam);
		return recordList;
	}

	/**
	 * 拼付订单信息查询
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
	 * 判断交易码是否正确
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean tradeTypeCheck(Map<String, Object> requestMap) throws Exception {
		String tradeType = (String) requestMap.get("tradeType");
		if (!"0003".equals(tradeType)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断支付类型是否需要平台联机交易 ,需要就发起交易. 拼付订单secThirdId目前只支持06,不需要做联机交易，只处理thirdId、payType
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> verifyCommunication(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回数据
		Map<String, Object> resp = new HashMap<String, Object>();// 与第三方网关交互返回
		Map<String, Object> subresp = new HashMap<String, Object>();// 与第三方网关交互返回子集
		try {
			String payType = Convert.convert(String.class, tradeParam.get("payType"));
			resultMap.put("returnCode", "0000");// 默认成功，可以Update订单状态
			if ("0101".equals(payType) || "0102".equals(payType) || "0104".equals(payType)) {// 支付宝需要联机查询
				String appId = aliPayService.alipayAppIdGet(tradeParam);
				tradeParam.put("appId", appId);
				resp = aliPayService.f2fQueryPayResultForLoop(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderQueryService 0101 and 0102 and 0104 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"0000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0001");
				} else {
					Map<String, Object> data = Convert.toMap(String.class, Object.class, subresp.get("data"));// 返回data节点
					if (!"Payed".equals(data.get("status"))) {
						if ("TRADE_CLOSED".equals(data.get("tradeStatus"))
								|| "TRADE_FINISHED".equals(data.get("tradeStatus"))) {
							resultMap.put("returnCode", "0002");// 支付确认失败
						} else {
							resultMap.put("returnCode", "0001");// 支付结果查询失败，继续查询
						}
					}
				}
			} else if ("0201".equals(payType) || "0202".equals(payType) || "0204".equals(payType)) {// 微信需要联机查询
				resp = wechatPayService.OrderQuery(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderQueryService 0201 and 0202 and 0204 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"000000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0001");
				} else {
					Map<String, Object> data = Convert.toMap(String.class, Object.class, subresp.get("data"));// 返回data节点
					if (!"SUCCESS".equals(data.get("trade_state"))) {
						if ("NOTPAY".equals(data.get("trade_state")) || "USERPAYING".equals(data.get("trade_state"))) {
							resultMap.put("returnCode", "0001");// 支付结果查询失败，继续查询
						} else {
							resultMap.put("returnCode", "0002");// 支付确认失败
						}
					}
				}
			}
		} catch (Exception e) {// 防止联机交易后异常传导到上层事务
			resultMap.put("returnCode", "9999");
			return resultMap;
		}
		return resultMap;
	}

	/**
	 * 更新pay_order、pay_order_pay记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> updateOrderStatus(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回数据
		Map<String, Object> outdata = new HashMap<String, Object>();// 返回数据data节点
		int ret = 0;
		tradeParam.put("status", "1");//支付状态已确认
		tradeParam.put("thirdDate", DateUtils.getDateTime("yyyyMMdd"));
		tradeParam.put("thirdTime", DateUtils.getDateTime("HHmmss"));
		ret = sqlService.update("pay_order_pay", "updatePayOrderPayStatus", tradeParam);//更新支付订单为确认状态
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0001");// 支付结果查询失败，继续查询
			return resultMap;
		}
		if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
			ret = sqlService.update("pay_order_pay", "updateSecPayOrderPayStatus", tradeParam);//更新拼付订单为确认状态
			if (ret != 1) {// 影响一条记录
				resultMap.put("returnCode", "0001");// 支付结果查询失败，继续查询
				return resultMap;
			}
		}
		tradeParam.put("pay_status", "2");//订单支付完成
		ret = sqlService.update("pay_order", "updatePayOrderStatus", tradeParam);
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0001");// 支付结果查询失败，继续查询
			return resultMap;
		}
		outdata.put("orderId", tradeParam.get("orderId"));
		outdata.put("transDate", DateUtils.getDateTime("yyyyMMdd"));
		if (tradeParam.containsKey("secOrderId"))
			outdata.put("secOrderId", tradeParam.get("secOrderId"));
		if (tradeParam.containsKey("thirdPtls"))
			outdata.put("thirdPtls", tradeParam.get("thirdPtls"));
		if (tradeParam.containsKey("secThirdPtls"))
			outdata.put("secThirdPtls", tradeParam.get("secThirdPtls"));
		resultMap.put("data", outdata);
		resultMap.put("returnCode", "0000");
		return resultMap;
	}
}
