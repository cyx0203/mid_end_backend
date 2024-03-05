package com.gg.midend.service.app;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gg.midend.service.http.WechatPayRequest;
import com.gg.midend.utils.DateUtils;
import com.gg.midend.utils.JsonUtils;
import com.gg.midend.utils.PayUtils;


@Service
public class WechatPayService {
	
	@Autowired
	private WechatPayRequest wechatrequest;

	 /** 国光微信网关URL*/    
    @Value("${SysConstants.WechatPayGate_URL}")
    private  String WechatPayGate_URL;
    /** 国光微信网关MD5 KEY*/
    @Value("${SysConstants.WechatPayGate_KEY}")
    private  String WechatPayGate_KEY;
    
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
			tradeparammap.put("merId", (String)data.get("realMerchantId"));			
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("body", "微信支付");
			tradeparammap.put("tradeType", "NATIVE");
			tradeparammap.put("attach",  "{\"route\":\"his\"}");
			tradeparammap.put("detail", (String) data.get("orderType"));
			tradeparammap.put("totalAmount", (String) data.get("settleAmt"));
			tradeparammap.put("tradeTime", DateUtils.getDateTime("yyyyMMddHHmmss"));
			tradeparammap.put("channel", (String) data.get("chanelType"));
			tradeparammap.put("trdType", (String) data.get("orderType"));
			tradeparammap.put("payType", (String) data.get("payType"));
			System.out.println("tradeparammap="+tradeparammap);
			req.put("tradeType", "TradeCreate");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
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
			tradeparammap.put("merId", (String)data.get("realMerchantId"));	
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("channel", (String) data.get("chanelType"));
			tradeparammap.put("totalAmount", (String) data.get("settleAmt"));
			tradeparammap.put("tradeTime", DateUtils.getDateTime("yyyyMMddHHmmss"));
			tradeparammap.put("authCode", (String) data.get("thirdAuthCode"));
			tradeparammap.put("body", "微信支付");
			tradeparammap.put("detail", (String) data.get("orderType"));
			tradeparammap.put("attach",  "{\"route\":\"his\"}");
			req.put("tradeType", "MicroPay");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 查询订单支付结果
	 */
	public Map<String, Object> OrderQuery(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("merId", (String)data.get("realMerchantId"));
			tradeparammap.put("outTradeNo", (String)data.get("orderId"));
			tradeparammap.put("channel", (String)data.get("chanelType"));
			req.put("tradeType", "OrderQuery");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 取消支付
	 * @param data
	 * @return
	 */
	public Map<String, Object> CloseOrder(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("merId", (String)data.get("realMerchantId"));
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("channel", (String)data.get("chanelType"));
			req.put("tradeType", "CloseOrder");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {
			
		}
		return resp;
	}
	
	/**
	 * 退款
	 */
	public Map<String, Object> Refund(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("merId", (String)data.get("realMerchantId"));
			tradeparammap.put("outTradeNo", (String) data.get("refundorderId"));
			tradeparammap.put("channel", (String)data.get("chanelType"));
			tradeparammap.put("origOutTradeNo", (String)data.get("orderId"));
			tradeparammap.put("orderFee", (String) data.get("orderAmt"));//原订单金额
			tradeparammap.put("refundFee", (String) data.get("refundAmt"));
			tradeparammap.put("refundDesc", (String) data.get("refundReason"));
			req.put("tradeType", "Refund");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {			
		}
		return resp;
	}
	
	/**
	 * 退款查询
	 */
	public Map<String, Object> RefundQuery(Map<String, Object> data) {
		// TODO Auto-generated method stub
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> tradeparammap = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();
		try {
			tradeparammap.put("merId", (String)data.get("realMerchantId"));
			tradeparammap.put("outTradeNo", (String) data.get("orderId"));
			tradeparammap.put("outRefundNo", (String) data.get("refundorderId"));
			tradeparammap.put("channel", (String)data.get("chanelType"));
			System.out.println("tradeparammap = "+tradeparammap);
			req.put("tradeType", "RefundQuery");
			req.put("tradeParam", tradeparammap);
			req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, WechatPayGate_KEY));
			resp = wechatrequest.dopost(WechatPayGate_URL, JsonUtils.MapToJson(req));
		}catch(Exception e) {		
		}
		return resp;
	}

}
