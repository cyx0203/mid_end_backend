package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 实体类
 * 问诊医生信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-15
 **/
@Data
public class DoctPwdInfo {

    /**
     * 医院编号
     */
    private String hospitalId;

    /**
     * 医生编号
     */
    private String doctId;

    /**
     * 激活标志
     */
    private String doctPsd;

    /**
     * 激活标志
     */
    private String doctActive;
}
