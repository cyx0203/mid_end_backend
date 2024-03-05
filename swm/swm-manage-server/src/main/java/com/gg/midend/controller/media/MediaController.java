package com.gg.midend.controller.media;

import com.gg.core.controller.BaseController;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MediaController extends BaseController {
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/queryMediaStatus")
    public Object queryMedia(@RequestBody Map paramMap) throws Exception {
        Map result = (Map) sqlService.excuteSql("mediastatus", "queryMediaStatus", paramMap);
        return result;
    }

    @RequestMapping(value = "/selectByTimeAndType")
    public Object transRecord(@RequestBody Map paramMap) throws Exception {
        System.out.println(paramMap);
        Map result = (Map) sqlService.excuteSql("mediastatus", "selectByTimeAndType", paramMap);
        return result;
    }
}
