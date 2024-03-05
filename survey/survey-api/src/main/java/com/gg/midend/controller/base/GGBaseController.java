package com.gg.midend.controller.base;

import com.gg.core.util.ParamUtil;

import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;


public class GGBaseController {

    @Value("${app.version}")
    private String version;
   

    public Object retBack(String code, String msg, Map<String,Object> map){

        Map headerMap = new HashMap<String,Object>();
        Map bodyMap = new HashMap<String,Object>();
        headerMap.put("returnCode",code);
        headerMap.put("returnMsg",msg);
        headerMap.put("version",version);
        bodyMap.putAll(map);
        Map retMap = new HashMap<String,Object>();
        retMap.put("header",headerMap);
        retMap.put("body",bodyMap);
        return retMap;
    }

    public Object retBack(String code, String msg, Map<String,Object> map,Object... args){
        Map paramMap = ParamUtil.arrayToMap(map,args);
        return retBack(code,msg,paramMap);
    }

    public Object retBack(String code, String msg, Object... args){
        Map paramMap = ParamUtil.arrayToMap(null,args);
        return retBack(code,msg,paramMap);
    }

    public Object retBack(String code, String msg){
        return retBack(code,msg,new HashMap());
    }

}
