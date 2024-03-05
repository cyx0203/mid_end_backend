package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 修改登录密码入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-24
 **/
@Data
public class ModifyPwdReq {

    /**
     * 医院id
     */
    @NotBlank(message = "医院id不能为空")
    private String hospitalId;

    /**
     * 医生id
     */
    @NotBlank(message = "医生id不能为空")
    private String doctId;

    /**
     * 原始密码
     */
    @NotBlank(message = "原始密码不能为空")
    private String origInquiryPsd;

    /**
     * 修改密码
     */
    @NotBlank(message = "修改密码不能为空")
    private String inquiryPsd;
}
