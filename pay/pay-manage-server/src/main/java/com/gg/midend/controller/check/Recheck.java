package com.gg.midend.controller.check;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.ConnHttp;
import com.gg.midend.utils.MD5;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: ml
 * Created time: 2023-03-13
 * Description: 重新对账
 */
@RestController
@EnableAsync
public class Recheck {
    @Autowired
    ConnHttp connHttp;

    private static final Logger logger = LoggerFactory.getLogger(Recheck.class);

    @Async
    @RequestMapping(value = "/manage/check.recheck")
    public Object recheck(@RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String paramMapStr = Json.toJson(paramMap, JsonFormat.compact());
        String paramMapMd5 = paramMapStr + "";
        String sign = MD5.MD5Encode(paramMapMd5);

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("timestamp",System.currentTimeMillis());
        headerMap.put("custId","001");

        map.put("body", paramMap);
        map.put("header", headerMap);
        map.put("sign", sign);

        logger.debug("发送参数: {}", map);
        Map rspMap = (Map) connHttp.send("https://mz.dzyywlyy.com/check/pay-check-server/reCheck", map);
        logger.debug("返回参数: {}",rspMap);

        return 1;
    }

}
