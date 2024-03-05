package com.gg.midend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * TODO
 *
 * @Description
 * @Auther: Administrator
 * @Date: 2023/3/17 15:47
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(
                new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2)
        );
    }
}
