package com.gg.midend.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.alibaba.fastjson.JSON;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 描述：源启外联平台的工具类
 */
@Component
public class YQRequestUtil {

    /* 源启外联接口地址 */
    private static String requestUrl;

    @Value("${yq-config.request-url}")
    public void setRequestUrl(String requestUrl) {
        YQRequestUtil.requestUrl = requestUrl;
    }

    /**
     * 描述：外联平台-退号退费（异步处理）
     * 
     * @param functionName 方法名
     * @param reqJson      请求参数
     */
    public static void requestYQ(final String functionName, final Map<String, Object> reqMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> requestData = new LinkedHashMap<>();

                    // 构造请求报文
                    Map<String, Object> reqBodyMap = new HashMap<>();
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("terminal_mark", "");
                    infoMap.put("order_id", reqMap.get("busOrderNo"));
                    infoMap.put("operator", "A0006");
                    infoMap.put("channel_refund_serial_number", "");
                    infoMap.put("extend_params", "");
                    infoMap.put("cancel_reason", "");
                    infoMap.put("return_type", "");

                    Map<String, Object> extMap = new HashMap<>();
                    extMap.put("pass_through", "");

                    reqBodyMap.put("info", infoMap);
                    reqBodyMap.put("ext_info", extMap);
                    // 组装参数
                    JSONObject reqJson = JSONUtil.createObj().set("sign_type", "md5").set("sign", "")
                            .set("charset", "utf-8").set("method", functionName).set("version", "1.0")
                            .set("language", "zh_CN").set("sys_track_code", String.valueOf(System.currentTimeMillis()))
                            .set("app_id", "702755e56d664798bcf688df7f97b401").set("enterprise_id", "420115002")
                            .set("timestamp", String.valueOf(System.currentTimeMillis())).set("encrypt_type", "AES");

                    // 入参转 json 格式
                    JSONObject requset = JSONUtil.createObj().set("head", reqJson).set("body",
                            YQSign.aesEncode(JSON.toJSONString(reqBodyMap)));

                    reqJson.set("sign", YQSign.sign(JSON.toJSONString(requset)));
                    requset.set("head", reqJson);

                    requestData.put("head", reqJson);
                    requestData.put("body", YQSign.aesEncode(JSON.toJSONString(reqBodyMap)));

                    GlobalConfig.log_api.info("请求 源启（" + reqMap.get("busOrderNo") + "） 的入参: " + requestData);
                    // 发送 post 请求
                    String respJson = HttpUtil.post(requestUrl, JSON.toJSONString(requestData));
                    GlobalConfig.log_api.info("请求 源启（" + reqMap.get("busOrderNo") + "） 的出参: " + respJson);
                } catch (Exception e) {
                    GlobalConfig.log_api.info("请求 源启（" + reqMap.get("busOrderNo") + "） 失败: " + e);
                }
            }
        }).start();
    }
}
