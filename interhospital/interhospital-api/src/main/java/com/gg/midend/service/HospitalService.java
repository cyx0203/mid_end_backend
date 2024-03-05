package com.gg.midend.service;

import com.gg.midend.domain.VO.HospInfo;
import com.gg.midend.domain.VO.HospListInfo;

/**
 * 医院相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 */
public interface HospitalService {

    /**
     * 查询医院信息
     *
     * @param hospitalId 医院编号
     * @return HospInfo 医院信息
     */
    HospInfo qryHosInfo(String hospitalId);

    /**
     * 查询登录医院列表
     *
     * @param active 医院编号
     * @return HospInfo 医院信息
     */
    HospListInfo getLoginHospital(String active);
}
