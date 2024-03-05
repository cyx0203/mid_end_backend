package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 实体类
 * 撤回消息入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Data
public class RecallMsgReq {

    /**
     * 主题id
     */
    @NotNull(message = "主题id不能为空")
    private Long themeId;

    /**
     * 待回复消息id
     */
    @NotNull(message = "待回复消息id不能为空")
    private Long msgId;

    /**
     * 消息状态
     * 0-撤销;1-未读;2-已读
     */
    @NotNull(message = "消息状态不能为空")
    private Integer status;
}
