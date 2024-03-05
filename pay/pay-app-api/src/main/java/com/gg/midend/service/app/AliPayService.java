package com.gg.midend.service.app;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gg.midend.config.GlobalParam;
import com.gg.midend.service.http.AliPayRequest;
import com.gg.midend.utils.JsonUtils;
import com.gg.midend.utils.PayUtils;

import cn.hutool.core.convert.Convert;


@Service
public class AliPayService {
	
	
	@Autowired
	private AliPayRequest aliRequest;
	
	 /** 国光支付宝网关URL*/    
    @Value("${SysConstants.AliPayGate_URL}")
    private  String AliPayGate_URL;   
    /** 国光支付宝网关MD5 KEY*/
    @Value("${SysConstants.AliPayGate_KEY}")
    private  String AliPayGate_KEY;
	
	/**
	 * 获取二维码
	 * @param data
	 * @return
	 */
	public Map<String, Object> f2fScanQrCodePay(Map<String, Object> data) {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("body", " ");//要加个空格
			tradeparammap.put("subject", data.get("subject"));
			tradeparammap.put("totalAmount", (String) data.get("settleAmt"));
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("storeId", " ");//门店号，设备号-要加个空格
			tradeparammap.put("pid", (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			req.put("tradeType", "F2FScanQrCodePay");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		} catch (Exception e) {
		}
		return resp;
	}
	
	/**
	 * 条码枪扫支付码
	 * @param data
	 * @return
	 */
	public Map<String, Object> f2fBarCodePay(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("pid",  (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			tradeparammap.put("scene", "bar_code");
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("subject", data.get("subject"));
			tradeparammap.put("totalAmount", (String) data.get("settleAmt"));
			tradeparammap.put("authCode", (String) data.get("thirdAuthCode"));
			tradeparammap.put("storeId", " ");
			tradeparammap.put("body", " ");
			req.put("tradeType", "F2FBarCodePay");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 轮询支付结果
	 */
	public Map<String, Object> f2fQueryPayResultForLoop(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("pid", (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			req.put("tradeType", "F2FQueryPayResultForLoop");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 取消支付
	 * @param data
	 * @return
	 */
	public Map<String, Object> f2fCancelPay(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("pid", (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			req.put("tradeType", "F2FCancelPay");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {
			
		}
		return resp;
	}
	
	/**
	 * 退款
	 */
	public Map<String, Object> f2fRefundTradePay(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("outRequestNo", (String) data.get("refundorderId"));
			tradeparammap.put("pid", (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			tradeparammap.put("subject", data.get("subject"));
			tradeparammap.put("totalAmount", (String) data.get("refundAmt"));
			tradeparammap.put("storeId", " ");//门店号，设备号			
			req.put("tradeType", "F2FRefundTradePay");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 退款查询
	 */
	public Map<String, Object> f2fRefundTradeQuery(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("outRequestNo", (String) data.get("refundorderId"));
			tradeparammap.put("pid", (String)data.get("realMerchantId"));
			tradeparammap.put("appid", (String)data.get("appId"));
			req.put("tradeType", "F2FRefundTradeQuery");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.aligenerateSignature(tradeparammap, AliPayGate_KEY));
			resp = aliRequest.dopost(AliPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {		
		}
		return resp;
	}
	
	/**
	 * alipay appid获取
	 * 
	 * @return
	 * @throws Exception
	 */
	public String alipayAppIdGet(Map<String, Object> tradeParam) throws Exception {
		String realMerchantId = Convert.convert(String.class, tradeParam.get("realMerchantId"));
		String thisAppId = null;
		for (Object o : GlobalParam.alipayParamList) {
			Map<Object, Object> oo = (Map) o;
			String thisRealMerchantId = Convert.convert(String.class, oo.get("merchant_id"));
			thisAppId = Convert.convert(String.class, oo.get("app_id"));
			if (realMerchantId.equalsIgnoreCase(thisRealMerchantId)) {
				thisRealMerchantId = Convert.convert(String.class, oo.get("merchant_id"));
				break;
			}
		}
		return thisAppId;
	}

}
