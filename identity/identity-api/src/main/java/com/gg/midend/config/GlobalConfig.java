package com.gg.midend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalConfig {
    public static Logger log_api;
    static {
        try{
            log_api = LoggerFactory.getLogger("Log.Api");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
