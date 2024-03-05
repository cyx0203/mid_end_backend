package com.gg.midend.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.util.ExceptionHandler;
import com.gg.midend.util.MapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 病案打印
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcare-api/caseBook")
public class CaseBookController extends GGBaseController {

    private final SqlService sqlService;

    /**
     * 病案打印预约登记
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/creatPrint")
    public Object creatCaseBookPrint(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("creatCaseBookPrint =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));
        try {
            // 校验必传字段
            String[] needParams = {"hospitalId", "patientId", "patientName", "patientNo", "inPatientId", "applyName", "applyIdNo", "applyPhoneNo",
                    "recipientName", "recipientPhoneNo", "recipientCity", "recipientAddress", "recipientPostalCode",
                    "applyPhotoPath", "idFrontPhotoPath", "idBackPhotoPath"};
            MapUtil.checkParams(needParams, reqMap);

            if (ObjUtil.isNotNull(reqMap.get("applyPhotoPath"))) {
                reqMap.put("imgBase", reqMap.get("applyPhotoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("applyPhotoPath", reqMap.get("id"));
            }

            if (ObjUtil.isNotNull(reqMap.get("idFrontPhotoPath"))) {
                reqMap.put("imgBase", reqMap.get("idFrontPhotoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("idFrontPhotoPath", reqMap.get("id"));
            }

            if (ObjUtil.isNotNull(reqMap.get("idBackPhotoPath"))) {
                reqMap.put("imgBase", reqMap.get("idBackPhotoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("idBackPhotoPath", reqMap.get("id"));
            }

            int ret = sqlService.update("caseBook", "intCasebook", reqMap);

            if (ret == 0) {
                GlobalConfig.log_api.info("病案打印预约登记失败");
                throw new Exception("病案打印预约登记失败");
            } else {
                respBodyMap.put("id", reqMap.get("id"));
                GlobalConfig.log_api.info("病案打印预约登记成功");
            }

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 病案打印预约查询
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/qryPrint")
    public Object qryPrint(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("qryPrint =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

        try {
            // 校验必传字段
            String[] needParams = {"hospitalId", "patientId"};
            MapUtil.checkParams(needParams, reqMap);

            List<?> caseBookList = sqlService.selectList("caseBook", "selCaseBook", reqMap);

            // 设置返回报文正文
            respBodyMap.put("listInfo", caseBookList);
            respBodyMap.put("num",caseBookList.size());

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 病案复印更新状态
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/modifyPrint")
    public Object modifyPrint(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("modifyPrint =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

        try {
            // 校验必传字段
            String[] needParams = {"id"};
            MapUtil.checkParams(needParams, reqMap);

            int ret = sqlService.update("caseBook", "updCaseBook", reqMap);

            // 设置返回报文正文
            respBodyMap.put("num", ret);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }
}
