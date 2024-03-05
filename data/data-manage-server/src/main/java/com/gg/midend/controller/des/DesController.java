package com.gg.midend.controller.des;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.service.DesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @Description
 * @Auther: Administrator
 * @Date: 2023/4/7 16:12
 */
@RestController
public class DesController {

    @Autowired
    private DesService desService;

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/startDes")
    public Object startDes(@RequestBody Map paramMap) throws ExceptionCenter {
        return desService.startDes(paramMap);
    }

    @RequestMapping(value = "/stopDes")
    public Object stopDes(@RequestBody Map paramMap) throws ExceptionCenter {
        return desService.stopDes(paramMap);
    }

    @RequestMapping(value = "/startDesAll")
    public Object startDesAll(@RequestBody Map paramMap) throws ExceptionCenter {

        List noStateDev = sqlService.selectList("dev","selectDevListWithOutState",paramMap);
        for(Iterator it = noStateDev.iterator(); it.hasNext();){
            Map map = (Map) it.next();
            map.put("hospitalId",paramMap.get("hospitalId"));
            startDes(map);
        }
        return 1;
    }

}
