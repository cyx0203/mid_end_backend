package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 医院相关信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class HospInfo {

    /**
     * 医院编号
     */
    private String hospitalId;

    /**
     * 医生编号
     */
    private String hospitalName;

    /**
     * 上级医院编号
     */
    private String parId;

    /**
     * 医院层级
     */
    private String level;

    /**
     * 医院类型
     */
    private String type;

    /**
     * 医院级别（一、二、三）级
     */
    private String hosClass;

    /**
     * 医院级别（甲、乙、丙）等
     */
    private String grade;

    /**
     * 医院电话
     */
    private String phone;

    /**
     * 医院地址
     */
    private String address;

    /**
     * 医院归属机构
     */
    private Integer branchId;
}
