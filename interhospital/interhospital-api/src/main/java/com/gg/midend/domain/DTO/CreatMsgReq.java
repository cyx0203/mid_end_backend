package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增消息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Data
public class CreatMsgReq {

    /**
     * 消息id
     */
    private Long msgId;

    /**
     * 问诊主题id
     */
    @NotNull(message = "问诊主题id不能为空")
    private Long themeId;

    /**
     * 医院id
     */
    private String hospitalId;

    /**
     * 医生的id
     */
    private String receiver;

    /**
     * 主题名
     */
    private String theme;

    /**
     * 消息来源：
     * A-患者;B-医生
     */
    @NotBlank(message = "消息来源不能为空")
    private String msgBy;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 类型
     */
    @NotNull(message = "主题类型不能为空")
    private Integer type;

    /**
     * 卡片内容
     */
    private String card;

    /**
     * 图片文件的地址
     */
    private String msgImageAddress;

    /**
     * 现剩余次数
     */
    private Integer countdown;

    /**
     * 引用消息ID
     */
    private Long orgId;
}
