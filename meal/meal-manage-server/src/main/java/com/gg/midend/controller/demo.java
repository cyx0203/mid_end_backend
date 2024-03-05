package com.gg.midend.controller;

import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class demo {
    @Autowired
    private SqlService sqlService;

    @Transactional(rollbackFor = {Exception.class})
    public Object test() throws ApiException {
        Map map = new HashMap();
        map.put("code", "0");
        try {
            sqlService.update("xxx", "xxx", "wid", "222");
            fun();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "error");
            throw new ApiException(map);
        }
        return map;
    }

    public void fun() throws Exception {
        sqlService.update("xxx", "xxx", "wid", "65656");
        String[] a = new String[1];
        a[3] = "";
    }
}
