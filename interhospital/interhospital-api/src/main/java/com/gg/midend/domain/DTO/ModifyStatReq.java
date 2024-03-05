package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 修改登录状态入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class ModifyStatReq {

    /**
     * 医院id
     */
    @NotBlank(message = "医院id不能为空")
    private String hospitalId;

    /**
     * 医生id
     */
    @NotBlank(message = "医生id不能为空")
    private String doctorCode;

    /**
     * 修改状态
     */
    @NotBlank(message = "医生修改状态不能为空")
    private String inquiryStatus;
}
