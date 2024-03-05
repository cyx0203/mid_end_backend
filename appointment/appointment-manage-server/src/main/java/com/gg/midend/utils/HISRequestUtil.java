package com.gg.midend.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 描述：HIS 请求的包装类
 */
@Component
public class HISRequestUtil {

    /* HIS 接口地址 */
    private static String requestUrl;

    @Value("${his-config.request-url}")
    public void setRequestUrl(String requestUrl) {
        HISRequestUtil.requestUrl = requestUrl;
    }

    /**
     * 描述：请求 HIS 接口获取相关信息
     *
     * @param functionName his 接口名
     * @param reqJson      请求报文 json 格式
     * @return 返回报文
     */
    public static String requestHIS(String functionName, JSONObject reqJson) {
        // 构造入参，转化为 xml 格式
        String reqXmlStr = "<funderService functionName='" + functionName + "'><value><![CDATA[<request>" + JSONUtil.toXmlStr(reqJson)
                + "</request>]]></value></funderService>";
        GlobalConfig.log_api.info("请求HIS入参: " + reqXmlStr);
        // 构造 post 请求
        HashMap<String, Object> paramMapForHIS = new HashMap<>();
        paramMapForHIS.put("param", reqXmlStr);
        // 发送 post 请求
        String respXmlStr = HttpUtil.post(requestUrl, paramMapForHIS);
        GlobalConfig.log_api.info("请求HIS出参: " + respXmlStr);
        return respXmlStr;
    }
}
