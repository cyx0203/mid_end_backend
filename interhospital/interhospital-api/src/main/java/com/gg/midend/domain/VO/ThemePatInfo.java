package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-15
 **/
@Data
public class ThemePatInfo {


    /**
     * 问诊主题id
     */
    private Long themeId;

    /**
     * 医院id
     */
    private String hospitalId;

    /**
     * 主题状态
     * 1-未接诊;0-问诊中;1-结帖;2-取消问诊
     */
    private Integer themeStatus;

    /**
     * 问诊类型
     */
    private String themeType;

    /**
     * 最新回复的条目ID
     */
    private Long newMsgId;

    /**
     * 上次问诊ID
     */
    private Long lastThemeId;

    /**
     * 最后回复日期
     */
    private String msgDate;

    /**
     * 最后回复时间
     */
    private String msgTime;

    /**
     * 患者id
     */
    private String patId;

    /**
     * 患者姓名
     */
    private String patName;

    /**
     * 消息内容
     */
    private String msg;
}
