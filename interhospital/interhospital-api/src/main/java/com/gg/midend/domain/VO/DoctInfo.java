package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 实体类
 * 问诊医生信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-15
 **/
@Data
public class DoctInfo {

    /**
     * 医院编号
     */
    private String hospitalId;

    /**
     * 医生编号
     */
    private String doctorCode;

    /**
     * 科室编号
     */
    private String deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 医生头像图片路径
     */
    private String imagePath;

    /**
     * 医生介绍
     */
    private String doctorDesc;

    /**
     * 医生职称
     */
    private String doctorLevel;

    /**
     * 关注次数
     */
    private Integer doctorFollow;

    /**
     * 问诊次数
     */
    private Integer doctorAsk;

    /**
     * 评价平均分
     */
    private Integer doctorScore;

    /**
     * 激活标志
     */
    private String doctActive;
}
