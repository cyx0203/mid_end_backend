package com.gg.midend.controller;

import com.gg.core.service.SqlService;
import com.gg.midend.service.DevStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 适配老agent接口，接口规范和其他不一致
 */
@RestController
@RequestMapping(value = "/data-api/agent", method = RequestMethod.POST)
public class DevController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private DevStateService devStateService;

    @RequestMapping(value = "/")
    public Object api(@RequestParam Map paramMap) {
        Map retMap = new HashMap();
        retMap.put("rspCode", "00");
        retMap.put("rspMsg", "交易成功");
        try {
            //老agent不送hospitalId，默认送H001
            paramMap.put("hospitalId", "H001");
            String tradeCode = (String) paramMap.get("tradeCode");
            if ("".equals(tradeCode)) {
                throw new Exception("缺少【tradeCode】!");
            } else if ("syscfg.selectSys".equals(tradeCode)) {
                //签到，
                List retList = sqlService.selectList("descfg", "selectSys", paramMap);
                retMap.put("listInfo", retList);
            } else if ("devstate.update".equals(tradeCode)) {
                devStateService.update(paramMap);
            } else if ("devstate.statusUpdate".equals(tradeCode)) {
                devStateService.statusUpdate(paramMap);
            } else {
                retMap.put("rspCode", "90");
                retMap.put("rspMsg", "交易类型不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            retMap.put("rspCode", "99");
            retMap.put("rspMsg", e.getMessage());
        } finally {
            return retMap;
        }
    }
}
