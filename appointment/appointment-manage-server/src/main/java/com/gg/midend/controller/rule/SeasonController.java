package com.gg.midend.controller.rule;

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
public class SeasonController {

    @Autowired
    private SqlService sqlService;

    // 更新日期和时间
    @RequestMapping(value = "/manage/ruleSeason.update")
    @Transactional(rollbackFor = { Exception.class })
    public Object update(@RequestBody Map<String, Object> paramMap) throws ExceptionCenter {
        if (paramMap.get("startDate") == null) {// 全年令时改变时间
            int ret = sqlService.update("ruleSeason", "updateTime", paramMap);// 变更全时令时间
            if (ret != 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
        } else {// 夏冬时令存在起始日期
            int ret = sqlService.update("ruleSeason", "updateTime", paramMap);// 变更时间
            if (ret != 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
            ret = sqlService.update("ruleSeason", "updateDate", paramMap);// 变更日期
            if (ret != 2) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
        }
        return 1;
    }

    // 变更时令
    @RequestMapping(value = "/manage/ruleSeason.editActive")
    @Transactional(rollbackFor = { Exception.class })
    public Object editActive(@RequestBody Map<String, Object> paramMap) throws ExceptionCenter {
        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.putAll(paramMap);
        if ("0".equals(paramMap.get("flag"))) {// 启用全令时
            reqMap.put("active", "1"); // 全令时状态位
            reqMap.put("swActive", "0"); // 夏冬令时状态位

            int ret = sqlService.update("ruleSeason", "updateActive", reqMap);// 变更全时令状态位
            if (ret != 2) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
            ret = sqlService.update("ruleSeason", "updateSWActive", reqMap);// 变更夏冬时令状态位
            if (ret != 4) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }

        } else if ("1".equals(paramMap.get("flag"))) {// 启用夏冬令时
            reqMap.put("active", "0"); // 全令时状态位
            reqMap.put("swActive", "1"); // 夏冬令时状态位

            int ret = sqlService.update("ruleSeason", "updateSWActive", reqMap);// 变更夏冬时令状态位
            if (ret != 4) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
            ret = sqlService.update("ruleSeason", "updateActive", reqMap);// 变更全时令状态位
            if (ret != 2) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
        }

        return 1;
    }
}
