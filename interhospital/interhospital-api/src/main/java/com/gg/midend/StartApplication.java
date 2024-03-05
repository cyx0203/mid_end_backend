package com.gg.midend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * SpringBoot应用启动类
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
@SpringBootApplication
@MapperScan("com.gg.midend.mapper")
@ComponentScan(basePackages = {"com.gg"})
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
