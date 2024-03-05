package com.gg.midend.utils;

import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GeneralRecordUtil {

    @Autowired
    private SqlService sqlService;

    /**
     * 描述：插入记录/更新记录
     * 可用于：订单流水表
     * @param namespace mapper 命名空间
     * @param sqlId mapper sql_id
     * @param reqMap 所需参数
     * @param failMsg 可能的错误信息
     * @param successMsg 可能的成功信息
     */
    public void recordDetail(String namespace, String sqlId, Map<String, Object> reqMap,
                                            String failMsg, String successMsg) throws Exception {
        int ret = sqlService.update(namespace, sqlId, reqMap);
        if (ret < 1) {
            GlobalConfig.log_api.info(failMsg + ", update 未匹配到记录");
            throw new Exception(failMsg);
        } else {
            GlobalConfig.log_api.info(successMsg);
        }
    }

}
