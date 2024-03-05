package com.gg.midend.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：请求短信平台发送模板短信的工具类
 */
@Component
public class SendMsgUtil {

    /* 短信接口地址 */
    private static String requestUrl;

    @Value("${msg-config.request-url}")
    public void setRequestUrl(String requestUrl) {
        SendMsgUtil.requestUrl = requestUrl;
    }

    /* 同步号源后，需要通知的收信人号码 */
    private static String mobiles;

    @Value("${msg-config.receive-phone}")
    public void setMobiles(String mobiles) {
        SendMsgUtil.mobiles = mobiles;
    }

    /**
     * 描述：同步号源后，通知收信人
     * @param sourceDate 号源日期 年月日
     * @param status 同步结果
     */
    public static void notify(String sourceDate, String status) {
        // 构造请求报文
        Map<String, Object> reqBodyMap = new HashMap<>();
        reqBodyMap.put("mobiles", mobiles);
        reqBodyMap.put("templateType", "sync");
        ArrayList<String> params = new ArrayList<>();
        params.add(sourceDate);
        params.add(status);
        reqBodyMap.put("params", JSON.toJSONString(params));

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("header", new HashMap<String, Object>());
        reqMap.put("body", reqBodyMap);

        // 入参转 json 格式
        String reqJson = JSON.toJSONString(reqMap);
        GlobalConfig.log_api.info("请求 SendMsg 的入参: " + reqJson);

        // 发送 post 请求
        String respJson = HttpUtil.post(requestUrl, reqJson);
        GlobalConfig.log_api.info("请求 SendMsg 的出参: " + respJson);
    }
}
