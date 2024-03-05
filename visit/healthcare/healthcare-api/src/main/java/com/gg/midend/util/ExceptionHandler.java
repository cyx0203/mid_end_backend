package com.gg.midend.util;

import com.gg.core.exception.ApiException;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：本类用于整合 controller 中 try-catch 模块的异常处理
 */
public class ExceptionHandler {

    @Value("${app.version}")
    private static String version;

    /**
     * 描述：整合 sql 异常处理与 非 sql 异常处理
     * @param e 异常
     * @return 返回异常信息
     */
    public static Object handleExpNeedReturn(Exception e) {
        // sql 异常处理
        if ((e.getCause() != null) && (e.getCause() instanceof SQLException)) {
            return retBack("99", e.getCause().getMessage());
        }
        // 非 sql 异常处理
        return retBack("99", e.getMessage());
    }

    /**
     * 描述：整合 sql 异常处理与 非 sql 异常处理
     * @param e 异常
     * @return ApiException 返回需抛出的异常
     */
    public static ApiException handleExpNeedRollback(Exception e) {
        // sql 异常处理
        if ((e.getCause() != null) && (e.getCause() instanceof SQLException)) {
            return new ApiException(retBack("99", e.getCause().getMessage(), new HashMap<>()));
        }
        // 非 sql 异常处理
        return new ApiException(retBack("99", e.getMessage(), new HashMap<>()));
    }

    public static Object retBack(String code, String msg) {
        return retBack(code, msg, new HashMap<>());
    }

    public static Object retBack(String code, String msg, Map<String, Object> map) {

        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>(map);
        headerMap.put("returnCode", code);
        headerMap.put("returnMsg", msg);
        headerMap.put("version", version);

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("header", headerMap);
        retMap.put("body", bodyMap);

        GlobalConfig.log_api.info("出参: " + retMap);
        return retMap;
    }
}
