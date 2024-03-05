package com.gg.midend.service;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: 樊亦村
 * Created time: 2022-01-13 08:00:00 
 * Description: 定时生成号源坑位数据类
 */
@Component
public class CreateSourceService {

    @Autowired
    private SqlService sqlService;

    @Value("${config.sourceDay}")
    private int sourceDay;

    /**
     * 每天定时轮询sch_date表，检查7天内需要生成号源的记录
     */
    //@PostConstruct
    @Scheduled(cron = "00 00 02 * * ?")
    @Transactional(rollbackFor = { Exception.class })
    public void scheduledTask() throws ExceptionCenter {
        GlobalConfig.log_api.info("=====开始生成" + sourceDay + "天内的号源(sch_source)=====");
        // 查询所有院区
        List hosList = sqlService.selectList("com", "selectHospital");
        for (Object hosItem : hosList) {
            Map map = (Map) hosItem;
            String hospitalId = map.get("id").toString();
            GlobalConfig.log_api.info("=====开始为院区：" + hospitalId + "生成号源=====");
            createSourceForOneHospital(hospitalId);
        }
    }

    /**
     * 为每家医院生成号源
     *
     * @param hospitalId 医院编号
     * @throws ExceptionCenter 异常
     */
    // 为每家医院生成号源
    private void createSourceForOneHospital(String hospitalId) {
        // 查询尚未生成号源的排班记录(src_schedule, valid=0)
        Map<String, String> queryCondition = new HashMap<>();
        queryCondition.put("businessType", "1");
        queryCondition.put("hospitalId", hospitalId);
        List srcScheduleList = sqlService.selectList("srcSchedule", "selectUnValid", queryCondition);
        
        // 查询号源生成数量规则
        List sourceRuleList = sqlService.selectList("ruleSource", "selectById", queryCondition);
        if (sourceRuleList == null || sourceRuleList.size() == 0) {
            GlobalConfig.log_api.info("=====异常的科室号源规则=====");
            return;
        }

        // 从今天开始计算到提前放号的最后一天
        for (int i = 0; i < sourceDay; i++) {
            // 获取从今天起，i天后的日期，格式yyyyMMdd
            String tempDate = getAnyDate(i);
            int findNum = 0;
            int successNum = 0;
            String srcDateId = "";
            for (Object item : srcScheduleList) {
                Map srcScheduleMap = (Map) item;
                if (tempDate.equals(srcScheduleMap.get("srcDate"))) {
                    findNum++;
                    // src_date表主键
                    srcDateId = srcScheduleMap.get("srcDateId").toString();
                    // 午别
                    String noon = srcScheduleMap.get("noon").toString();
                    
                    // 找到了,开始对每个半天(startTime-endTime)生成号源
                    if (createSourceForOneSchedule(srcScheduleMap, sourceRuleList)) {
                        successNum++;
                    }
                }
            }
            // 开始清算
            if (findNum > 0) {
                String valid;
                if (findNum == successNum) {
                    valid = "1";
                } else if (successNum == 0) {
                    valid = "9";
                } else {
                    valid = "8";
                }
                // 更新src_date表生成号源状态字段
                srcDateUpdate(srcDateId, valid);
            }
        }
    }

    /**
     * 为一个号源排班（某半天，某科室，某号别，某医生）生成号源
     *
     * @param srcScheduleMap 需要生成号源的记录(src_schedule表)
     * @param sourceRuleList 号源数量规则
     * @return boolean 生成成功-true; 生成失败-false
     */
    private boolean createSourceForOneSchedule(Map srcScheduleMap,  List sourceRuleList) {
        try {
            String srcScheduleId = srcScheduleMap.get("id").toString();
            String deptId = srcScheduleMap.get("deptId").toString(); // 科室编号
            String registerType = srcScheduleMap.get("registerType").toString(); // 号别
            String doctorId = srcScheduleMap.get("doctorId").toString(); // 医生编号
            
            // 开始把srcScheduleMap这条记录 和 号源数量规则表中的一条 匹配上
            List<Map<String, Object>> sourceRule = matchSchduleRule(sourceRuleList, deptId, registerType, doctorId);
            if (sourceRule == null || sourceRule.size() <= 0) {
                GlobalConfig.log_api.info("=====获取匹配规则失败=====");
                return false;
            }
            // 开始插入号源明细表 start
            List<Map<String, Object>> insertSourceList = new ArrayList<>();
            int order = 0;
            
            List<Map<String, Object>> advanceList = matchAdvanceRule(sourceRule, srcScheduleMap.get("noon").toString()); //匹配号源规则
            if (advanceList.size() <= 0) {
                GlobalConfig.log_api.info("=====获取匹配特殊规则失败=====");
            }
            for (Map<String, Object> advanceMap : advanceList) {
                int SOURCE_NUM = Integer.parseInt(advanceMap.get("sourceNum").toString());// 号源数量
                for (int l = 0; l < SOURCE_NUM; l++) {
                    Map<String, Object> insertSourceMap = new HashMap<>();
                    insertSourceMap.put("srcSchId", srcScheduleId);
                    insertSourceMap.put("order", ++order);
                    insertSourceMap.put("stime", advanceMap.get("stime"));
                    insertSourceMap.put("etime", advanceMap.get("etime"));
                    insertSourceMap.put("numType", "0");
                    insertSourceList.add(insertSourceMap);
                }
            }
            // 存 src_source 表
            Map<String, Object> insertMap = new HashMap<>();
            insertMap.put("insertList", insertSourceList);
            int ret = sqlService.update("srcSource", "insertBatch", insertMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("=====批量插入src_source失败=====");
                return false;
            }
            // 更新 src_schedule 的 valid 值
            Map<String, String> updateMap = new HashMap<>();
            updateMap.put("id", srcScheduleId);
            updateMap.put("valid", "1");
            ret = sqlService.update("srcSchedule", "update", updateMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("=====更新src_schedule失败=====");
                return false;
            }
            return true;
        } catch (

        Exception e) {
            GlobalConfig.log_api.info("生成号源失败，原因：" + e);
            return false;
        }
    }

