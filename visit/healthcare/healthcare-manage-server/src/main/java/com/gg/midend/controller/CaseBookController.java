package com.gg.midend.controller;

import com.alibaba.fastjson.JSONObject;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 病案打印
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcare-manage/caseBook")
public class CaseBookController{

    private final SqlService sqlService;

    /**
     * HIS划价接口
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/pricing")
    public Object pricing(@RequestBody Map<String, Object> paramMap) {

        // 返回报文
        Map<String, Boolean> returnMap = new HashMap<>();
        Map<String, Object> hisBodyMap = new HashMap<>();
        try {

            Map patientInfo = (Map) sqlService.selectOne("caseBook", "selCaseBook", paramMap);

            Map userInfo = (Map) sqlService.selectOne("sysUser", "selSysUser", paramMap);

            hisBodyMap.put("IdCardNo",patientInfo.get("patientNo"));
            hisBodyMap.put("pat_name",patientInfo.get("patientName"));
            hisBodyMap.put("inpatient_seq",patientInfo.get("inPatientId"));
            hisBodyMap.put("Number",paramMap.get("printNum"));
            hisBodyMap.put("Punter",userInfo.get("name"));

            HttpUtils httpUtils = new HttpUtils();
            JSONObject result = httpUtils.sendRequest(hisBodyMap);

            int ret = 0;
            if("A".equals(result.get("exeStatus").toString())){
                paramMap.put("checkAccount",userInfo.get("name"));
                paramMap.put("originalPid",result.get("pid"));
                paramMap.put("originalNo",result.get("No"));
                paramMap.put("status","1");
                ret = sqlService.update("caseBook", "updCaseBookStatus", paramMap);
            }
            if (ret == 0){
                returnMap.put("success",false);
            }else {
                returnMap.put("success",true);
            }
            return returnMap;
        } catch (Exception e) {
            returnMap.put("success",false);
            return returnMap;
        }
    }
}
