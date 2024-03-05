package com.gg.midend.controller.seat;

import com.gg.core.controller.BaseController;
import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SeatController extends BaseController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/addSeat")
    public Object addSeat(@RequestBody Map paramMap) throws Exception {
        List hospitalId=(List) paramMap.get("hospitalId");
        for(int i=0;i<hospitalId.size();i++){
            paramMap.remove("hospitalId");
            paramMap.put("hospitalId",hospitalId.get(i));
            int ret = sqlService.update("comseat", "insert", paramMap);

            if (ret < 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "新增失败");
            }
        }
        Map result=new HashMap();
        result.put("success",true);
        return result;
    }
}
