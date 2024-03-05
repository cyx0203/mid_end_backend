package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 医生登录入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class DoctLoginReq {

    /**
     * 医生ID
     */
    @NotBlank(message = "医生ID不能为空")
    private String fId;

    /**
     * 医生登录密码
     */
    @NotBlank(message = "医生登录密码不能为空")
    private String fPasswd;

    /**
     * 医院编号
     */
    @NotBlank(message = "医院编号不能为空")
    private String fHospitalid;
}
