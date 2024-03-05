package com.gg.midend.controller.order;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.ConnHttp;
import com.gg.midend.utils.MD5;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: ml
 * Created time: 2023-03-22
 * Description: 查询
 */
@RestController
public class Query {

    @Autowired
    ConnHttp connHttp;

    private static final Logger logger = LoggerFactory.getLogger(Query.class);

    @Value("${payQueryUrl.queryOrder}")
    String queryOrder; // 0003

    @RequestMapping(value = "/manage/payOrder.query")
    public Object query(@RequestBody Map<String, Object> paramMap){
        Map<String, Object> returnMap = new HashMap<>();
        String md5Key = "";
        try{
            sendUrl(paramMap, "0003", md5Key, queryOrder);
            returnMap.put("success","0");
        }catch(Exception e){
            returnMap.put("success","1");
        }
        return returnMap;
    }

    private Map sendUrl(Map paramMap, String tradeType, String md5Key, String queryUrl) throws Exception {
        logger.debug("============" + tradeType + " ==" + "start" + "== " + queryUrl + "=================");
        Map<String, Object> map = new HashMap<>();
        String paramMapStr = Json.toJson(paramMap, JsonFormat.compact());
        String paramMapMd5 = paramMapStr + "" + md5Key;
        String sign = MD5.MD5Encode(paramMapMd5);
        map.put("tradeParam", paramMap);
        map.put("tradeType", tradeType);
        map.put("sign", sign);
        logger.debug("向 {} 发送参数: {}", tradeType, map);
        Map rspMap = (Map) connHttp.send(queryUrl, map);
        logger.debug("{} 返回参数: {}", tradeType, rspMap);
        //判断返回结果
        if (rspMap == null) {
            throw new Exception("查询前置服务异常");
        }
        String returnCode = (String) rspMap.get("returnCode");
        if (!"0000".equals(returnCode)) {
            throw new Exception(rspMap.get("returnInfo").toString());
        }
        logger.debug("============" + tradeType + " ==" + "end" + "== " + queryUrl + "=================");
        return rspMap;
    }
}
