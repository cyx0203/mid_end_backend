package com.gg.midend.controller;

import com.gg.core.controller.BaseController;
import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.core.util.ParamUtil;
import com.gg.midend.HttpRequest.ConnHttp;
import com.gg.midend.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class TransController extends BaseController {
    @Autowired
    private ConnHttp connHttp;

    //退款接口地址
    @Value("${refund.refundUrl}")
    String refundUrl;

    //退款接口，连支付平台
    @RequestMapping({"/refund"})
    public Object refund(@RequestBody Map paramMap) throws ExceptionCenter {
        Map header = new HashMap();
        Map body = new HashMap();
        header.put("timestamp", DateUtil.getNowDate("yyyy-MM-dd hh:mm:ss"));
        header.put("custId", "manage");

        body.put("refundOrderId", DateUtil.getNowDate("yyyyMMddhhmmss") + (int) ((Math.random() * 9 + 1) * 100000) + "");
        body.put("orderDate", DateUtil.getNowDate("yyyyMMdd"));
        body.put("orderId", paramMap.get("orderId"));
        body.put("wxId", paramMap.get("wxId"));
        body.put("payAmt", paramMap.get("payAmt"));
        body.put("totalAmt", paramMap.get("totalAmt"));
        body.put("payOrderId", paramMap.get("payOrderId"));
        body.put("remark", paramMap.get("remark"));
        body.put("refundOperId", paramMap.get("account"));

        Map map = new HashMap();
        map.put("header", header);
        map.put("body", body);
        map.put("sign", "");

        try {
            Map rspReturnOrderMap = (Map) connHttp.getUrlRet(refundUrl, map, MediaType.APPLICATION_JSON);
            Map headerMap = (Map) rspReturnOrderMap.get("header");
            if (!Objects.equals("0", headerMap.get("returnCode"))) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, (String) headerMap.get("returnMsg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "交易异常");
        }
        return success();
    }
}
