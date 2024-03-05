package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Data
public class QryMsgReq {

    /**
     * 主题ID
     */
    @NotNull(message = "主题ID不能为空")
    private Long themeId;

    /**
     * 消息来源
     * A-患者;B-医生
     */
    @NotBlank(message = "消息来源不能为空")
    private String msgBy;

    /**
     * 类型
     * 1-图文;2-卡片
     */
    private Integer msgType;
}
