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

/**
 * 撤销订单
 * @author 87392
 *
 */
@Service
public class OrderRevokeService {
	
	@Autowired
	private SqlService sqlService;
	@Autowired
	private AliPayService aliPayService;
	@Autowired
	private WechatPayService wechatPayService;
	
	/**
	 * 撤销订单
	 * @param requestMap
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> revokeOrder(Map<String, Object> requestMap) throws ApiException {
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
				resultMap.put("returnCode", "2001");
				return resultMap;
			} else {
				tradeParam.putAll(orderInfoQuery(recordList));
			}
			if (!payTypeCheck(tradeParam)) {// 支付方式校验
				resultMap.put("returnCode", "0006");
				return resultMap;
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
			Map<String, Object> outdata = new HashMap<String, Object>();// 返回数据data节点
			outdata.put("orderId", tradeParam.get("orderId"));
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
			if (!VerifyMapUtils.notEmpty(tradeParam, new String[] {"orderId"}))
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
	 * 支付方式校验
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	private boolean payTypeCheck(Map<String, Object> tradeParam) throws Exception {
		String payType = Convert.convert(String.class, tradeParam.get("payType"));
		if("0102".equals(payType)||"0202".equals(payType))
		   return true;
		return false;
	}
	
	/**
	 * 判断交易码是否正确
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean tradeTypeCheck(Map<String, Object> requestMap) throws Exception {
		String tradeType = (String) requestMap.get("tradeType");
		if (!"0004".equals(tradeType)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 联机交易
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> verifyCommunication(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回数据
		Map<String, Object> resp = new HashMap<String, Object>();// 与第三方网关交互返回
		Map<String, Object> subresp = new HashMap<String, Object>();// 与第三方网关交互返回子集
		try {
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			resultMap.put("returnCode", "0000");// 默认成功，可以Update订单状态
			if ("01".equals(thirdId)) {//支付宝
				String appId = aliPayService.alipayAppIdGet(tradeParam);
				tradeParam.put("appId", appId);
				resp = aliPayService.f2fCancelPay(tradeParam);
				GlobalConfig.log_api.info("OrderRevokeService 01 收到返回：{}", resp);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				if (!"200".equals(resp.get("statuscode")) || !"0000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0009");//取消订单失败
				}else {
					resultMap.put("returnCode", "0000");
				}
			} else if ("02".equals(thirdId)) {
				resp = wechatPayService.CloseOrder(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderRevokeService 02 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"000000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0009");//取消订单失败
				}else {
					Map<String, Object> data = (Map<String, Object>) subresp.get("data");// 微信返回data节点
					if("SUCCESS".equals(data.get("result_code"))){
						resultMap.put("returnCode", "0000");
					}else {
						resultMap.put("returnCode", "0009");//取消订单失败
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
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> updateOrderStatus(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回数据
		resultMap.put("returnCode", "0000");
		int ret = 0;
		tradeParam.put("pay_status", "9");
		ret = sqlService.update("pay_order", "updatePayOrderStatus", tradeParam);//更新订单状态为取消
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0009");// 取消订单失败
			return resultMap;
		}
		tradeParam.put("status", "9");
		ret = sqlService.update("pay_order_pay", "updateAllPayOrderPayStatus", tradeParam);//更新订单状态为取消
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0009");// 取消订单失败
			return resultMap;
		}
		return resultMap;
	}

}
