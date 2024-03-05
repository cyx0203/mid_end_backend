package com.gg.midend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.gg"})
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
