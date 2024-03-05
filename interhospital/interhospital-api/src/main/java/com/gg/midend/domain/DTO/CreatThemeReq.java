package com.gg.midend.domain.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实体类
 * 新建主题信息入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
@Data
public class CreatThemeReq {

    /**
     * 问诊主题id
     */
    private Long themeId;

    /**
     * 问诊主题名称
     */
    private String themeName;

    /**
     * 医院id
     */
    @NotBlank(message = "医院id不能为空")
    private String hospitalId;

    /**
     * 科室id
     */
    @NotBlank(message = "科室id不能为空")
    private String deptId;

    /**
     * 医生id
     */
    private String doctId;

    /**
     * 患者id
     */
    @NotBlank(message = "患者id不能为空")
    private String patId;

    /**
     * 患者微信的openid
     */
    @NotBlank(message = "患者微信的openid不能为空")
    private String userId;

    /**
     * 主题状态
     * 1-未接诊;0-问诊中;1-结帖;2-取消问诊
     */
    private String themeStatus;

    /**
     * 问诊类型
     */
    private String themeType;

    /**
     * 问诊支付流水
     */
    private String payNo;

    /**
     * 支付标志
     * Y-已支付;N-未支付
     */
    private String payFlag;

    /**
     * 支付金额
     */
    private String payAmt;

    /**
     * 有效标志
     * Y-是;N-否
     */
    private String available;

    /**
     * 新消息标志
     * 0-否,1-新提问,2-新回复
     */
    private String newReply;

    /**
     * 最新回复的条目ID
     */
    private String newMsgId;

    /**
     * 问诊倒计
     */
    private String countdown;

    /**
     * 上次问诊ID
     */
    private String lastThemeId;

    /**
     * 预约资源id
     */
    private String sourceId;

    /**
     * 问诊目的
     */
    private String purpose;

    /**
     * 备注1
     */
    private String remark1;

    /**
     * 备注2
     */
    private String remark2;

    /**
     * 备注3
     */
    private String remark3;
}
