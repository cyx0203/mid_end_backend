package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 实体类
 * 主题消息记录
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Data
public class ThemeMsgInfo {

    /**
     * 消息id
     */
    private String id;

    /**
     * 主题id
     */
    private Long themeId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 图片地址
     */
    private String msgImageAddress;

    /**
     * 消息来源
     */
    private String msgBy;

    /**
     * 回复消息ID
     */
    private String orgId;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 卡片内容
     */
    private String card;

    /**
     * 消息状态
     */
    private Integer state;

    /**
     * 最后一条消息日期
     */
    private String msgDate;

    /**
     * 最后一条消息时间
     */
    private String msgTime;

    /**
     * 备注1
     */
    private String remark1;

    /**
     * 备注2
     */
    private String remark2;

    /**
     * 备注3
     */
    private String remark3;
}
