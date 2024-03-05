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
 * 就诊陪护
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcare-api/careCard")
public class CareCardController extends GGBaseController{

    private final SqlService sqlService;

    /**
     * 就诊陪护申请登记
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/creatCareCard")
    public Object creatCareCard(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("creatCareCard =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));
        try {
            // 校验必传字段
            String[] needParams = {"hospitalId", "patientId", "inPatientId", "idNo", "name",
                    "phoneNo", "photoPath", "idPhotoPath" , "userId"};
            MapUtil.checkParams(needParams, reqMap);

            List<?> careCardList = sqlService.selectList("careCard", "selCardIdByUserId", reqMap);

            if (careCardList.size() >= 1) {
                GlobalConfig.log_api.info("已存在有效的就诊陪护证，无法新增");
                throw new Exception("已存在有效的就诊陪护证，无法新增");
            }

            if(ObjUtil.isNotNull(reqMap.get("photoPath"))){
                reqMap.put("imgBase",reqMap.get("photoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("photoPath",reqMap.get("id"));
            }

            if(ObjUtil.isNotNull(reqMap.get("idPhotoPath"))){
                reqMap.put("imgBase",reqMap.get("idPhotoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("idPhotoPath",reqMap.get("id"));
            }

            if(ObjUtil.isNotNull(reqMap.get("checkPhotoPath"))){
                reqMap.put("imgBase",reqMap.get("checkPhotoPath"));
                int ret = sqlService.update("carePhoto", "intPhoto", reqMap);
                reqMap.put("checkPhotoPath",reqMap.get("id"));
            }

            int ret = sqlService.update("careCard", "intCareCard", reqMap);

            if (ret == 0) {
                GlobalConfig.log_api.info("就诊陪护申请登记失败");
                throw new Exception("就诊陪护申请登记失败");
            } else {
                respBodyMap.put("cardId", reqMap.get("cardId"));
                GlobalConfig.log_api.info("病案打印预约登记成功");
            }

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 就诊陪护申请查询
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/qryCareCard")
    public Object qryCareCardInfo(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("qryCareCard =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

        try {
            // 校验必传字段
            String[] needParams = {"hospitalId", "userId"};
            MapUtil.checkParams(needParams, reqMap);

            List<?> careCardList = sqlService.selectList("careCard", "selCareCard", reqMap);

            respBodyMap.put("listInfo", careCardList);
            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }
}
