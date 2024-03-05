package com.gg.midend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gg.midend.config.GlobalConfig;
import com.gg.midend.service.app.OrderCreateService;
import com.gg.midend.service.app.OrderPayService;
import com.gg.midend.service.app.OrderQueryService;
import com.gg.midend.service.app.OrderRefundCreateService;
import com.gg.midend.service.app.OrderRefundQueryService;
import com.gg.midend.service.app.OrderRefundService;
import com.gg.midend.service.app.OrderRevokeService;

import cn.hutool.core.convert.Convert;

@RestController
@EnableAsync
@RequestMapping(value = "/pay-app-api", method = RequestMethod.POST)
public class PayController {

	@Autowired
	private OrderCreateService orderCreateService;// 订单创建(模式1)
	@Autowired
	private OrderPayService orderPayService;// 订单创建(模式2)
	@Autowired
	private OrderQueryService orderQueryService;// 订单支付结果确认
	@Autowired
	private OrderRefundCreateService orderRefundCreateService;// 退费订单生成
	@Autowired
	private OrderRefundService orderRefundService;// 订单退费
	@Autowired
	private OrderRefundQueryService orderRefundQueryService;// 退费查询
	@Autowired
	private OrderRevokeService orderRevokeService;// 订单撤销

	/**
	 * 订单创建(模式1：客户发起支付)
	 * 
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createOrder", method = RequestMethod.POST)
	public Object createOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		GlobalConfig.log_api.info("createOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0001");
		GlobalConfig.log_api.info("createOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderCreateService.createOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("createOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("createOrder", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 订单创建(模式2：商户发起支付)
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/payOrder", method = RequestMethod.POST)
	public Object payOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		GlobalConfig.log_api.info("payOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0002");
		GlobalConfig.log_api.info("payOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderPayService.payOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("payOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("payOrder", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 订单支付结果确认
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
	public Object queryOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {	
		GlobalConfig.log_api.info("queryOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0003");
		GlobalConfig.log_api.info("queryOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderQueryService.queryOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("queryOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("queryOrder", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 退费订单创建
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createRefundOrder", method = RequestMethod.POST)
	public Object createRefundOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {	
		GlobalConfig.log_api.info("createRefundOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0005");
		GlobalConfig.log_api.info("createRefundOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderRefundCreateService.createRefundOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("createRefundOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("createRefundOrder", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 订单退款
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/refundOrder", method = RequestMethod.POST)
	public Object refundOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		GlobalConfig.log_api.info("refundOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0006");
		GlobalConfig.log_api.info("refundOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderRefundService.refundOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("refundOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("refundOrder", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 订单退款结果确认
	 * @param request
	 * @param response
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/refundQuery", method = RequestMethod.POST)
	public Object refundQuery(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {	
		GlobalConfig.log_api.info("refundQuery 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0007");
		GlobalConfig.log_api.info("refundQuery 处理后请求：{}", paramMap);
		try {
			resultMap = orderRefundQueryService.refundQuery(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("refundQuery Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("refundQuery", resultMap);
		}
		return resultMap;
	}
	
	/**
	 * 订单撤销
	 * @param request
	 * @param response
	 * @param jsonParam
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/revokeOrder", method = RequestMethod.POST)
	public Object revokeOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> paramMap) throws Exception {
		GlobalConfig.log_api.info("revokeOrder 收到原始请求：{}", paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();// 返回给客户端的结果
		paramMap = preRequestData(paramMap, "0004");
		GlobalConfig.log_api.info("revokeOrder 处理后请求：{}", paramMap);
		try {
			resultMap = orderRevokeService.revokeOrder(paramMap);
		} catch (Exception e) {
			GlobalConfig.log_api.info("revokeOrder Exception：{}", e.toString());
			resultMap.put("returnCode", "9999");// 系统异常
			return resultMap;
		} finally {
			if (!resultMap.containsKey("returnInfo")) {
				resultMap.put("returnInfo", getErrorInfo((String) resultMap.get("returnCode")));
			}
			resultMap = retBack("revokeOrder", resultMap);
		}
		return resultMap;
	}

	/**
	 * 将中台header、body格式报文转换成 原有报文格式
	 * 
	 * @param paramMap
	 *            header、body报文
	 * @param tradeType
	 *            交易类型
	 * @return
	 */
	private Map<String, Object> preRequestData(Map<String, Object> paramMap, String tradeType) {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		bodyMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));
		reqMap.put("tradeType", tradeType);
		reqMap.put("tradeParam", bodyMap);
		return reqMap;
	}

	/**
	 * 组中台标准返回报文
	 * 
	 * @param functionName
	 * @param code
	 * @param msg
	 * @param map
	 * @return
	 */
	private Map<String, Object> retBack(String functionName, Map<String, Object> resultMap) {
		Map<String, Object> headerMap = new HashMap<String, Object>();
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		headerMap.put("returnCode", resultMap.get("returnCode"));
		headerMap.put("returnMsg", resultMap.get("returnInfo"));
		headerMap.put("version", "1.0.0");
		if (resultMap.containsKey("data")) {
			dataMap = Convert.toMap(String.class, Object.class, resultMap.get("data"));
			bodyMap.putAll(dataMap);
		}
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("header", headerMap);
		retMap.put("body", bodyMap);
		GlobalConfig.log_api.info("{}返回请求：{}", functionName, retMap);
		return retMap;
	}

	/**
	 * 获取错误提示
	 * 
	 * @param code
	 * @return
	 */
	private String getErrorInfo(String code) {
		String errorInfo;
		switch (code) {
		case "0000":
			errorInfo = "交易成功";
			break;
		case "0001":
			errorInfo = "支付结果查询失败，继续查询";
			break;
		case "0002":
			errorInfo = "支付确认失败";
			break;
		case "0003":
			errorInfo = "签名错误";
			break;
		case "0004":
			errorInfo = "交易码错误";
			break;
		case "0005":
			errorInfo = "参数错误";
			break;
		case "0006":
			errorInfo = "支付类型和交易不匹配";
			break;
		case "0007":
			errorInfo = "创建订单失败";
			break;
		case "0008":
			errorInfo = "获取支付二维码失败";
			break;
		case "0009":
			errorInfo = "取消订单失败";
			break;
		case "0010":
			errorInfo = "退款中，继续查询退款状态";
			break;
		case "0011":
			errorInfo = "订单退款查询失败，请重新查询";
			break;
		case "0012":
			errorInfo = "退款失败，不用继续查询";
			break;
		case "0014":
			errorInfo = "订单未创建";
			break;
		case "0015":
			errorInfo = "请勿重复确认订单退款结果";
			break;
		case "0016":
			errorInfo = "订单已存在";
			break;
		case "0017":
			errorInfo = "订单号格式不正确";
			break;
		case "0020":
			errorInfo = "支付中，继续查询支付状态";
			break;
		case "0021":
			errorInfo = "创建退费订单失败";
			break;
		case "1001":
			errorInfo = "请检查商户渠道开通状态";
			break;
		case "1002":
			errorInfo = "请检查商户号";
			break;
		case "1003":
			errorInfo = "请检查商户支付方式开通状态";
			break;
		case "1004":
			errorInfo = "订单类型未查询到";
			break;
		case "1005":
			errorInfo = "请检查商户信息";
			break;
		case "1006":
			errorInfo = "请检查医院代码";
			break;
		case "1007":
			errorInfo = "请检查thirdid";
			break;
		case "1008":
			errorInfo = "订单中金额上送不合理";
			break;
		case "1009":
			errorInfo = "非法订单号";
			break;
		case "1010":
			errorInfo = "报文上送字段缺失";
			break;
		case "1011":
			errorInfo = "拼付支付类型thirdId不正确";
			break;
		case "1012":
			errorInfo = "非法退费订单号";
			break;
		case "2001":
			errorInfo = "没有查到订单信息";
			break;
		case "2002":
			errorInfo = "该订单状态已退款或已冲正";
			break;
		case "2003":
			errorInfo = "退款金额与原订单金额不一致";
			break;
		case "2004":
			errorInfo = "删除已存在退款订单失败";
			break;
		case "2005":
			errorInfo = "该支付方式未开通退款功能";
			break;
		case "2006":
			errorInfo = "当前订单状态不可退款";
			break;
		case "2007":
			errorInfo = "该订单可退款金额不足";
			break;
		case "2008":
			errorInfo = "退费订单不存在";
			break;
		case "2009":
			errorInfo = "该退费订单已关闭或已完成";
			break;
		case "2010":
			errorInfo = "还有未处理的退费订单，请到后台处理后再进行退费";
			break;
		case "2011":
			errorInfo = "已退款金额不符合冲正要求";
			break;
		default:
			errorInfo = "其他错误";
			break;
		}
		return errorInfo;
	}

}
