package com.gg.midend.mapper;

import com.gg.midend.domain.VO.HospInfo;
import com.gg.midend.domain.VO.HospListInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper
 * 医院相关
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Mapper
public interface HospitalMapper {

    HospInfo selHospInfo(@Param("hospitalId") String hospitalId);

    HospListInfo selHospList(@Param("active") String active);
}
