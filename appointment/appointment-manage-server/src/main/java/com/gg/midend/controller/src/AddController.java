package com.gg.midend.controller.src;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.utils.HISRequestUtil;
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

/**
 * Author: 樊亦村 Created time: 2023-02-25 08:00:00 Description: 加号、加排班控制类
 */
@RestController
public class AddController {

    @Autowired
    private SqlService sqlService;

    // 加号
    @RequestMapping(value = "/manage/src.addSource")
    @Transactional(rollbackFor = { Exception.class })
    public Object addSource(@RequestBody Map<String, Object> paramMap) throws Exception {
        String scheduleId = (String) paramMap.get("scheduleId");
        int addNum = (int) paramMap.get("addNum");
        // 步骤1：查询在src_source表中srcSchId的最大号序
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("scheduleId", scheduleId);
        Map<String, Object> maxOrderMap = (Map<String, Object>) sqlService.selectOne("srcSource", "selectMaxOrder", searchMap);
        int maxOrder = (int) maxOrderMap.get("maxOrder");
        // 步骤2：将加号插入src_source表
        List<Map<String, Object>> insertList = new ArrayList<>();
        for (int i = 0; i < addNum; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("srcSchId", scheduleId);
            map.put("order", maxOrder + i + 1);
            map.put("stime", paramMap.get("stime"));
            map.put("etime", paramMap.get("etime"));
            map.put("numType", "2");
            insertList.add(map);
        }
        Map<String, Object> insertBatchMap = new HashMap<>();
        insertBatchMap.put("insertList", insertList);
        sqlService.update("srcSource", "insertBatch", insertBatchMap);
        
        // 步骤3：插入src_add_record表
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("hospitalId", paramMap.get("hospitalId"));
        insertMap.put("srcSchId", scheduleId);
        insertMap.put("addNum", addNum);
        insertMap.put("startOrder", maxOrder + 1);
        insertMap.put("endOrder", maxOrder + addNum);
        insertMap.put("reason", "");
        insertMap.put("stime", paramMap.get("stime"));
        insertMap.put("etime", paramMap.get("etime"));
        insertMap.put("createUser", paramMap.get("createUser"));
        sqlService.update("srcAddRecord", "insert", insertMap);
        
        // 步骤4：通知HIS加号 start
        // 查询并构造需要给HIS送的数据start
        searchMap.put("scheduleId", scheduleId);
        searchMap.put("maxOrder", maxOrder);
        List<?> sourceList = sqlService.selectList("srcSource", "selectAddToHis", searchMap);
        if (sourceList.isEmpty()) {
            throw new Exception("新增后查询的号源为空");
        }
        Map source1st = (Map) sourceList.get(0);
        // 查询并构造需要给HIS送的数据end
        // 构造入参, xml 格式
        JSONObject reqJson = JSONUtil.createObj().set("scheduleId", scheduleId).set("date", source1st.get("date"))
                .set("noon", source1st.get("noon")).set("deptCode", source1st.get("deptCode"))
                .set("registerType", source1st.get("registerType")).set("doctorCode", source1st.get("doctorCode"))
                .set("sourceList", sourceList);
        // 发送请求
        String response = HISRequestUtil.requestHIS("YQ_addNotice", reqJson);
        // 返回 xml 格式报文，转为 json 字符串
        JSONObject respJson = JSONUtil.xmlToJson(response);
        // 判断请求结果，失败则抛出异常，成功取 payFee
        if (!"0".equals(respJson.getJSONObject("response").getStr("returnCode"))) {
            throw new Exception(respJson.getJSONObject("response").getStr("returnMsg"));
        }
        // 步骤4：通知HIS加号 end
        return 1;
    }

    // 添加排班
    @RequestMapping(value = "/manage/src.addSchdule")
    @Transactional(rollbackFor = { Exception.class })
    public Object addSchedule(@RequestBody Map<String, Object> paramMap) throws Exception {

        // 步骤1：插入 src_schedule表
        if(paramMap.get("doctorId") == null) {
            paramMap.put("doctorId", "");
        }
        int ret = sqlService.update("srcSchedule", "insertByAdd", paramMap);
        if (ret < 1) {
            GlobalConfig.log_api.info("=====添加排班失败，更新信息（" + paramMap + "）=====");
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        // 步骤2：根据号源规则生成号源
        int order = 0;
        // 开始插入号源明细表 start
        List<Map<String, Object>> insertSourceList = new ArrayList<>();
        for (int n = 0; n < Integer.parseInt(paramMap.get("sourceNum").toString()); n++) {
            Map<String, Object> insertSourceMap = new HashMap<>();
            insertSourceMap.put("srcSchId", paramMap.get("scheduleId"));
            insertSourceMap.put("order", ++order);
            insertSourceMap.put("stime", paramMap.get("stime"));
            insertSourceMap.put("etime", paramMap.get("etime"));
            insertSourceMap.put("numType", "2");
            insertSourceList.add(insertSourceMap);
        }
        //插入src_add_record表
        Map<String, Object> inMap = new HashMap<>();
        inMap.put("hospitalId", paramMap.get("hospitalId"));
        inMap.put("srcSchId", paramMap.get("scheduleId"));
        inMap.put("addNum", order);
        inMap.put("startOrder", 1);
        inMap.put("endOrder", order);
        inMap.put("reason", "");
        inMap.put("createUser", paramMap.get("createUser"));
        if (sqlService.update("srcAddRecord", "insert", inMap) < 1) {
            GlobalConfig.log_api.info("=====批量插入src_add_record失败=====");
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        
        // 存 src_source 表
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("insertList", insertSourceList);
        ret = sqlService.update("srcSource", "insertBatch", insertMap);
        if (ret < insertSourceList.size()) {
            GlobalConfig.log_api.info("=====批量插入src_source失败=====");
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        
        // 更新 src_schedule 的 valid 值
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", paramMap.get("scheduleId"));
        updateMap.put("valid", "1");
        if (sqlService.update("srcSchedule", "update", updateMap) < 1) {
            GlobalConfig.log_api.info("=====更新src_schedule失败=====");
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        
        // 步骤3：通知HIS加号 start
        // 查询并构造需要给HIS送的数据start
        paramMap.put("maxOrder", "0");
        List<?> sourceList = sqlService.selectList("srcSource", "selectAddToHis", paramMap);
        if (sourceList.isEmpty()) {
            throw new Exception("新增后查询的号源为空");
        }
        Map source1st = (Map) sourceList.get(0);
        // 查询并构造需要给HIS送的数据end
        // 构造入参, xml 格式
        JSONObject reqJson = JSONUtil.createObj().set("scheduleId", paramMap.get("scheduleId")).set("date", source1st.get("date"))
                .set("noon", source1st.get("noon")).set("deptCode", source1st.get("deptCode"))
                .set("registerType", source1st.get("registerType")).set("doctorCode", source1st.get("doctorCode"))
                .set("sourceList", sourceList);
        // 发送请求
        String response = HISRequestUtil.requestHIS("YQ_addNotice", reqJson);
        // 返回 xml 格式报文，转为 json 字符串
        JSONObject respJson = JSONUtil.xmlToJson(response);
        // 判断请求结果，失败则抛出异常，成功取 payFee
        if (!"0".equals(respJson.getJSONObject("response").getStr("returnCode"))) {
            throw new Exception(respJson.getJSONObject("response").getStr("returnMsg"));
        }

        return 1;
    }
}
