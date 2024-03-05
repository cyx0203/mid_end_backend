package com.gg.midend.controller.src;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.HISRequestUtil;
import com.gg.midend.utils.SendMsgUtil;
import com.gg.midend.utils.YQRequestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: 樊亦村
 * Created time: 2023-02-25 08:00:00
 * Description: 停诊排班控制类
 */
@RestController
public class StopController {

    @Autowired
    private SqlService sqlService;

    // 停诊排班
    @RequestMapping(value = "/manage/src.stop")
    @Transactional(rollbackFor = {Exception.class})
    public Object stop(@RequestBody Map<String, Object> paramMap) throws Exception {
        String srcSchId = (String) paramMap.get("scheduleId");
        // 步骤1：更新src_schedule表的active
        Map<String, Object> updateScheduleMap = new HashMap<>();
        updateScheduleMap.put("id", srcSchId);
        updateScheduleMap.put("active", '0');
        int ret = sqlService.update("srcSchedule", "updateStop", updateScheduleMap);
        if (ret < 1) {
            throw new Exception("号源已停诊！");
        }
        
        // 步骤2：插入src_stop_record表
        paramMap.put("srcSchId", srcSchId);
        paramMap.put("stopType", "1"); //停诊
        sqlService.update("srcStopRecord", "insert", paramMap);
        
        // 步骤3：查询有多少患者已经挂号了
        List<?> patList = sqlService.selectList("srcOrder", "selectHavingRegister", paramMap);
        
        StringBuffer phoneList = new StringBuffer(); //患者手机号码列表
        // 步骤4：将已经挂号了的患者插入stop_notice_list表
        if (!patList.isEmpty()) {
            for (Object o : patList) {
                Map<String, Object> patMap = (Map) o;
                patMap.put("recordId", paramMap.get("id"));
                patMap.put("tip", "1");
                phoneList.append(patMap.get("userPhone")).append(",");
            }
            Map<String, Object> insertNoticeList = new HashMap<>();
            insertNoticeList.put("list", patList);
            sqlService.update("srcStopNotice", "insertBatch", insertNoticeList);
        }

        // 步骤5：通知HIS停诊 start
        // 构造入参, xml 格式
        JSONObject reqJson = JSONUtil.createObj()
                .set("scheduleId", srcSchId)
                .set("action", "1");
        // 发送请求
        String response = HISRequestUtil.requestHIS("YQ_stopNotice", reqJson);
        // 返回 xml 格式报文，转为 json 字符串
        JSONObject respJson = JSONUtil.xmlToJson(response);
        // 判断请求结果，失败则抛出异常，成功取 payFee
        if (!"0".equals(respJson.getJSONObject("response").getStr("returnCode"))) {
            throw new Exception(respJson.getJSONObject("response").getStr("returnMsg"));
        }
        // 步骤5：通知HIS停诊 end
        
        //步骤6：判断是否有号源且已挂是否为当日号停诊，当日号只发送短信提示窗口退款，非当日停诊，短信通知+源启退号
        if(!patList.isEmpty()) {
            List<String> sendData = new ArrayList<>(); // 定义内容
            String date = (String) paramMap.get("date");
            String noon = "1".equals(paramMap.get("noon")) ? "上午" : "2".equals(paramMap.get("noon")) ? "下午" : "";
            StringBuffer sb = new StringBuffer();
            sb.append(date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6) + noon);
            sb.append("[" + paramMap.get("deptName").toString().replaceAll(" ", "") + "/");
            sb.append(paramMap.get("registerTypeName").toString().replaceAll(" ", ""));
            sb.append(paramMap.get("doctorName") == null ? "]" : "/" + paramMap.get("doctorName").toString().replaceAll(" ", "") + "]");
            //6.1发送通知短信
            sendData.add(sb.toString());//添加信息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            if(!sdf.format(new Date()).equals(date)) { //停诊预约号, 只发短信
                SendMsgUtil.notify("appointment", phoneList.toString(), sendData);
                //6.2.1调用源启进行退费
                for (Object p : patList) {
                    YQRequestUtil.requestYQ("orgine.powerrsp.service.overt.reserve.returnGoodsOrder", (Map) p);
                }
            }else {
                SendMsgUtil.notify("today", phoneList.toString(), sendData);
            }
        }
        
        return 1;
    }

    // 重启停诊排班
    @RequestMapping(value = "/manage/src.reboot")
    @Transactional(rollbackFor = {Exception.class})
    public Object reboot(@RequestBody Map<String, Object> paramMap) throws Exception {
        String srcSchId = (String) paramMap.get("scheduleId");
        // 步骤1：更新stop_record表，将reboot设置成1
        Map<String, Object> updateStopMap = new HashMap<>();
        updateStopMap.put("srcSchId", srcSchId);
        sqlService.update("srcStopRecord", "rebootRecord", updateStopMap);
        // 步骤2：更新src_schedule表的active
        Map<String, Object> updateScheduleMap = new HashMap<>();
        updateScheduleMap.put("id", srcSchId);
        updateScheduleMap.put("active", '1');
        sqlService.update("srcSchedule", "update", updateScheduleMap);
        // 步骤3：通知HIS重启 start
        // 构造入参, xml 格式
        JSONObject reqJson = JSONUtil.createObj()
                .set("scheduleId", srcSchId)
                .set("action", "2");
        // 发送请求
        String response = HISRequestUtil.requestHIS("YQ_stopNotice", reqJson);
        // 返回 xml 格式报文，转为 json 字符串
        JSONObject respJson = JSONUtil.xmlToJson(response);
        // 判断请求结果，失败则抛出异常，成功取 payFee
        if (!"0".equals(respJson.getJSONObject("response").getStr("returnCode"))) {
            throw new Exception(respJson.getJSONObject("response").getStr("returnMsg"));
        }
        // 步骤3：通知HIS重启 end
        return 1;
    }
    
    // 停号
    @RequestMapping(value = "/manage/src.subSource")
    @Transactional(rollbackFor = {Exception.class})
    public Object subSource(@RequestBody Map<String, Object> paramMap) throws Exception {
        String srcSchId = (String) paramMap.get("scheduleId");
        // 步骤1：更新src_source表的num_type
        Map<String, Object> updateSourceMap = new HashMap<>();
        updateSourceMap.put("srcSchId", srcSchId); //排班ID
        updateSourceMap.put("numType", '3');//0-普通号；1-保留号；2-附加号 3-停号
        updateSourceMap.put("startOrder", paramMap.get("startOrder"));
        updateSourceMap.put("endOrder", paramMap.get("endOrder"));
        int ret = sqlService.update("srcSource", "updateStop", updateSourceMap);
        if (ret < 1) {
            throw new Exception("指定号源已停号！");
        }
        
        // 步骤2：插入src_stop_record表
        paramMap.put("srcSchId", srcSchId); //排班ID
        paramMap.put("stopType", "2"); //停号
        sqlService.update("srcStopRecord", "insert", paramMap);


        // 步骤3：通知HIS停诊 start
        // 构造入参, xml 格式
        JSONObject reqJson = JSONUtil.createObj()
                .set("scheduleId", srcSchId)
                .set("action", "3") 
                .set("start", paramMap.get("startOrder"))
                .set("end", paramMap.get("endOrder"));
        // 发送请求
        String response = HISRequestUtil.requestHIS("YQ_stopNotice", reqJson);
        // 返回 xml 格式报文，转为 json 字符串
        JSONObject respJson = JSONUtil.xmlToJson(response);
        // 判断请求结果，失败则抛出异常，成功取 payFee
        if (!"0".equals(respJson.getJSONObject("response").getStr("returnCode"))) {
            throw new Exception(respJson.getJSONObject("response").getStr("returnMsg"));
        }
        // 步骤5：通知HIS停诊 end
        
        return 1;
    }
}
