package com.gg.midend.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalConfig {
    public static Logger log_api;

    static {
        try {
            log_api = LogManager.getLogger("Log.Api");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
