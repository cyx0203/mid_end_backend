package com.gg.midend.service.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.DateUtils;
import com.gg.midend.utils.MoneyUtils;
import com.gg.midend.utils.PayUtils;
import com.gg.midend.utils.VerifyMapUtils;

import cn.hutool.core.convert.Convert;

@Service
public class OrderRefundCreateService {

	@Autowired
	private SqlService sqlService;

	@Transactional(rollbackFor = { Exception.class })
	public Map<String, Object> createRefundOrder(Map<String, Object> requestMap) throws ApiException {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
		Map<String, Object> tradeParam = new HashMap<String, Object>();// tradeParam里面的数据
		resultMap.put("returnCode", "0000");
		try {
			if (!fieldCheck(requestMap)) {// 上送报文字段校验
				resultMap.put("returnCode", "1010");
				return resultMap;
			}
			tradeParam = Convert.toMap(String.class, Object.class, requestMap.get("tradeParam"));
			String refundorderId = null;
			if (tradeParam.containsKey("refundorderId") && tradeParam.get("refundorderId") != null) {
				refundorderId = Convert.convert(String.class, tradeParam.get("refundorderId"));
				if (refundorderId == null || refundorderId.length() < 20) {
					resultMap.put("returnCode", "1012");
					return resultMap;
				}
			} else {
				refundorderId = generateOrderTrace();// 终端没有上送退费流水号，平台就生成
			}
			tradeParam.put("refundorderId", refundorderId);
			List<Map<String, Object>> recordList = existOrder(tradeParam);// 查询原支付订单是否存在，且支付确认完成，并带出必要信息
			if (recordList.size() != 1) {
				resultMap.put("returnCode", "0021");
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
					resultMap.put("returnCode", "0021");
					return resultMap;
				} else {
					tradeParam.putAll(secOrderInfoQuery(secRecordList));
				}
				String secRefundorderId = null;
				if (tradeParam.containsKey("secRefundorderId") && tradeParam.get("secRefundorderId") != null) {
					secRefundorderId = Convert.convert(String.class, tradeParam.get("secRefundorderId"));
					if (secRefundorderId == null || secRefundorderId.length() < 20) {
						resultMap.put("returnCode", "1012");
						return resultMap;
					}
				} else {
					secRefundorderId = generateOrderTrace();// 终端没有上送拼付退费流水号，平台就生成
				}
				tradeParam.put("secRefundorderId", secRefundorderId);
			}
			if (!tradeTypeCheck(requestMap)) {// 判断交易码是否正确
				resultMap.put("returnCode", "0004");
				return resultMap;
			}
			resultMap = verifyPayOrderStatus(tradeParam);// 判断pay_order中相关条件与上送字段是否冲突
			if (!"0000".equals(resultMap.get("returnCode"))) {
				return resultMap;
			}
			tradeParam.put("orderAmt", resultMap.get("orderAmt"));// 原始支付订单总金额,后面使用
			if(!verifyPayOrderPayAmtStatus(tradeParam)){//考虑加入拼付的情况下，orderId、secOrderId支付订单，在支持部分退款的情况下，要单独判断可退金额是否足够(支持医保支付也部分退)
				resultMap.put("returnCode", "2007");
				return resultMap;
			}
			if (!verifyPayOrderPayStatus(tradeParam)) {// 判断pay_order_pay表中是否有未完成确认状态的退费记录,不允许创建新的退费订单
				resultMap.put("returnCode", "2010");
				return resultMap;
			}
			if (!generatePayOrderPay(tradeParam)) {// 生成pay_order_pay记录
				resultMap.put("returnCode", "0021");
				return resultMap;
			}
			if (!medicalOrderDeal(tradeParam)) {// 处理医保订单金额
				resultMap.put("returnCode", "0021");
				return resultMap;
			}
			if (tradeParam.containsKey("spellFlag")) {// 拼付标志获取
				if (!generateSecPayOrderPay(tradeParam)) {// 生成pay_order_pay记录
					resultMap.put("returnCode", "0021");
					return resultMap;
				}
				if (!medicalSecOrderDeal(tradeParam)) {// 处理医保订单金额
					resultMap.put("returnCode", "0021");
					return resultMap;
				}
			}
			Map<String, Object> outdata = new HashMap<String, Object>();// data节点
			outdata.put("orderId", tradeParam.get("orderId"));
			outdata.put("refundorderId", tradeParam.get("refundorderId"));
			outdata.put("refundAmt", tradeParam.get("refundAmt"));
			outdata.put("transDate", DateUtils.getDateTime("yyyyMMdd"));
			if (tradeParam.containsKey("secOrderId"))
				outdata.put("secOrderId", tradeParam.get("secOrderId"));
			if (tradeParam.containsKey("secRefundorderId"))
				outdata.put("secRefundorderId", tradeParam.get("secRefundorderId"));
			if (tradeParam.containsKey("planAmt"))
				outdata.put("planAmt", tradeParam.get("planAmt"));
			if (tradeParam.containsKey("personAmt"))
				outdata.put("personAmt", tradeParam.get("personAmt"));
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
			if (!VerifyMapUtils.notEmpty(tradeParam,
					new String[] { "merchantId", "chanelType", "orderId", "refundFlag" }))
				return false;
		}
		return true;
	}

	/**
	 * 判断数据库是否插入了相应的订单记录 且支付状态为确认
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> existOrder(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectOrderComplete",
				tradeParam);
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
			String orderTrace = Convert.convert(String.class, oo.get("order_trace"));
			String firstOrderAmt = Convert.convert(String.class, oo.get("order_amt"));// pay_order_pay中自费金额
			String firstAcctPay = Convert.convert(String.class, oo.get("acct_pay"));// pay_medical_fee中医保个人账户
			String firstFundPaySumamtfrom = Convert.convert(String.class, oo.get("fund_pay_sumamtfrom"));// pay_medical_fee中基金统筹账户
			tradeParam.put("realMerchantId", merchantId);
			tradeParam.put("payType", payType);
			tradeParam.put("thirdId", thirdId);
			tradeParam.put("orderTrace", orderTrace);
			tradeParam.put("firstOrderAmt", firstOrderAmt);
			tradeParam.put("firstAcctPay", firstAcctPay);
			tradeParam.put("firstFundPaySumamtfrom", firstFundPaySumamtfrom);
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
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectSecOrderComplete",
				tradeParam);
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
			String secOrderAmt = Convert.convert(String.class, oo.get("order_amt"));// pay_order_pay中自费金额
			String secAcctPay = Convert.convert(String.class, oo.get("acct_pay"));// pay_medical_fee中医保个人账户
			String secFundPaySumamtfrom = Convert.convert(String.class, oo.get("fund_pay_sumamtfrom"));// pay_medical_fee中基金统筹账户
			tradeParam.put("realSecMerchantId", merchantId);
			tradeParam.put("secPayType", payType);
			tradeParam.put("secThirdId", thirdId);
			tradeParam.put("secOrderAmt", secOrderAmt);// 应该为0
			tradeParam.put("secAcctPay", secAcctPay);
			tradeParam.put("secFundPaySumamtfrom", secFundPaySumamtfrom);
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
		if (!"0005".equals(tradeType)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断pay_order中相关条件与上送字段是否冲突
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> verifyPayOrderStatus(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
		String refundAmt = Convert.convert(String.class, tradeParam.get("refundAmt"));
		String refundFlag = Convert.convert(String.class, tradeParam.get("refundFlag"));
		resultMap.put("returnCode", "0000");
		List<Map<String, Object>> payOrderList = sqlService.selectList("pay_order", "selectOrderPay", tradeParam);
		if (payOrderList.size() != 1) {// pay_order没有对应记录
			resultMap.put("returnCode", "2001");
			return resultMap;
		}
		Map<String, Object> payOrderInfo = payOrderList.get(0);
		String thisOrderAmt = Convert.convert(String.class, payOrderInfo.get("order_amt"));
		String thisRefundAmt = Convert.convert(String.class, payOrderInfo.get("refund_amt"));
		String thisPayStatus = Convert.convert(String.class, payOrderInfo.get("pay_status"));
		String thisRefundStatus = Convert.convert(String.class, payOrderInfo.get("refund_status"));
		resultMap.put("orderAmt", thisOrderAmt);// 将原始订单总金额传出去
		if (!"2".equals(thisPayStatus)) {// 支付状态不正常
			resultMap.put("returnCode", "2006");
			return resultMap;
		}
		if ("1".equals(thisRefundStatus) || "3".equals(thisRefundStatus)) {// 已全额退款，已冲正
			resultMap.put("returnCode", "2002");
			return resultMap;
		}
		if ("1".equals(refundFlag)) {
			if (!"0".equals(thisRefundAmt)) {// 冲正已退金额必须为0
				resultMap.put("returnCode", "2011");
				return resultMap;
			}
		}
		String thisCanRefund = MoneyUtils.moneySub(thisOrderAmt, thisRefundAmt);// 还可以退的总金额

		//非拼付的情况下，正常情况报文上送refundAmt为自费金额。
		//thirdId=06时，refundAmt应该为0，planAmt、personAmt有相应的值；thirdId为其他时，refundAmt有值，其他两个字段为0
		//戴庄不论thirdId为什么类型，只有refundAmt有值
		if(tradeParam.containsKey("planAmt")) {
			String planAmt = Convert.convert(String.class, tradeParam.get("planAmt"));
			refundAmt = MoneyUtils.moneyAdd(refundAmt, planAmt);
		}
		if(tradeParam.containsKey("personAmt")) {
			String personAmt = Convert.convert(String.class, tradeParam.get("personAmt"));
			refundAmt = MoneyUtils.moneyAdd(refundAmt, personAmt);
		}
		if (!MoneyUtils.moneyComp(thisCanRefund, refundAmt)) {// 订单总的可退金额不足
			resultMap.put("returnCode", "2007");
			return resultMap;
		}
		return resultMap;
	}

	/**
	 * 考虑加入拼付的情况下，orderId、secOrderId支付订单，在支持部分退款的情况下，要单独判断可退金额是否足够(支持医保支付也部分退)
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean verifyPayOrderPayAmtStatus(Map<String, Object> tradeParam) throws Exception {
		String canRefund = null;//还可以退的金额
		// 查询同一个orderTrace下面所有确认退费成功的记录
		List<Map<String, Object>> allConfirmedRefundList = sqlService.selectList("pay_order_pay",
				"selectAllConfirmRefundOrder", tradeParam);
		if (tradeParam.containsKey("spellFlag")) {// 拼付情况		
			String firstOrderAmt = Convert.convert(String.class, tradeParam.get("firstOrderAmt"));// orderId支付订单总金额（自费）
			String firstHaveRefundOrderAmt = calRefundSettleAmt(allConfirmedRefundList,
					Convert.convert(String.class, tradeParam.get("payType")));// 已退费自费总金额
			canRefund = MoneyUtils.moneySub(firstOrderAmt, firstHaveRefundOrderAmt);
			if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("refundAmt"))))
				return false;
			String secAcctPay = Convert.convert(String.class, tradeParam.get("secAcctPay"));// 拼付订单，医保个账支付总金额
			String secHaveRefundAcctPay = calRefundPersonAmt(allConfirmedRefundList);// 已退医保个人账户
			canRefund = MoneyUtils.moneySub(secAcctPay, secHaveRefundAcctPay);
			if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("personAmt"))))
				return false;
			String secFundPaySumamtfrom = Convert.convert(String.class, tradeParam.get("secFundPaySumamtfrom"));// 拼付订单，医保基金统筹总金额
			String secHaveRefundFundPaySumamtfrom = calRefundPlanAmt(allConfirmedRefundList);// 已退医保基金统筹
			canRefund = MoneyUtils.moneySub(secFundPaySumamtfrom, secHaveRefundFundPaySumamtfrom);
			if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("planAmt"))))
				return false;
		} else {//非拼付
			if("06".equals(tradeParam.get("thirdId"))) {//全部医保独立支付
				String firstAcctPay = Convert.convert(String.class, tradeParam.get("firstAcctPay"));// orderId订单 ，医保个账支付总金额
				String firstHaveRefundAcctPay = calRefundPersonAmt(allConfirmedRefundList);// orderId订单 ，已退医保个人账户
				canRefund = MoneyUtils.moneySub(firstAcctPay, firstHaveRefundAcctPay);
				if(tradeParam.containsKey("personAmt")) {//正常情况
					if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("personAmt"))))
						return false;
				}else {//戴庄特殊情况
					if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("refundAmt"))))
						return false;
				}
			}else {//全部自费支付
				String firstOrderAmt = Convert.convert(String.class, tradeParam.get("firstOrderAmt"));// orderId订单支付总金额（自费）
				String firstHaveRefundOrderAmt = calRefundSettleAmt(allConfirmedRefundList,
						Convert.convert(String.class, tradeParam.get("payType")));// 已退费自费总金额
				canRefund = MoneyUtils.moneySub(firstOrderAmt, firstHaveRefundOrderAmt);
				if(!MoneyUtils.moneyComp(canRefund, Convert.convert(String.class, tradeParam.get("refundAmt"))))
					return false;
			}
		}
		return true;
	}

	/**
	 * 计算已退款金额(自费)
	 * 
	 * @param allConfirmedRefundList
	 * @param payType
	 * @return
	 * @throws Exception
	 */
	private String calRefundSettleAmt(List<Map<String, Object>> allConfirmedRefundList, String payType)
			throws Exception {
		String refundAmt = "0";
		for (Object o : allConfirmedRefundList) {
			Map<Object, Object> oo = (Map) o;
			String thisPayType = Convert.convert(String.class, oo.get("pay_type_id"));
			if (payType.equals(thisPayType)) {
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("order_amt")));
			}
		}
		return refundAmt;
	}

	/**
	 * 计算已退款金额(医保个账)
	 * 
	 * @param allConfirmedRefundList
	 * @return
	 * @throws Exception
	 */
	private String calRefundPersonAmt(List<Map<String, Object>> allConfirmedRefundList) throws Exception {
		String refundAmt = "0";
		for (Object o : allConfirmedRefundList) {
			Map<Object, Object> oo = (Map) o;
			String thirdId = Convert.convert(String.class, oo.get("third_id"));
			if ("06".equals(thirdId)) {
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("acct_pay")));
			}
		}
		return refundAmt;
	}

	/**
	 * 计算已退款金额(医保基金统筹)
	 * 
	 * @param allConfirmedRefundList
	 * @return
	 * @throws Exception
	 */
	private String calRefundPlanAmt(List<Map<String, Object>> allConfirmedRefundList) throws Exception {
		String refundAmt = "0";
		for (Object o : allConfirmedRefundList) {
			Map<Object, Object> oo = (Map) o;
			String thirdId = Convert.convert(String.class, oo.get("third_id"));
			if ("06".equals(thirdId)) {
				refundAmt = MoneyUtils.moneyAdd(refundAmt, Convert.convert(String.class, oo.get("fund_pay_sumamt")));
			}
		}
		return refundAmt;
	}

	/**
	 * 判断pay_order_pay表中是否有未完成确认状态的退费记录
	 * 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	private boolean verifyPayOrderPayStatus(Map<String, Object> tradeParam) throws Exception {
		List<Map<String, Object>> payOrderPayList = sqlService.selectList("pay_order_pay", "selectUncheck", tradeParam);
		if (payOrderPayList.size() > 0) {
			return false;
		}
		return true;
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
	 * 生成pay_order_pay记录
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean generatePayOrderPay(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("order_id", tradeParam.get("refundorderId"));
			paramMap.put("merchant_id", tradeParam.get("realMerchantId"));
			paramMap.put("hospital_id", tradeParam.get("hospitalZoneCode"));
			paramMap.put("order_trace", tradeParam.get("orderTrace"));
			paramMap.put("trans_type", -1);
			paramMap.put("third_id", tradeParam.get("thirdId"));
			paramMap.put("pay_type_id", tradeParam.get("payType"));
			paramMap.put("channel_id", tradeParam.get("chanelType"));
			paramMap.put("create_date", DateUtils.getDateTime("yyyyMMdd"));
			paramMap.put("create_time", DateUtils.getDateTime("HHmmss"));
			paramMap.put("refund_reason", tradeParam.get("refundReason"));
			paramMap.put("status", "0");
			if ("06".equals(Convert.convert(String.class, tradeParam.get("thirdId")))) {
				paramMap.put("order_amt", 0);
			} else {
				paramMap.put("order_amt", Convert.convert(Integer.class, tradeParam.get("refundAmt")));
			}
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
	 * 处理医保订单金额
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
				paramMap.put("order_id", tradeParam.get("refundorderId"));
				paramMap.put("medfee_sumamt", Convert.convert(Integer.class, tradeParam.get("orderAmt")));// 订单总金额
				paramMap.put("fund_pay_sumamt", 0);// 统筹金额(戴庄没有送这个字段)，默认填0
				paramMap.put("acct_pay", Convert.convert(Integer.class, tradeParam.get("refundAmt")));// 个人医保账户余额支出(戴庄退费字段没有和支付一样定义)，此时refundAmt代表
				paramMap.put("psn_cash_pay", 0);// 个人自费支出
				if (tradeParam.containsKey("personAmt"))
					paramMap.put("acct_pay", Convert.convert(Integer.class, tradeParam.get("personAmt")));// 个人医保账户余额支出，覆盖戴庄逻辑
				if (tradeParam.containsKey("planAmt"))
					paramMap.put("fund_pay_sumamt", Convert.convert(Integer.class, tradeParam.get("planAmt")));// 统筹金额，覆盖戴庄逻辑
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
			paramMap.put("order_id", tradeParam.get("secRefundorderId"));
			paramMap.put("merchant_id", tradeParam.get("realSecMerchantId"));
			paramMap.put("hospital_id", tradeParam.get("hospitalZoneCode"));
			paramMap.put("order_trace", tradeParam.get("orderTrace"));
			paramMap.put("trans_type", -1);
			paramMap.put("third_id", tradeParam.get("secThirdId"));
			paramMap.put("pay_type_id", tradeParam.get("secPayType"));
			paramMap.put("channel_id", tradeParam.get("chanelType"));
			paramMap.put("order_amt", 0);
			paramMap.put("create_date", DateUtils.getDateTime("yyyyMMdd"));
			paramMap.put("create_time", DateUtils.getDateTime("HHmmss"));
			paramMap.put("refund_reason", tradeParam.get("refundReason"));
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
	 * 处理医保订单金额
	 * 
	 * @param tradeParam
	 * @return
	 * @throws Exception
	 */
	private boolean medicalSecOrderDeal(Map<String, Object> tradeParam) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			String thirdId = Convert.convert(String.class, tradeParam.get("thirdId"));
			if ("06".equals(thirdId)) {
				paramMap.put("order_id", tradeParam.get("secRefundorderId"));
				paramMap.put("medfee_sumamt", Convert.convert(Integer.class, tradeParam.get("orderAmt")));// 订单总金额
				paramMap.put("fund_pay_sumamt", Convert.convert(Integer.class, tradeParam.get("planAmt")));// 统筹金额
				paramMap.put("acct_pay", Convert.convert(Integer.class, tradeParam.get("personAmt")));// 个人医保账户余额支出
				paramMap.put("psn_cash_pay", 0);// 个人自费支出
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

}
