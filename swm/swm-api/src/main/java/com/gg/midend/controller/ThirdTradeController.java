package com.gg.midend.controller;

import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ThirdTradeController extends GGBaseController {
    @Value("${illegal:<,>,alert}")
    private String illegal;

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/data-api/saveTrdRecord", method = RequestMethod.POST)
    public Object saveTrdRecord(@RequestBody Map<String, Object> paramMap) {
        try {
            Map headerMap = (Map) paramMap.get("header");
            Map<String, Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");

            bodyMap.put("channel", channel);
            String[] needParams = {"trdDate", "trdTime", "hospitalId", "trdType", "devId", "payClientNo", "merchantId"};//必传字段
            for (String p : needParams) {
                if (MapUtil.getMapParamValue(bodyMap, p, "").equals("")) {
                    throw new Exception("缺少【" + p + "】");
                }
            }
            for (String key : bodyMap.keySet()) {
                String value = (String) bodyMap.get(key);
                GlobalConfig.log_api.debug(key + ":" + value);
                if (filterIllegal(key) || filterIllegal(value)) {
                    throw new Exception("请求参数非法，请检查");
                }
            }
            Map qMap = new HashMap();
            qMap.put("hospitalId", bodyMap.get("hospitalId"));
            List<Map> HospAndProjInfo = sqlService.selectList("merhospcfg", "selectInfo", qMap);
            if (HospAndProjInfo.size() <= 0) {
                throw new Exception("【hospitalId】不存在");
            }

            int comRet = sqlService.update("trdrecord", "save", bodyMap);
            if (0 >= comRet) {
                throw new Exception("保存失败");
            }
            return retBack("00", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return retBack("99", e.getMessage());
        }
    }

    @RequestMapping(value = "/data-api/updateTrdRecord", method = RequestMethod.POST)
    public Object updateTrdRecord(@RequestBody Map<String, Object> paramMap) {
        try {
            Map headerMap = (Map) paramMap.get("header");
            Map<String, Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");
            bodyMap.put("channel", channel);

            String[] needParams = {"hospitalId", "payClientNo"};//必传字段
            for (String p : needParams) {
                if (MapUtil.getMapParamValue(bodyMap, p, "").equals("")) {
                    throw new Exception("缺少【" + p + "】");
                }
            }
            for (String key : bodyMap.keySet()) {
                String value = (String) bodyMap.get(key);
                GlobalConfig.log_api.debug(key + ":" + value);
                if (filterIllegal(key) || filterIllegal(value)) {
                    throw new Exception("请求参数非法，请检查");
                }
            }
            Map qMap = new HashMap();
            qMap.put("hospitalId", bodyMap.get("hospitalId"));
            List<Map> HospAndProjInfo = sqlService.selectList("merhospcfg", "selectInfo", qMap);
            if (HospAndProjInfo.size() <= 0) {
                throw new Exception("【hospitalId】不存在");
            }

            int comRet = sqlService.update("trdrecord", "update", bodyMap);
            if (0 >= comRet) {
                throw new Exception("操作失败");
            }
            return retBack("00", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return retBack("99", e.getMessage());
        }
    }

    private boolean filterIllegal(String string) {
        String[] filter = illegal.split(",");
        for (String reg : filter) {
            if (string.contains(reg)) {
                return true;
            }
        }
        return false;
    }
}
