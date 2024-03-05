package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 新增常用语信息入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class CreatWordsReq {

    /**
     * 医院编号
     */
    @NotBlank(message = "医院ID不能为空")
    private String hospitalId;

    /**
     * 医生编号
     */
    @NotBlank(message = "医生ID不能为空")
    private String doctorCode;

    /**
     * 类型
     */
    @NotBlank(message = "医生常用语类型不能为空")
    private String type;

    /**
     * 内容
     */
    private String content;
}
