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
import com.gg.midend.utils.MoneyUtils;
import com.gg.midend.utils.VerifyMapUtils;

import cn.hutool.core.convert.Convert;

@Service
public class OrderRefundQueryService {

	@Autowired
	private SqlService sqlService;
	@Autowired
	private AliPayService aliPayService;
	@Autowired
	private WechatPayService wechatPayService;

	/**
	 * 订单退款
	 * 
	 * @param requestMap
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> refundQuery(Map<String, Object> requestMap) throws ApiException {
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
				resultMap.put("returnCode", "2008");
				return resultMap;
			} else {
				tradeParam.putAll(orderInfoQuery(recordList));
			}
			if (tradeParam.containsKey("spellFlag")) {// 拼付
				if (!tradeParam.containsKey("secOrderId")) {
					resultMap.put("returnCode", "1010");
					return resultMap;
				}
				List<Map<String, Object>> secRecordList = existSecOrder(tradeParam);
				if (secRecordList.size() != 1) {
					resultMap.put("returnCode", "2008");
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
			
			resultMap = verifyCommunication(tradeParam);// 联机交易
			if (!"0000".equals(resultMap.get("returnCode"))) {// 联机交易不成功或还没完全确认
				return resultMap;
			}
			resultMap = updateOrderStatus(tradeParam);//更新pay_order、pay_order_pay记录
			Map<String, Object> outdata = new HashMap<String, Object>();// 返回数据data节点
			outdata.put("transDate", DateUtils.getDateTime("yyyyMMdd"));
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
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectRefundOrder", tradeParam);
		return recordList;
	}

	/**
	 * 退费订单信息查询 且未确认状态
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
			if ("01".equals(thirdId) || "02".equals(thirdId)) {
				String refundAmt = Convert.convert(String.class, oo.get("order_amt"));
				tradeParam.put("refundAmt", refundAmt);
			}
		}
		return tradeParam;
	}

	/**
	 * 判断数据库是否插入了拼付的退费订单记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existSecOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectSecRefundOrder",
				tradeParam);
		return recordList;
	}

	/**
	 * 拼付退费订单信息查询 且未确认状态
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
		if (!"0007".equals(tradeType)) {
			return false;
		}
		return true;
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
	 * 判断支付类型是否需要平台联机交易 ,需要就发起交易. 
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
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			resultMap.put("returnCode", "0000");// 默认成功，可以Update订单状态
			if ("01".equals(thirdId)) {
				String appId = aliPayService.alipayAppIdGet(tradeParam);
				tradeParam.put("appId", appId);
				resp = aliPayService.f2fRefundTradeQuery(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderRefundQueryService 01 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"0000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0011");//需要继续确认
					if(resp.get("statuscode").equals("200")){
						resultMap.put("returnCode", "0012");//退费确认失败
					}
				} else {
					resultMap.put("returnCode", "0000");
				}
			} else if ("02".equals(thirdId)) {
				resp = wechatPayService.RefundQuery(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderRefundQueryService 02 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"000000".equals(subresp.get("returnCode"))) {
					resultMap.put("returnCode", "0011");//需要继续确认
					if(resp.get("statuscode").equals("200")){
						resultMap.put("returnCode", "0012");//退费确认失败
					}
				} else {
					resultMap.put("returnCode", "0000");
				}
			}
		} catch (Exception e) {// 防止联机交易后异常传导到上层事务
			resultMap.put("returnCode", "9999");
			return resultMap;
		}
		return resultMap;
	}
	
	/**
	 * 计算已退款金额
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private String calRefundAmt(List<Map<String, Object>> allConfirmedRefundList) throws Exception {
		String refundAmt = "0";
		for (Object o : allConfirmedRefundList) {
			Map<Object, Object> oo = (Map) o;
			String thirdId = Convert.convert(String.class, oo.get("third_id"));
			if("06".equals(thirdId)) {
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("acct_pay")));//医保个人账户
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("fund_pay_sumamt")));//基金统筹
			}else {
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("order_amt")));//自费金额
			}
		}
		return refundAmt;
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
		String refundFlag = Convert.convert(String.class, tradeParam.get("refundFlag"));//0正常退费，1冲正
		String orderAmt = Convert.convert(String.class, tradeParam.get("orderAmt"));//原订单总金额
		resultMap.put("returnCode", "0000");
		int ret = 0;
		//先更新pay_order_pay中本次影响的退费订单状态，下一步要统计已退费金额
		tradeParam.put("status", "1");//确认退费状态
		tradeParam.put("thirdDate", DateUtils.getDateTime("yyyyMMdd"));
		tradeParam.put("thirdTime", DateUtils.getDateTime("HHmmss"));
		ret = sqlService.update("pay_order_pay", "updatePayOrderPayRefundStatus", tradeParam);//更新支付订单为确认状态
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0011");// 退费结果确认失败，继续查询
			return resultMap;
		}
		if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
			ret = sqlService.update("pay_order_pay", "updateSecPayOrderPayRefundStatus", tradeParam);//更新拼付订单为确认状态
			if (ret != 1) {// 影响一条记录
				resultMap.put("returnCode", "0011");// 退费结果确认失败，继续查询
				return resultMap;
			}
		}
		//查询同一个orderTrace下面所有确认退费成功的记录，用来计算已退款金额总额
		List<Map<String, Object>> allConfirmedRefundList = sqlService.selectList("pay_order_pay", "selectAllConfirmRefundOrder",
				tradeParam);
		String refundAmt = calRefundAmt(allConfirmedRefundList);//已退费金额
		tradeParam.put("refundAmt", refundAmt);
		tradeParam.put("refund_amt", Convert.convert(Integer.class, tradeParam.get("refundAmt")));
		if(MoneyUtils.moneyCompSame(orderAmt, refundAmt)) {//金额相同
			if("1".equals(refundFlag)) {//冲正
				tradeParam.put("refund_status", "3");
			}else {
				tradeParam.put("refund_status", "1");//已经全额退
			}
		}
		if(MoneyUtils.moneyCompMore(orderAmt, refundAmt)) {//订单总金额大于已退费金额
			tradeParam.put("refund_status", "2");//部分退款
		}
		ret = sqlService.update("pay_order", "updatePayOrderStatus", tradeParam);
		if (ret != 1) {// 影响一条记录
			resultMap.put("returnCode", "0011");// 退费结果确认失败，继续查询
			return resultMap;
		}
		return resultMap;
	}

}
