package com.gg.midend.HttpRequest;

import com.alibaba.fastjson.JSON;
import com.gg.midend.config.GlobalConfig;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ConnHttp {
    /**
     * POST---有参测试(对象参数)
     *
     * @date
     */
    public Object getUrlRet(String url, Object data, MediaType mediaType) {
        GlobalConfig.log_api.info("=========开始交易============");
        GlobalConfig.log_api.info("URL:" + url);
        GlobalConfig.log_api.info("request:" + JSON.toJSONString(data));
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        String jsonString = JSON.toJSONString(data);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        String json = "";
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                json = EntityUtils.toString(responseEntity);
            }
            GlobalConfig.log_api.info("rs:" + json);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map map = (Map) JSON.parse(json);
        GlobalConfig.log_api.info("=========开始结束============");
        return map;
    }
}
