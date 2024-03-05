package com.gg.midend.service.app;

import java.math.BigDecimal;
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
import com.gg.midend.utils.DateUtils;
import com.gg.midend.utils.JsonUtils;
import com.gg.midend.utils.MoneyUtils;
import com.gg.midend.utils.PayUtils;
import com.gg.midend.utils.VerifyMapUtils;

import cn.hutool.core.convert.Convert;

/**
 * 模式2创建订单
 * 
 * @author 87392
 *
 */
@Service
public class OrderPayService {

	@Autowired
	private SqlService sqlService;
	@Autowired
	private AliPayService aliPayService;
	@Autowired
	private WechatPayService wechatPayService;

	/**
	 * 生成原始订单号 pay_order 状态为创建订单等待支付 pay_order_pay 状态为待确认 
	 * 支持拼付
	 * 
	 * @param requestMap
	 * @return
	 * @throws ApiException
	 */
	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> payOrder(Map<String, Object> requestMap) throws ApiException {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
		Map<String, Object> tradeParam = new HashMap<String, Object>();// tradeParam里面的数据
		resultMap.put("returnCode", "0000");
		try {
			if (!fieldCheck(requestMap)) {// 上送报文字段校验
				resultMap.put("returnCode", "1010");
				return resultMap;
			}
			tradeParam = Convert.toMap(String.class, Object.class, requestMap.get("tradeParam"));
			String merchantId = channelIdCheck(tradeParam);// 收单机构、渠道、支付方式是否开通，匹配出真实商户号
			if (merchantId == null || merchantId.length() == 0) {
				resultMap.put("returnCode", "1001");
				return resultMap;
			} else {
				tradeParam.put("realMerchantId", merchantId);
			}
			if (!hospitalIdCheck(tradeParam)) {// 判断上送报文的医院ID是否存在
				resultMap.put("returnCode", "1006");
				return resultMap;
			}
			if (!instIdCheck(tradeParam)) {// 收单机构、商户是否已激活
				resultMap.put("returnCode", "1002");
				return resultMap;
			}
			if (!transactionTypeCheck(tradeParam)) {// 交易类型是否正确
				resultMap.put("returnCode", "1004");
				return resultMap;
			}
			if (!payTypeCheck(tradeParam)) {// 支付方式、商户号是否已激活
				resultMap.put("returnCode", "1003");
				return resultMap;
			}
			if (!payThirdCheck(tradeParam)) {// thirdid是否存在
				resultMap.put("returnCode", "1007");
				return resultMap;
			}
			if (!tradeTypeCheck(requestMap)) {// 判断交易码是否正确
				resultMap.put("returnCode", "0004");
				return resultMap;
			}
			if (!verifyPayType(tradeParam)) {// 判断支付类型和下单模式是否匹配
				resultMap.put("returnCode", "0006");
				return resultMap;
			}
			if (!verifyAmt(tradeParam)) {// 判断订单中金额的合理性
				resultMap.put("returnCode", "1008");
				return resultMap;
			}
			String orderTrace = generateOrderTrace();
			String orderId = null;
			if (tradeParam.containsKey("orderId") && tradeParam.get("orderId") != null) {
				System.out.println("+++++++++++++++++++++++");
				orderId = Convert.convert(String.class, tradeParam.get("orderId"));
				if (orderId == null || orderId.length() < 20) {
					resultMap.put("returnCode", "1009");
					return resultMap;
				}
			} else {
				orderId = generateOrderTrace();
			}

			String createDate = orderTrace.substring(0, 8);
			String createTime = DateUtils.getDateTime("HHmmss");
			tradeParam.put("orderTrace", orderTrace);
			tradeParam.put("orderId", orderId);
			tradeParam.put("createDate", createDate);
			tradeParam.put("createTime", createTime);

			if (!existOrder(tradeParam)) {
				resultMap.put("returnCode", "0016");// 订单号存在，终端调用0003；如果不存在，终端使用原交易记录重做payOrder
				return resultMap;
			}
			if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
				if (!verifySpellPayType(tradeParam)) {// 判断拼付支付类型,thirdId
					resultMap.put("returnCode", "1011");
					return resultMap;
				}
				String secOrderId = null;
				if (tradeParam.containsKey("secOrderId") && tradeParam.get("secOrderId") != null) {						
					secOrderId = Convert.convert(String.class, tradeParam.get("secOrderId"));
				}else {
					secOrderId = generateOrderTrace();
				}
				tradeParam.put("secOrderId", secOrderId);
				String secMerchantId = channelSecIdCheck(tradeParam);
				if (secMerchantId == null || secMerchantId.length() == 0) {
					resultMap.put("returnCode", "1001");
					return resultMap;
				}
				tradeParam.put("realSecMerchantId", secMerchantId);
			}
			if (!generatePayOrder(tradeParam)) {// 生成pay_order记录
				resultMap.put("returnCode", "0007");
				return resultMap;
			}
			if (!generatePayOrderPay(tradeParam)) {// 生成pay_order_pay记录
				resultMap.put("returnCode", "0007");
				return resultMap;
			}
			if (!medicalOrderDeal(tradeParam)) {// 处理医保订单金额
				resultMap.put("returnCode", "0007");
				return resultMap;
			}
			// >>>>>>>>>>>>>>>>处理拼付订单数据库记录逻辑(留存)>>>>>>>>>>>>>>>>
			if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
				if (!generateSecPayOrderPay(tradeParam)) {// 生成pay_order_pay记录
					resultMap.put("returnCode", "0007");
					return resultMap;
				}
				if (!medicalSecOrderDeal(tradeParam)) {// 处理医保订单金额
					resultMap.put("returnCode", "0007");
					return resultMap;
				}
			}
			// <<<<<<<<<<<<<<<<处理拼付订单数据库记录逻辑(留存)<<<<<<<<<<<<<<<<
			resultMap = verifyCommunication(tradeParam);
			if ("0007".equals(resultMap.get("returnCode"))) {// 确认失败
				return resultMap;
			}
			//0020或0000
			Map<String, Object> outdata = new HashMap<String, Object>();// 返回数据data节点
			outdata.put("orderId", tradeParam.get("orderId"));
			if (tradeParam.containsKey("secOrderId"))
				outdata.put("secOrderId", tradeParam.get("secOrderId"));
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
			if (!VerifyMapUtils.notEmpty(tradeParam, new String[] { "merchantId", "chanelType", "thirdId", "orderType",
					"payType", "hospitalZoneCode", "orderAmt" }))
				return false;
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			if("06".equals(thirdId)) {
				if (!VerifyMapUtils.notEmpty(tradeParam, new String[] { "planAmt", "personAmt" }))
					return false;
			}
		}
		return true;
	}

	/**
	 * 通过上送的收单机构、渠道、支付方式与预查询符合要求List数据比对 比对成功则上送收单机构、渠道、支付方式开通成功，返回商户号
	 * 
	 * @param tradeParam
	 * @return
	 */
	private String channelIdCheck(Map<String, Object> tradeParam) throws Exception {
		String thisRealMerchantId = null;
		String merchantId = Convert.convert(String.class, tradeParam.get("merchantId"));
		String chanelType = Convert.convert(String.class, tradeParam.get("chanelType"));
		String payType = Convert.convert(String.class, tradeParam.get("payType"));
		if (merchantId == null || chanelType == null || payType == null || GlobalParam.merchantWithChannelList == null)// 防止空指针
			return thisRealMerchantId;
		for (Object o : GlobalParam.merchantWithChannelList) {
			Map<Object, Object> oo = (Map) o;
			String thisMerchantId = Convert.convert(String.class, oo.get("inst_id"));
			String thisChanelType = Convert.convert(String.class, oo.get("channel_id"));
			String thisPayType = Convert.convert(String.class, oo.get("pay_type_id"));
			if (merchantId.equalsIgnoreCase(thisMerchantId) && chanelType.equalsIgnoreCase(thisChanelType)
					&& payType.equalsIgnoreCase(thisPayType)) {
				thisRealMerchantId = Convert.convert(String.class, oo.get("merchant_id"));
				break;
			}
		}
		return thisRealMerchantId;
	}

	/**
	 * 判断上送报文的医院ID是否存在:mer_hospital
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean hospitalIdCheck(Map<String, Object> tradeParam) {
		boolean flag = false;
		String hospId = Convert.convert(String.class, tradeParam.get("hospitalZoneCode"));
		if (hospId == null || GlobalParam.hospIdList == null)// 防止空指针
			return flag;
		for (Object o : GlobalParam.hospIdList) {
			Map<Object, Object> oo = (Map) o;
			String thisHospId = Convert.convert(String.class, oo.get("hospital_id"));
			if (hospId.equalsIgnoreCase(thisHospId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 收单机构、商户是否已激活:mer_merchant
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean instIdCheck(Map<String, Object> tradeParam) {
		boolean flag = false;
		String merchantId = Convert.convert(String.class, tradeParam.get("merchantId"));
		String realMerchantId = Convert.convert(String.class, tradeParam.get("realMerchantId"));
		if (merchantId == null || realMerchantId == null || GlobalParam.merchantWithInstList == null)// 防止空指针
			return flag;
		for (Object o : GlobalParam.merchantWithInstList) {
			Map<Object, Object> oo = (Map) o;
			String thisMerchantId = Convert.convert(String.class, oo.get("inst_id"));
			String thisRealMerchantId = Convert.convert(String.class, oo.get("id"));
			if (merchantId.equalsIgnoreCase(thisMerchantId) && realMerchantId.equalsIgnoreCase(thisRealMerchantId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 交易类型是否正确
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean transactionTypeCheck(Map<String, Object> tradeParam) {
		boolean flag = false;
		String orderType = Convert.convert(String.class, tradeParam.get("orderType"));
		if (orderType == null || GlobalParam.transactionTypeList == null)// 防止空指针
			return flag;
		for (Object o : GlobalParam.transactionTypeList) {
			Map<Object, Object> oo = (Map) o;
			String thisGoodId = Convert.convert(String.class, oo.get("id"));
			if (orderType.equalsIgnoreCase(thisGoodId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 支付方式、商户号是否已激活
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean payTypeCheck(Map<String, Object> tradeParam) {
		boolean flag = false;
		String payType = Convert.convert(String.class, tradeParam.get("payType"));
		String realMerchantId = Convert.convert(String.class, tradeParam.get("realMerchantId"));
		if (payType == null || realMerchantId == null || GlobalParam.merchantWithPayTpeyList == null)// 防止空指针
			return flag;
		for (Object o : GlobalParam.merchantWithPayTpeyList) {
			Map<Object, Object> oo = (Map) o;
			String thisPayType = Convert.convert(String.class, oo.get("pay_type_id"));
			String thisRealMerchantId = Convert.convert(String.class, oo.get("merchant_id"));
			if (payType.equalsIgnoreCase(thisPayType) && realMerchantId.equalsIgnoreCase(thisRealMerchantId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * thirdid是否存在
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean payThirdCheck(Map<String, Object> tradeParam) {
		boolean flag = false;
		String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
		if (thirdId == null || GlobalParam.payThirdList == null)// 防止空指针
			return flag;
		for (Object o : GlobalParam.payThirdList) {
			Map<Object, Object> oo = (Map) o;
			String thisThirdId = Convert.convert(String.class, oo.get("id"));
			if (thirdId.equalsIgnoreCase(thisThirdId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 判断交易码是否正确
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean tradeTypeCheck(Map<String, Object> requestMap) throws Exception {
		String tradeType = (String) requestMap.get("tradeType");
		if (!"0002".equals(tradeType)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断支付类型和下单模式是否匹配
	 * 
	 * @param tradeParam
	 * @return
	 */
	private boolean verifyPayType(Map<String, Object> tradeParam) throws Exception {
		String payType = (String) tradeParam.get("payType");
		String thirdId = (String) tradeParam.get("thirdId");
		if (!payType.substring(0, 2).equals(thirdId))
			return false;
		if ("0101".equals(payType) || "0104".equals(payType) || "0201".equals(payType) || "0204".equals(payType)
				|| "0501".equals(payType)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断订单中金额的合理性
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean verifyAmt(Map<String, Object> tradeParam) throws Exception {
		BigDecimal unionBDAmt = new BigDecimal(0);
		BigDecimal orderAmtBD = new BigDecimal(0);
		if (tradeParam.containsKey("orderAmt")) {
			String orderAmt = (String) tradeParam.get("orderAmt");
			orderAmtBD = new BigDecimal(orderAmt);
		}
		if (tradeParam.containsKey("settleAmt")) {
			String settleAmt = (String) tradeParam.get("settleAmt");
			BigDecimal settleAmtBD = new BigDecimal(settleAmt);
			unionBDAmt = MoneyUtils.moneyAdd(unionBDAmt, settleAmtBD);
		}
		if (tradeParam.containsKey("planAmt")) {
			String planAmt = (String) tradeParam.get("planAmt");
			BigDecimal planAmtBD = new BigDecimal(planAmt);
			unionBDAmt = MoneyUtils.moneyAdd(unionBDAmt, planAmtBD);
		}
		if (tradeParam.containsKey("personAmt")) {
			String personAmt = (String) tradeParam.get("personAmt");
			BigDecimal personAmtBD = new BigDecimal(personAmt);
			unionBDAmt = MoneyUtils.moneyAdd(unionBDAmt, personAmtBD);
		}
		if (MoneyUtils.moneyComp(orderAmtBD, unionBDAmt)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成平台创建的原始订单号
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private String generateOrderTrace() throws Exception {
		String orderTrace = null;
		orderTrace = PayUtils.generatePipeline();
		return orderTrace;
	}

	/**
	 * 判断数据库是否插入了相应的订单记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean existOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectOrder", tradeParam);
		if (recordList.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 拼付订单支付类型、thirdId不能相同 拼付必须上送自己的支付类型、thirdId
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean verifySpellPayType(Map<String, Object> tradeParam) throws Exception {
		String payType = (String) tradeParam.get("payType");
		String thirdId = (String) tradeParam.get("thirdId");
		if (!tradeParam.containsKey("secThirdId"))
			return false;
		if (tradeParam.containsKey("secPayType"))
			return false;
		String secThirdId = (String) tradeParam.get("secThirdId");
		String secPayType = (String) tradeParam.get("secPayType");
		if (payType.equals(secPayType) || thirdId.equals(secThirdId)) {
			return false;
		}
		if (!secPayType.substring(0, 2).equals(secThirdId))
			return false;
		if (!"06".equals(secThirdId))
			return false;
		return true;
	}
	
	/**
	 * 通过上送的收单机构、渠道、支付方式与预查询符合要求List数据比对 比对成功则上送收单机构、渠道、支付方式开通成功，返回商户号
	 * 
	 * @param tradeParam
	 * @return
	 */
	private String channelSecIdCheck(Map<String, Object> tradeParam) throws Exception {
		String thisRealMerchantId = null;
		String merchantId = Convert.convert(String.class, tradeParam.get("merchantId"));//实际为收单机构号码
		String chanelType = Convert.convert(String.class, tradeParam.get("chanelType"));
		String secPayType = Convert.convert(String.class, tradeParam.get("secPayType"));
		if (merchantId == null || chanelType == null || secPayType == null || GlobalParam.merchantWithChannelList == null)// 防止空指针
			return thisRealMerchantId;
		for (Object o : GlobalParam.merchantWithChannelList) {
			Map<Object, Object> oo = (Map) o;
			String thisMerchantId = Convert.convert(String.class, oo.get("inst_id"));
			String thisChanelType = Convert.convert(String.class, oo.get("channel_id"));
			String thisPayType = Convert.convert(String.class, oo.get("pay_type_id"));
			if (merchantId.equalsIgnoreCase(thisMerchantId) && chanelType.equalsIgnoreCase(thisChanelType)
					&& secPayType.equalsIgnoreCase(thisPayType)) {
				thisRealMerchantId = Convert.convert(String.class, oo.get("merchant_id"));
				break;
			}
		}
		return thisRealMerchantId;
	}

	/**
	 * 生成pay_order记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean generatePayOrder(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("order_trace", tradeParam.get("orderTrace"));
			paramMap.put("merchant_id", tradeParam.get("realMerchantId"));
			paramMap.put("hospital_id", tradeParam.get("hospitalZoneCode"));
			paramMap.put("channel_id", tradeParam.get("chanelType"));
			paramMap.put("goods_id", tradeParam.get("orderType"));
			paramMap.put("order_amt", tradeParam.get("orderAmt"));
			paramMap.put("refund_amt", 0);
			paramMap.put("pay_status", "1");
			paramMap.put("refund_status", "0");
			paramMap.put("biz_status", "0");
			paramMap.put("buyer_id", tradeParam.get("payerId"));
			paramMap.put("buyer_name", tradeParam.get("payerName"));
			paramMap.put("payerTel", tradeParam.get("payerTel"));
			paramMap.put("fac_id", tradeParam.get("manufacturerId"));
			paramMap.put("create_date", tradeParam.get("createDate"));
			paramMap.put("create_time", tradeParam.get("createTime"));
			int ret = 0;
			ret = sqlService.update("pay_order", "insert", paramMap);
			if (ret != 1) {// 影响一条记录
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 生成pay_order_pay记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean generatePayOrderPay(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("order_id", tradeParam.get("orderId"));
			paramMap.put("merchant_id", tradeParam.get("realMerchantId"));
			paramMap.put("hospital_id", tradeParam.get("hospitalZoneCode"));
			paramMap.put("order_trace", tradeParam.get("orderTrace"));
			paramMap.put("trans_type", 1);
			paramMap.put("third_id", tradeParam.get("thirdId"));
			paramMap.put("pay_type_id", tradeParam.get("payType"));
			paramMap.put("channel_id", tradeParam.get("chanelType"));
			paramMap.put("order_amt", Convert.convert(Integer.class, tradeParam.get("settleAmt")));
			paramMap.put("create_date", tradeParam.get("createDate"));
			paramMap.put("create_time", tradeParam.get("createTime"));
			paramMap.put("status", "0");
			if (tradeParam.containsKey("oper_id"))
				paramMap.put("oper_id", tradeParam.get("operId"));
			if (tradeParam.containsKey("oper_name"))
				paramMap.put("oper_name", tradeParam.get("operName"));
			if (tradeParam.containsKey("term_id"))
				paramMap.put("term_id", tradeParam.get("termId"));
			int ret = 0;
			ret = sqlService.update("pay_order_pay", "insert", paramMap);
			if (ret != 1) {// 影响一条记录
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 记录医保金额
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean medicalOrderDeal(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			if ("06".equals(thirdId)) {
				paramMap.put("order_id", tradeParam.get("orderId"));
				paramMap.put("medfee_sumamt", tradeParam.get("orderAmt"));// 订单总金额
				paramMap.put("fund_pay_sumamt", tradeParam.get("planAmt"));// 统筹金额
				paramMap.put("acct_pay", tradeParam.get("personAmt"));// 个人医保账户余额支出
				paramMap.put("psn_cash_pay", tradeParam.get("settleAmt"));// 个人自费支出
				int ret = 0;
				ret = sqlService.update("pay_medical_fee", "insert", paramMap);
				if (ret != 1) {// 影响一条记录
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 生成pay_order_pay拼付部分记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean generateSecPayOrderPay(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("order_id", tradeParam.get("secOrderId"));
			paramMap.put("merchant_id", tradeParam.get("realSecMerchantId"));
			paramMap.put("hospital_id", tradeParam.get("hospitalZoneCode"));
			paramMap.put("order_trace", tradeParam.get("orderTrace"));
			paramMap.put("trans_type", 1);
			paramMap.put("third_id", tradeParam.get("secThirdId"));
			paramMap.put("pay_type_id", tradeParam.get("secPayType"));
			paramMap.put("channel_id", tradeParam.get("chanelType"));
			paramMap.put("order_amt", 0);
			paramMap.put("create_date", tradeParam.get("createDate"));
			paramMap.put("create_time", tradeParam.get("createTime"));
			paramMap.put("status", "0");
			if (tradeParam.containsKey("oper_id"))
				paramMap.put("oper_id", tradeParam.get("operId"));
			if (tradeParam.containsKey("oper_name"))
				paramMap.put("oper_name", tradeParam.get("operName"));
			if (tradeParam.containsKey("term_id"))
				paramMap.put("term_id", tradeParam.get("termId"));
			int ret = 0;
			ret = sqlService.update("pay_order_pay", "insert", paramMap);
			if (ret != 1) {// 影响一条记录
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 记录医保金额
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean medicalSecOrderDeal(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("order_id", tradeParam.get("secOrderId"));
			paramMap.put("medfee_sumamt", Convert.convert(Integer.class, tradeParam.get("orderAmt")));// 订单总金额
			paramMap.put("fund_pay_sumamt", Convert.convert(Integer.class, tradeParam.get("planAmt")));// 统筹金额
			paramMap.put("acct_pay", Convert.convert(Integer.class, tradeParam.get("personAmt")));// 个人医保账户余额支出
			paramMap.put("psn_cash_pay", Convert.convert(Integer.class, tradeParam.get("settleAmt")));// 个人自费支出
			int ret = 0;
			ret = sqlService.update("pay_medical_fee", "insert", paramMap);
			if (ret != 1) {// 影响一条记录
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	 * 判断支付类型是否需要平台联机交易 ,需要就发起交易
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
			resultMap.put("returnCode", "0000");
			if ("0101".equals(payType) || "0104".equals(payType)) {
				String appId = aliPayService.alipayAppIdGet(tradeParam);
				tradeParam.put("appId", appId);
				tradeParam.put("subject", goodNameGet(tradeParam));// 获取商品名称
				resp = aliPayService.f2fBarCodePay(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderPayService 0101 and 0104 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"0000".equals(subresp.get("returnCode"))) {
					if ("200".equals(resp.get("statuscode"))) {
						if ("WAITING_PAY".equals(subresp.get("returnCode"))
								|| "SYSTEM_ERROR".equals(subresp.get("returnCode"))) {
							resultMap.put("returnCode", "0020");// 支付中请轮询结果
						} else {
							resultMap.put("returnCode", "0007");// 确定失败
						}
					} else {
						resultMap.put("returnCode", "0020");// 通信未知结果，继续查询
					}
				}
			} else if ("0201".equals(payType) || "0204".equals(payType)) {// 微信
				resp = wechatPayService.f2fBarCodePay(tradeParam);
				subresp = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
				GlobalConfig.log_api.info("OrderPayService 0201 and 0204 收到返回：{}", resp);
				if (!"200".equals(resp.get("statuscode")) || !"000000".equals(subresp.get("returnCode"))) {
					if ("200".equals(resp.get("statuscode"))) {
						if ("WAITING_PAY".equals(subresp.get("returnCode"))
								|| "SYSTEM_ERROR".equals(subresp.get("returnCode"))) {
							resultMap.put("returnCode", "0020");// 支付中请轮询结果
						} else {
							resultMap.put("returnCode", "0007");// 确定失败
						}
					} else {
						resultMap.put("returnCode", "0020");// 通信未知结果，继续查询
					}
				}
			}
		} catch (Exception e) {// 防止联机交易后异常传导到上层事务
			resultMap.put("returnCode", "9999");
			return resultMap;
		}
		return resultMap;
	}

}
