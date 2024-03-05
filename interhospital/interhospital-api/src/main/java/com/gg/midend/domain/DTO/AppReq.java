package com.gg.midend.domain.DTO;

import com.gg.core.annotation.IntEnum;
import com.gg.core.validation.enums.SexEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 实体类
 * 预约问诊请求信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
@Data
public class AppReq {

    /**
     * 问诊主题id
     */
    @NotNull(message = "问诊主题id不能为空")
    private Long themeId;

    /**
     * 问诊主题名字
     */
    private String themeName;

    /**
     * 患者姓名
     */
    @NotBlank(message = "患者姓名不能为空")
    private String patName;

    /**
     * 患者性别
     * 1-男;2-女;3-未知
     */
    @IntEnum(message = "性别枚举校验不通过 1-男，2-女，3-未知",target = SexEnum.class)
    private Integer patSex;

    /**
     * 患者年龄
     */
    @NotNull(message = "患者年龄不能为空")
    private Integer patAge;

    /**
     * 本此问诊的主要目的
     */
    private String purpose;

    /**
     * 既往史
     */
    private String history;

    /**
     * 过敏史
     */
    private String allergy;

    /**
     * 家族史
     */
    private String family;

    /**
     * 现病史
     */
    private String now;

    /**
     * 个人习惯
     */
    private String habits;

    /**
     * 状态
     * 0-未修改;1-已修改
     */
    private Integer state;

    /**
     * 图片列表，以“|”分隔
     */
    private String picPath;

    /**
     * 备注
     */
    private String remark1;
}
