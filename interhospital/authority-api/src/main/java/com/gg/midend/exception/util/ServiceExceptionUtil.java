package com.gg.midend.exception.util;

import com.gg.core.exception.api.Header;
import com.gg.midend.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ServiceException} 工具类
 * 目的在于，格式化异常信息提示。
 */
@Slf4j
public class ServiceExceptionUtil {

    /**
     * 错误码提示模板
     */
    private static final ConcurrentMap<String, String> MESSAGES = new ConcurrentHashMap<>();

    public static void putAll(Map<String, String> messages) {
        ServiceExceptionUtil.MESSAGES.putAll(messages);
    }

    public static void put(String code, String message) {
        ServiceExceptionUtil.MESSAGES.put(code, message);
    }

    public static void delete(String code, String message) {
        ServiceExceptionUtil.MESSAGES.remove(code, message);
    }

    // ========== 和 ServiceException 的集成 ==========

    public static ServiceException exception(Header header) {
        String messagePattern = MESSAGES.getOrDefault(header.getRspCode(), header.getRspMsg());
        return exception0(header.getRspCode(), messagePattern);
    }

    public static ServiceException exception(Header header, Object... params) {
        String messagePattern = MESSAGES.getOrDefault(header.getRspCode(), header.getRspMsg());
        return exception0(header.getRspCode(), messagePattern, params);
    }

    /**
     * 创建指定编号的 ServiceException 的异常
     *
     * @param code 编号
     * @return 异常
     */
    public static ServiceException exception(String code) {
        return exception0(code, MESSAGES.get(code));
    }

    /**
     * 创建指定编号的 ServiceException 的异常
     *
     * @param code   编号
     * @param params 消息提示的占位符对应的参数
     * @return 异常
     */
    public static ServiceException exception(String code, Object... params) {
        return exception0(code, MESSAGES.get(code), params);
    }

    public static ServiceException exception0(String code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new ServiceException(code, message);
    }

    // ========== 格式化方法 ==========

    /**
     * 将错误编号对应的消息使用 params 进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    public static String doFormat(String code, String messagePattern, Object... params) {
        StringBuilder buff = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;
        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    buff.append(messagePattern.substring(i));
                    return buff.toString();
                }
            } else {
                buff.append(messagePattern, i, j);
                buff.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
        }
        buff.append(messagePattern.substring(i));
        return buff.toString();
    }

}
