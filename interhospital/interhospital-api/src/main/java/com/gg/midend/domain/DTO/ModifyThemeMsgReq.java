package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 新增消息修改主题请求
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Data
public class ModifyThemeMsgReq {

    /**
     * 主题id
     */
    @NotBlank(message = "主题id不能为空")
    private Long themeId;

    /**
     * 消息来源
     * A-患者;B-医生
     */
    private String msgBy;

    /**
     * 最新回复的条目ID
     */
    private Long newMsgId;

    /**
     * 新消息标志
     * 0-否;1-新提问;2-新回复
     */
    private Integer newReply;

    /**
     * 问诊倒计(次)
     */
    private Integer countdown;

    /**
     * 问诊状态
     */
    private Integer themeStatus;
}