    // 更新src_date表生成号源状态字段
    private void srcDateUpdate(String srcDateId, String valid) {
        // 更新主表apt_resource的 valid 值
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", srcDateId);
        updateMap.put("valid", valid);
        sqlService.update("srcDate", "update", updateMap);
    }


    /**
     * 匹配号源规则
     *
     * @param sourceRuleList 号源数量规则
     * @param deptId         被匹配的科室编号
     * @param registerType   被匹配的号别
     * @param doctorId   被匹配的号别
     * @return List<Map<String, Object>> 号源数量规则
     */
    private List<Map<String, Object>> matchSchduleRule(List sourceRuleList, String deptId, String registerType, String doctorId) {
        Map<String, Object> ruleMap;
        String deptRule;//科室规则
        String doctorRule;
        String regTypeRule;
        Map<String, Object> map = new HashMap<>();
        GlobalConfig.log_api.info("=====sourceRuleList:" + sourceRuleList);
        // 步骤1：如果医生不为空，优先匹配医生，一旦匹配上就结束
        if(doctorId != null && !"".equals(doctorId)) {
            for (Object o : sourceRuleList) {
                ruleMap = (Map<String, Object>) o;

                doctorRule = ruleMap.get("doctorIdList").toString();//专家医生列表
                if (doctorRule.contains(doctorId)) {
                    return (List<Map<String, Object>>) ruleMap.get("List");
                }
            }
        }
        
        // 步骤2：先同时匹配科室+号别+默认医生##，一旦匹配上就结束
        for (Object o : sourceRuleList) {
            ruleMap = (Map) o;
            deptRule = ruleMap.get("deptIdList").toString();
            regTypeRule = ruleMap.get("registerType").toString();
            if (registerType.equals(regTypeRule) && deptRule.contains(deptId)) {
                return (List<Map<String, Object>>) ruleMap.get("List");
            }
        }
        // 步骤3：匹配号别 + 通用科室##+默认医生##，一旦匹配上就结束
        for (Object o : sourceRuleList) {
            ruleMap = (Map<String, Object>) o;
            deptRule = ruleMap.get("deptIdList").toString();
            regTypeRule = ruleMap.get("registerType").toString();
            if (registerType.equals(regTypeRule) && "##".equals(deptRule)) {
                return (List<Map<String, Object>>) ruleMap.get("List");
            }
        }
        // 步骤4：匹配通用号别## + 通用科室##+默认医生##，一旦匹配上就结束
        for (Object o : sourceRuleList) {
            ruleMap = (Map) o;
            deptRule = ruleMap.get("deptIdList").toString();
            regTypeRule = ruleMap.get("registerType").toString();
            if ("##".equals(regTypeRule) && "##".equals(deptRule)) {
                return (List<Map<String, Object>>) ruleMap.get("List");
            }
        }
        return null;
    }

    /**
     * 匹配号源明细规则
     *
     * @param sourceRuleList 号源特殊规则
     * @param ruleId         被匹配的规则ID
     * @param noon           午别
     */
    private List<Map<String, Object>> matchAdvanceRule(List advanceList, String noon) {
        List<Map<String, Object>> ruleList = new ArrayList<>();//匹配规则
        for (Object o : advanceList) {
            Map<String, Object> advanceMap = (Map<String, Object>) o;
            
            if ("1".equals(noon)) {// 上午规则
                if (Integer.parseInt(advanceMap.get("etime").toString()) <= 1200) {
                    ruleList.add(advanceMap);
                } else {
                    continue;
                }
            } else if ("2".equals(noon)) {// 下午规则
                if (Integer.parseInt(advanceMap.get("etime").toString()) > 1200) {
                    ruleList.add(advanceMap);
                } else {
                    continue;
                }
            }
        }
        return ruleList;
    }

    // 获取从今天起，days天后的日期，格式yyyyMMdd
    private String getAnyDate(int days) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(date.getTime() + (long) (1000 * 60 * 60 * 24 * days)));
    }

}
