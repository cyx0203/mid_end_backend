package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.AppraisalReq;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper
 * 医生护工评价管理
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-03
 **/
@Mapper
public interface AppraisalMapper{

    Long intAppraisalInfo(AppraisalReq appraisalReq);

    int delByFlowNo(Long flowNo);

}
