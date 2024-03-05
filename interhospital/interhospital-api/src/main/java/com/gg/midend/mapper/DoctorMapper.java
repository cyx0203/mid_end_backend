package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.ModifyPwdReq;
import com.gg.midend.domain.DTO.ModifyStatReq;
import com.gg.midend.domain.VO.DoctInfo;
import com.gg.midend.domain.VO.DoctPwdInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper
 * 医生相关
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Mapper
public interface DoctorMapper {

    DoctPwdInfo selDoctPwd(@Param("hospitalId") String hospitalId, @Param("doctId") String doctId);

    DoctInfo selDoctInfo(@Param("hospitalId") String hospitalId, @Param("doctId") String doctId);

    int updDoctPwd(ModifyPwdReq modifyPwdReq);

    int updDoctStat(ModifyStatReq modifyStatReq);
}
