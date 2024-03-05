package com.gg.midend.controller.seat;

import com.gg.core.controller.BaseController;
import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FinanceController extends BaseController {
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/updateFinance")
    @Transactional(rollbackFor = { Exception.class })
    public Object updateEmployee(@RequestBody Map paramMap) throws Exception {
        int ret = sqlService.update("comfinance", "deleteFinance", paramMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "更新时删除失败");
        }

        List level=(List) paramMap.get("level");
        List levelName=(List) paramMap.get("levelName");
        for(int i=0;i<level.size();i++){
            paramMap.remove("level");
            paramMap.remove("levelName");
            paramMap.put("level",level.get(i));
            paramMap.put("levelName",levelName.get(i));
            int ret3 = sqlService.update("comfinance", "insertSelective", paramMap);

            if (ret3 < 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "更新失败");
            }
        }
        Map result=new HashMap();
        result.put("success",true);
        return result;
    }

    @RequestMapping(value = "/updateFinance2")
    @Transactional(rollbackFor = { Exception.class })
    public Object updateEmployee2(@RequestBody Map paramMap) throws Exception {
        List level=(List) paramMap.get("level");
        List levelName=(List) paramMap.get("levelName");
        for(int i=0;i<level.size();i++){
            paramMap.remove("level");
            paramMap.remove("levelName");
            paramMap.put("level",level.get(i));
            paramMap.put("levelName",levelName.get(i));
            int ret3 = sqlService.update("comfinance", "updateByPrimaryKeySelective", paramMap);

            if (ret3 < 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "更新失败");
            }
        }
        Map result=new HashMap();
        result.put("success",true);
        return result;
    }

    @RequestMapping(value = "/deleteFinance")
    public Object deleteEmployee(@RequestBody Map paramMap) throws Exception {
        int ret = sqlService.update("comfinance", "deleteFinance", paramMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
        }
        Map result=new HashMap();
        result.put("success",true);
        return result;
    }

    @RequestMapping(value = "/addFinance")
    public Object addFinance(@RequestBody Map paramMap) throws Exception {
        List level=(List) paramMap.get("level");
        List levelName=(List) paramMap.get("levelName");
        for(int i=0;i<level.size();i++){
            paramMap.remove("level");
            paramMap.remove("levelName");
            paramMap.put("level",level.get(i));
            paramMap.put("levelName",levelName.get(i));
            int ret = sqlService.update("comfinance", "insertSelective", paramMap);

            if (ret < 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "新增失败");
            }
        }
        Map result=new HashMap();
        result.put("success",true);
        return result;
    }
}
