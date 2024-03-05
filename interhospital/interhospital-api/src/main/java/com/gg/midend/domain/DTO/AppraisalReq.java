package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 实体类
 * 新增医生护工评价入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-03
 **/
@Data
public class AppraisalReq {

    /**
     * 评价号
     * yyyyMMddHHmm+随机6位
     */
    private Long flowNo;

    /**
     * 评价类型
     * 1-问诊;2-挂号;3-住院;4-关注;9-其他
     */
    @NotNull(message = "评价类型不能为空")
    private Integer flowType;

    /**
     * 医院编号
     */
    @NotBlank(message = "医院编号不能为空")
    private String hospitalId;

    /**
     * 医生id
     */
    @NotBlank(message = "医生id不能为空")
    private String doctId;

    /**
     * 患者id
     */
    private String patId;

    /**
     * 患者id
     */
    private String userId;

    /**
     * 患者姓名
     */
    private String patName;

    /**
     * 评价分数
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 激活标志
     * 0-未激活;1-激活
     */
    private Integer status;

    /**
     * 评价标志
     * 0-未完成;1-完成
     */
    private Integer flag;

    /**
     * 评价内容
     */
    private String remark1;
    /**
     * 评价内容
     */
    private String remark2;
}
