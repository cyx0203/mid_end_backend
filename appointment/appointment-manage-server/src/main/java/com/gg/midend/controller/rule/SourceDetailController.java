package com.gg.midend.controller.rule;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.utils.RuleSourceHandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SourceDetailController {

    @Autowired
    private SqlService sqlService;

    
    // 插入号源规则及子表
    @RequestMapping(value = "/manage/ruleSourceDetail.insertById")
    @Transactional(rollbackFor = { Exception.class })
    public Object insert(@RequestBody Map<String, Object> paramMap) throws ExceptionCenter {
        //第1步 插入规则
        int ret = sqlService.update("ruleSourceDetail", "insert", paramMap);
        if (ret != 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        //第2步 插入号源详细表
        //组装插入数据
        List<Map<String, Object>> insertList = new ArrayList<>();
        //获取时令规则
         List<?> seasonRuleList = sqlService.selectList("ruleSeason", "selectByPid", paramMap);
        if (seasonRuleList == null || seasonRuleList.size() == 0) {
            GlobalConfig.log_api.info("=====异常的时令规则=====");
            return -1;
        }
        insertList = RuleSourceHandle.createRuleSourceAdvance(seasonRuleList, paramMap);
        
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("list", insertList);
        ret = sqlService.update("ruleSourceAdvance", "insert", insertMap); //插入号源详细表
        if(ret < insertList.size()) {
            GlobalConfig.log_api.info("=====插入规则失败=====");
            return -1;
        }
        //完成插入
        return 1;
    }
    

    // 更新号源规则及子表
    @RequestMapping(value = "/manage/ruleSourceDetail.updateDetail")
    @Transactional(rollbackFor = { Exception.class })
    public Object update(@RequestBody Map<String, Object> paramMap) throws ExceptionCenter {
        //第1步 更新规则
        int ret = sqlService.update("ruleSourceDetail", "updateById", paramMap);
        if (ret != 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        //第2步 删除详细规则
        ret = sqlService.update("ruleSourceAdvance", "deleteById", paramMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        //第3步 插入号源详细表
        //组装插入数据
        List<Map<String, Object>> insertList = new ArrayList<>();
        paramMap.put("id", paramMap.get("oid"));
        //获取时令规则
         List<?> seasonRuleList = sqlService.selectList("ruleSeason", "selectByPid", paramMap);
        if (seasonRuleList == null || seasonRuleList.size() == 0) {
            GlobalConfig.log_api.info("=====异常的时令规则=====");
            return -1;
        }
        insertList = RuleSourceHandle.createRuleSourceAdvance(seasonRuleList, paramMap);
        
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("list", insertList);
        ret = sqlService.update("ruleSourceAdvance", "insert", insertMap); //插入号源详细表
        if(ret < insertList.size()) {
            GlobalConfig.log_api.info("=====插入规则失败=====");
            return -1;
        }
        //完成插入
        return 1;
    }
}
