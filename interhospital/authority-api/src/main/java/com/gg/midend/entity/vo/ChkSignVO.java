package com.gg.midend.entity.vo;

import lombok.Data;

/**
 * 轮询检查数据
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-09
 **/
@Data
public class ChkSignVO {

    /**
     * 员工工号
     */
    private String jobNum;

    /**
     * 签名值
     */
    private String signData;

    /**
     * 签名验签结果
     */
    private String signResult;
}
