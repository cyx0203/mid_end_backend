package com.gg.midend.controller;

import cn.hutool.core.convert.Convert;
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
 * 出入信息登记
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcare-api/access")
public class AccessController extends GGBaseController {

    private final SqlService sqlService;

    /**
     * 出入信息登记
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/creatRecord")
    public Object creatRecord(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("creatRecord =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));
        try {
            // 校验必传字段
            String[] needParams = {"cardId", "outTime", "planInTime"};
            MapUtil.checkParams(needParams, reqMap);
            // 更新其它记录为失效
            sqlService.update("accessRecords", "updRecordStatus", reqMap);

            int ret2 = sqlService.update("accessRecords", "intAccessRecord", reqMap);

            if (ret2 == 0) {
                GlobalConfig.log_api.info("出入信息登记失败");
                throw new Exception("出入信息登记登记失败");
            } else {
                respBodyMap.put("num", ret2);
                GlobalConfig.log_api.info("出入信息登记登记成功");
            }

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 出入信息查询
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/qryRecord")
    public Object qryRecord(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("qryRecord =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

        try {
            // 校验必传字段
            String[] needParams = {"cardId"};
            MapUtil.checkParams(needParams, reqMap);

            List<?> accessRecordList = sqlService.selectList("accessRecords", "selAccessRecord", reqMap);

            respBodyMap.put("listInfo", accessRecordList);
            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 出入信息更新状态
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/modifyRecord")
    public Object modifyRecord(@RequestBody Map<String, Object> paramMap) throws ApiException {

        GlobalConfig.log_api.info("modifyRecord =================================================>");
        GlobalConfig.log_api.info("入参: " + paramMap);

        // 返回报文
        Map<String, Object> respBodyMap = new HashMap<>();
        // 请求报文正文，作为 mybatis 查询的数据源
        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

        try {
            // 校验必传字段
            String[] needParams = {"id","actualInTime","accessStatus"};
            MapUtil.checkParams(needParams, reqMap);

            int ret = sqlService.update("accessRecords", "updAccessRecord", reqMap);

            // 设置返回报文正文
            respBodyMap.put("num", ret);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }
}
