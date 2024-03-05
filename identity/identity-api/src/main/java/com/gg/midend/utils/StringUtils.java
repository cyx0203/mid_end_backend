package com.gg.midend.utils;

import org.springframework.beans.factory.annotation.Value;

public class StringUtils {
    @Value("${illegal:<,>,alert}")
    private String illegal;

    public boolean filterIllegal(String string) {
        String[] filter = illegal.split(",");
        for (String reg : filter) {
            if (string.contains(reg)) {
                return true;
            }
        }
        return false;
    }
}
