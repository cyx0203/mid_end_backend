package com.gg.midend.controller;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.core.util.ParamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping({"/validationoppwd"})
    public Object validationoppwd(@RequestBody Map paramMap) throws ExceptionCenter {
        List<Map> list = sqlService.selectList("mealcanteenemployee", "check-opuser", paramMap);
        if (list.size() < 1)
            throw new ExceptionCenter("500", "密码不正确");

        return ParamUtil.arrayToMap((Map) null, new Object[0]);
    }
}
