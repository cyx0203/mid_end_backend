package com.gg.midend.service;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: 樊亦村
 * Created time: 2023-02-21 08:00:00
 * Description: 每天凌晨定时把src_source表的历史记录导入到src_source_h表
 */
@Component
public class RemoveSourceService {

    @Autowired
    private SqlService sqlService;

    /**
     * 每天凌晨定时把src_source表的历史记录导入到src_source_h表
     */
    @Scheduled(cron = "00 00 01 * * ?")
    @Transactional(rollbackFor = {Exception.class})
    public void scheduledTask() {
        try {
            System.out.println("=====开始将号源清理至历史表=====");
            // 步骤1：导数据
            sqlService.update("srcSourceH", "insertByTask");
            // 步骤2：清数据
            sqlService.update("srcSource", "deleteByTask");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
