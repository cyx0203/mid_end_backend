package com.gg.midend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CA认证服务启动类
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-07
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.gg"})
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
