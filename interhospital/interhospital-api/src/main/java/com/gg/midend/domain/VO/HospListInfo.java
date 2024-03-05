package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 实体类
 * 医院登录列表信息
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Data
public class HospListInfo {

    /**
     * 医生编号
     */
    private String hospitalId;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 一级机构号
     */
    private String deptId1;

    /**
     * 一级机构名称
     */
    private String deptName1;

    /**
     * 二级机构号
     */
    private String deptId2;

    /**
     * 二级机构名称
     */
    private String deptName2;

}
