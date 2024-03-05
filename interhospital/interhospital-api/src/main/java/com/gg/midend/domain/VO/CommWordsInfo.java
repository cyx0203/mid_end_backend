package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 医生常用语信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class CommWordsInfo {

    /**
     * 医院编号
     */
    private String hospitalId;

    /**
     * 医生编号
     */
    private String doctId;

    /**
     * 类型
     */
    private String type;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
