package com.gg.midend.controller;

import com.gg.core.controller.BaseController;
import com.gg.core.service.SqlService;
import com.gg.midend.bean.TestBean;
import com.gg.midend.service.MyTest;
import com.gg.midend.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HelloController extends BaseController {
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/ttest")
    public Object transRecord(@RequestBody Map paramMap) throws Exception{
        String startDate = (String) paramMap.get("startDate");
        String endDate = (String) paramMap.get("endDate");
        //获取循环查询的表名称
        List tabList = DateUtils.getBetweenDate(startDate,endDate,"trd_record_","left","yyyyMMdd");
        //查询所有数据，支持分页
        Map map = (Map) sqlService.excuteSql("cyx_test","getId",paramMap,"tableName",tabList);
        return success(map);
    }

    @RequestMapping(value = "/selectByPrimaryKey")
    public Object queryDevice(@RequestBody Map paramMap) throws Exception {
        Map result = (Map) sqlService.excuteSql("roomstatus", "selectByPrimaryKey", paramMap);
        return result;
    }

    @Autowired
    private MyTest myTest;

    @RequestMapping(value = "/test2")
    public String myBean() throws Exception {
        return myTest.my_getId().getHospitalId();
    }

    // controller里可以实例化service里的对象,调用service里写的服务
}
