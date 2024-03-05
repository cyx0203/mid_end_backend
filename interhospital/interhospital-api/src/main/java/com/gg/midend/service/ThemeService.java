package com.gg.midend.service;

import com.gg.midend.domain.DTO.ModifyThemeMsgReq;
import com.gg.midend.domain.DTO.QryThemeAppReq;
import com.gg.midend.domain.DTO.QryThemePatReq;
import com.gg.midend.domain.DTO.ThemeReq;
import com.gg.midend.domain.VO.ThemeAppInfo;
import com.gg.midend.domain.VO.ThemeInfo;
import com.gg.midend.domain.VO.ThemePatInfo;
import com.gg.midend.domain.VO.ThemeStatics;

import java.util.List;

/**
 * 问诊主题相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-17
 */
public interface ThemeService {

    /**
     * 创建问诊主题
     * 插入新问诊主题记录
     *
     * @param themeReq 主题信息
     * @return themeId
     */
    Long intThemeInfo(ThemeReq themeReq);

    /**
     * 结帖
     * 更新问诊状态为结帖
     *
     * @param themeId 主题号
     * @return num 更新记录条数
     */
    int stopTheme(Long themeId);

    /**
     * 查询单条主题信息
     * 根据主题号查询主题信息
     *
     * @param themeId 主题号
     * @return ThemeInfo
     */
    ThemeInfo selThemeInfo(Long themeId);

    /**
     * 更新新消息
     * 根据主题号更新新消息标志及问诊倒计等字段
     *
     * @param modifyThemeMsgReq 主题号等字段
     * @return num 更新记录条数
     */
    int updThemeMsg(ModifyThemeMsgReq modifyThemeMsgReq);

    /**
     * 查询主题信息
     *
     * @param reqVO 主题号
     * @return ThemePatInfo 主题信息列表
     */
    List<ThemePatInfo> queryThemePatInfo(QryThemePatReq reqVO);

    /**
     * 查询问诊统计信息
     *
     * @param doctId 医生id
     * @return ThemeStatics 统计信息
     */
    ThemeStatics staticsStatus(String doctId);

    /**
     * 查询主题及预约信息
     *
     * @param reqVO 主题信息
     * @return ThemeAppInfo 主题信息列表
     */
    List<ThemeAppInfo> qryThemeAppInfo(QryThemeAppReq reqVO);
}
