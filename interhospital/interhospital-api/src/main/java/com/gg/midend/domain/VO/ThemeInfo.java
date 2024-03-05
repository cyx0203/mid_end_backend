package com.gg.midend.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类-出参数据对象
 * 查询问诊主题
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-15
 **/
@Data
public class ThemeInfo {

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
    private String hospitalId;

    /**
     * 科室id
     */
    private String deptId;

    /**
     * 医生id
     */
    private String doctId;

    /**
     * 患者id
     */
    private String patId;

    /**
     * 患者微信的openid
     */
    private String userId;

    /**
     * 主题状态
     * 0-问诊中;1-结帖;2-取消问诊;3-未接诊
     */
    private Integer themeStatus;

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
    private BigDecimal payAmt;

    /**
     * 有效标志
     * Y-是;N-否
     */
    private String available;

    /**
     * 新消息标志
     * 0-否,1-新提问,2-新回复
     */
    private Integer newReply;

    /**
     * 最新回复的条目ID
     */
    private Long newMsgId;

    /**
     * 问诊倒计
     */
    private Integer countdown;

    /**
     * 上次问诊ID
     */
    private Long lastThemeId;

    /**
     * 预约资源id
     */
    private String sourceId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

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
