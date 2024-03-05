package com.gg.midend.service;

import com.gg.midend.domain.DTO.AppraisalReq;

/**
 * 医生护工评价服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-03
 */
public interface AppraisalService {

    /**
     * 新建医生护工评价
     *
     * @param appraisalReq 评价信息
     * @return flow_no 评价号
     */
    Long intAppraisalInfo(AppraisalReq appraisalReq);

    /**
     * 删除医生护工评价
     *
     * @param flowNo 评价号
     * @return num 更新记录条数
     */
    int delAppraisalInfo(Long flowNo);
}
