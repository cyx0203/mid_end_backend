package com.gg.midend.controller;

import com.gg.core.util.ParamUtil;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;


public class GGBaseController {

    @Value("${app.version}")
    private String version;
   

    public Object retBack(String code, String msg, Map<String,Object> map){

        Map<String,Object> headerMap = new HashMap<>();
        Map<String,Object> bodyMap = new HashMap<>(map);
        headerMap.put("returnCode",code);
        headerMap.put("returnMsg",msg);
        headerMap.put("version",version);

        Map<String,Object> retMap = new HashMap<>();
        retMap.put("header",headerMap);
        retMap.put("body",bodyMap);

        GlobalConfig.log_api.info("出参: " + retMap);
        return retMap;
    }

    public Object retBack(String code, String msg, Map<String,Object> map,Object... args){
        Map<String, Object> paramMap = ParamUtil.arrayToMap(map, args);
        return retBack(code,msg,paramMap);
    }

    public Object retBack(String code, String msg,Object... args){
        Map<String, Object> paramMap = ParamUtil.arrayToMap(null,args);
        return retBack(code,msg,paramMap);
    }

    public Object retBack(String code, String msg){
        return retBack(code,msg,new HashMap<>());
    }

}
