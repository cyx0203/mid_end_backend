package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.AppReq;

/**
 * 预约问诊Mapper
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
public interface AppMapper {

    int intAppInfo(AppReq appReq);
}
