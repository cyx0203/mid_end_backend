package com.gg.midend.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import com.gg.midend.config.GlobalConfig;

import java.util.Arrays;


@Aspect
@Component
public class PayAppApiAspect {

    @Pointcut("execution(* com.gg.midend.controller.*.*(..))")
    private void pointCutMethod(){
    }

    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();
        long duration = (end - begin) / 1000000;

        GlobalConfig.log_api.info("调用方法：{}，\n参数：{}，\r\n耗时：{}毫秒\r\n返回：{}",
                pjp.getSignature().toString(), Arrays.toString(pjp.getArgs()),
                duration, obj);
        return obj;
    }


}

