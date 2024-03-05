package com.gg.midend.controller;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PrintDataController extends GGBaseController {
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/data-api/savePrintData", method = RequestMethod.POST)
    public Object savePrintData(@RequestBody Map<String, Object> paramMap) {
        try {
            Map headerMap = (Map) paramMap.get("header");
            Map<String, Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");
            bodyMap.put("channel", channel);

            if (MapUtil.getMapParamValue(bodyMap, "cardNo", "").equals("") && MapUtil.getMapParamValue(bodyMap, "patId", "").equals("")) {
                throw new Exception("【cardNo】和【patId】不能同时为空");
            }

            Map qMap = new HashMap();
            qMap.put("hospitalId", bodyMap.get("hospitalId"));
            List<Map> HospAndProjInfo = sqlService.selectList("merhospcfg", "selectInfo", qMap);
            if (HospAndProjInfo.size() <= 0) {
                throw new Exception("【hospitalId】不存在");
            }

            int comRet = sqlService.update("printdata", "save", bodyMap);
            if (0 >= comRet) {
                throw new Exception("操作失败");
            }
            return retBack("00", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return retBack("99", e.getMessage());
        }
    }

    @RequestMapping(value = "/data-api/queryPrintData", method = RequestMethod.POST)
    public Object queryPrintData(@RequestBody Map<String, Object> paramMap) {
        try {
            Map headerMap = (Map) paramMap.get("header");
            Map<String, Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");
            bodyMap.put("channel", channel);

            if (MapUtil.getMapParamValue(bodyMap, "cardNo", "").equals("") && MapUtil.getMapParamValue(bodyMap, "patId", "").equals("")) {
                throw new Exception("【cardNo】和【patId】不能同时为空");
            }

            Map qMap = new HashMap();
            qMap.put("hospitalId", bodyMap.get("hospitalId"));
            List<Map> HospAndProjInfo = sqlService.selectList("merhospcfg", "selectInfo", qMap);
            if (HospAndProjInfo.size() <= 0) {
                throw new Exception("【hospitalId】不存在");
            }

            List<Map> list = sqlService.selectList("printdata", "query", bodyMap);
            if (list.size() < 1) {
                throw new Exception("无记录");
            }
            Map map = new HashMap();
            map.put("listinfo", list);
            return retBack("00", "成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return retBack("99", e.getMessage());
        }
    }
}
