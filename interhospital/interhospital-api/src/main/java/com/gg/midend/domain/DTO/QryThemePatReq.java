package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 查询问诊主题入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-15
 **/
@Data
public class QryThemePatReq {

    @NotBlank(message = "医院id不能为空")
    private String hospitalId;

    @NotBlank(message = "医生id不能为空")
    private String doctId;

    private String payFlag;

}
