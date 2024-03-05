package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.ModifyThemeMsgReq;
import com.gg.midend.domain.DTO.QryThemeAppReq;
import com.gg.midend.domain.DTO.QryThemePatReq;
import com.gg.midend.domain.DTO.ThemeReq;
import com.gg.midend.domain.VO.ThemeAppInfo;
import com.gg.midend.domain.VO.ThemeInfo;
import com.gg.midend.domain.VO.ThemePatInfo;
import com.gg.midend.domain.VO.ThemeStatics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 * 问诊主题相关
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-13
 **/
@Mapper
public interface ThemeMapper {

    int updThemeName(@Param("themeId") Long themeId, @Param("themeName") String themeName, @Param("purpose") String purpose);

    int updThemeMsg(ModifyThemeMsgReq modifyThemeMsgReq);

    int intThemeInfo(ThemeReq themeReq);

    int stopTheme(Long themeId);

    ThemeInfo selThemeInfo(Long themeId);

    List<ThemePatInfo> queryThemePatInfo(QryThemePatReq reqVO);

    ThemeStatics staticsStatus(String doctId);

    List<ThemeAppInfo> selThemeAppInfo(QryThemeAppReq qryThemeAppReq);
}
