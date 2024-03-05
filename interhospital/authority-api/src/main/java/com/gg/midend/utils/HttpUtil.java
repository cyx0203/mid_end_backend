package com.gg.midend.utils;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.gg.midend.entity.pojo.ResponseData;
import com.gg.midend.exception.enums.GlobalErrorCodeConstants;
import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static com.gg.midend.constant.Constants.CA_CODE_ERROR;
import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 基于okhttp轻量框架封装的HttpUtil
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-11
 **/
public class HttpUtil {

    private static final OkHttpClient client;

    private static final Gson gson = new Gson();

    // 初始化OkHttpClient
    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(5L, TimeUnit.SECONDS)
                .readTimeout(5L, TimeUnit.SECONDS)
                .writeTimeout(5L, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送GET请求
     *
     * @param url  请求的目标URL
     * @param type 响应体类型
     * @return 响应体泛型类型，封装了响应结果或异常信息
     */
    public static <T> T get(String url, Type type) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return execute(request, type);
    }

    /**
     * 发送POST请求
     * 请求体为application/x-www-form-urlencoded类型
     *
     * @param url  请求的目标URL
     * @param data  请求体参数
     * @param type 响应体类型
     * @return 响应体泛型类型，封装了响应结果或异常信息
     */
    public static <T> T post(String url, Object data, Type type) {

        // 构建请求体
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        if (ObjUtil.isNotNull(data)){
            Class<?> clazz = data.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(data);
                    if (fieldValue != null) {
                        formBodyBuilder.addEncoded(field.getName(), fieldValue.toString());
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Failed to access field: " + field.getName());
                }
            }
        }

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBodyBuilder.build())
                .build();
        return execute(request, type);
    }

    private static <T> T execute(Request request, Type type) {

        try (Response response = client.newCall(request).execute()) {

            String body = response.body().string();
            ResponseData responseData = gson.fromJson(body, ResponseData.class);

            if (!response.isSuccessful()) {
                throw exception(GlobalErrorCodeConstants.NOT_FOUND);
            }

            if (StrUtil.equals(CA_CODE_ERROR, responseData.getCode())) {
                throw exception(responseData.getCode(),responseData.getInfo());
            }

            return gson.fromJson(body, type);
        } catch (SocketTimeoutException e) {
            throw exception(GlobalErrorCodeConstants.OUT_OF_TIME);
        } catch (IOException e) {
            throw exception(GlobalErrorCodeConstants.FAILED);
        }
    }
}