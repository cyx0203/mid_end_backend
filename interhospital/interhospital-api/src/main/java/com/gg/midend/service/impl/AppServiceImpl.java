package com.gg.midend.service.impl;

import com.gg.midend.domain.DTO.AppReq;
import com.gg.midend.mapper.AppMapper;
import com.gg.midend.mapper.ThemeMapper;
import com.gg.midend.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import static com.gg.midend.exception.enums.GlobalErrorCodeConstants.DATABASE_RECORD_REPEAT;
import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 实现类
 * 预约信息服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final AppMapper appMapper;

    private final ThemeMapper themeMapper;

    @Override
    public Long intAppInfo(AppReq appReq) throws DuplicateKeyException {

        try {
            appMapper.intAppInfo(appReq);
            themeMapper.updThemeName(appReq.getThemeId(), appReq.getThemeName(),appReq.getPurpose());
        } catch (DuplicateKeyException e) {
            throw exception(DATABASE_RECORD_REPEAT);
        }
        return appReq.getThemeId();
    }
}
