package com.gg.midend.entity.pojo;

import lombok.Data;

/**
 *  调用CA返回数据头信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-07
 **/
@Data
public class ResponseData {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误提示
     */
    private String info;
}
