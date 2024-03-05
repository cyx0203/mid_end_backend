package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 查询问诊主题入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-22
 **/
@Data
public class QryThemeAppReq {

    /**
     * 患者id
     */
    @NotBlank(message = "患者id不能为空")
    private String patId;

    /**
     * 有效标志
     */
    @NotBlank(message = "有效标志不能为空")
    private String available;

    /**
     * 主题id
     */
    private Long themeId;

}
