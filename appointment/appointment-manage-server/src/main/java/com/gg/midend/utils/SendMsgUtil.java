package com.gg.midend.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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


    /**
     * 描述：停诊通知患者
     * @param model 模板id
     * @param sourceDate 号源日期 年月日
     * @param status 同步结果
     */
    public static void notify(String model, String mobiles, List<String> sendDate) {
        // 构造请求报文
        Map<String, Object> reqBodyMap = new HashMap<>();
        reqBodyMap.put("mobiles", mobiles);
        reqBodyMap.put("templateType", model);
        reqBodyMap.put("params", JSON.toJSONString(sendDate));

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
