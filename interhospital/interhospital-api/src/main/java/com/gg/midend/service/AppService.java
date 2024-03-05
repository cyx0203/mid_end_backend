package com.gg.midend.service;

import com.gg.midend.domain.DTO.AppReq;

/**
 * 预约信息服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 */
public interface AppService {

    /**
     * 插入预问诊信息
     *
     * @param appReq 预问诊请求信息
     * @return themeId 主题号
     */
    Long intAppInfo(AppReq appReq);
}
