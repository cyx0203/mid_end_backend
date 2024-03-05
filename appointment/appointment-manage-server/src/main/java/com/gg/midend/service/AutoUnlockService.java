package com.gg.midend.service;

import cn.hutool.core.util.StrUtil;
import com.gg.core.service.SqlService;

import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: 樊亦村
 * Created time: 2023-02-21 08:00:00
 * Description: 每分钟自动解锁号源
 */
@Component
public class AutoUnlockService {

    @Autowired
    private SqlService sqlService;

    @Value("${config.cancelMinutes}")
    private String cancelMinutes;

    /**
     * 每分钟自动解锁号源
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduledTask() {
        try {
            GlobalConfig.log_api.info("=====开始自动解锁号源=====");

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("autoUnlockMinute", cancelMinutes);
            /*
             * 1.获取五分钟内需要解锁的号
             * 2.更新 src_order src_source
             */
            sqlService.update("srcOrder", "autoUnlock", updateMap);
            GlobalConfig.log_api.info("=====自动解锁号源成功=====");

            // 生成 orderList
            List<String> unLockList = StrUtil.split((CharSequence) updateMap.get("unLockListStr"), ",");
            GlobalConfig.log_api.info("=====更新列表长度: " + unLockList.size() + "=====");

            // 3.插入 src_order_detail
            if (unLockList.size() != 0) {
                updateMap.put("unLockList", unLockList);
                sqlService.update("srcOrderDetail", "insert", updateMap);
                GlobalConfig.log_api.info("=====订单流水表插入成功=====");
            }
        } catch (Exception e) {
            // sql 异常处理
            if ((e.getCause() != null) && (e.getCause() instanceof SQLException)) {
                GlobalConfig.log_api.error("=====自动解锁号源失败: " + e.getCause().getMessage() + "=====");
            } else {
                // 非 sql 异常处理
                GlobalConfig.log_api.error("=====自动解锁号源失败: " + e.getMessage() + "=====");
            }
        }
    }
}
