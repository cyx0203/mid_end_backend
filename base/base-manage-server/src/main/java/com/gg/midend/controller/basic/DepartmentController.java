package com.gg.midend.controller.basic;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DepartmentController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/manage/comDept.insertDouble")
    @Transactional(rollbackFor = {Exception.class})
    public Object insertDouble(@RequestBody Map paramMap) throws ExceptionCenter {
        Map<String, Object> map1st = new HashMap<>();
        map1st.put("hospitalId", paramMap.get("hospitalId"));
        map1st.put("id", paramMap.get("id1"));
        map1st.put("name", paramMap.get("name1"));
        map1st.put("desc", "");
        map1st.put("parId", "##");
        map1st.put("level", 1);
        int ret = sqlService.update("comDept", "insert", map1st);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        Map<String, Object> map2nd = new HashMap<>();
        map2nd.put("hospitalId", paramMap.get("hospitalId"));
        map2nd.put("id", paramMap.get("id2"));
        map2nd.put("name", paramMap.get("name2"));
        map2nd.put("desc", paramMap.get("desc2"));
        map2nd.put("parId", paramMap.get("id1"));
        map2nd.put("level", 2);
        ret = sqlService.update("comDept", "insert", map2nd);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return 2;
    }
}
