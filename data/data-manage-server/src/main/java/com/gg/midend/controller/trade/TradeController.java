package com.gg.midend.controller.trade;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.controller.BaseController;
import com.gg.core.service.SqlService;
import com.gg.midend.bean.PersonInfo;
import com.gg.midend.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TradeController extends BaseController {

    @Autowired
    private SqlService sqlService;
    /**
     * TODO
     *
     * @description: 查询交易数据
     * @author: lufm
     * @date: 2023/2/13 20:41
     * @param: [paramMap]
     * @return: java.lang.Object
     **/
    @RequestMapping(value = "/transRecord")
    public Object transRecord(@RequestBody Map paramMap) throws Exception {
        String startDate = (String) paramMap.get("startDate");
        String endDate = (String) paramMap.get("endDate");
        //获取循环查询的表名称
        List tabList = DateUtils.getBetweenDate(startDate,endDate,"trd_record_","left","yyyyMMdd");
        //查询所有数据，支持分页
        Map map = (Map) sqlService.excuteSql("trdRecord","selectHisTrans",paramMap,"tableName",tabList);
        return success(map);
    }


    @RequestMapping(value = "/test")
    public Object test(@MultiRequestBody("body") PersonInfo personInfo) throws Exception {
        sqlService.selectList("hospBranch","queryBranch","",personInfo,"level","1");
        return success();
    }
}
