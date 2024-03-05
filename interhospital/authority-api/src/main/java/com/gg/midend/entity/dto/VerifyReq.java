package com.gg.midend.entity.dto;

import lombok.Data;

/**
 * 验证时间戳上送数据
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-09
 **/
@Data
public class VerifyReq {

    /**
     * 未知参数
     */
    private String signedData;

    /**
     * 未知参数
     */
    private String verifyData;
}
