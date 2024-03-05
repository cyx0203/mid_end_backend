package com.gg.midend.entity.vo;

import lombok.Data;

/**
 * 登录返回数据
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-08
 **/
@Data
public class LoginVO {

    /**
     * 二维码信息
     */
    private String qrCode;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 签名类型
     */
    private String signType;
}
