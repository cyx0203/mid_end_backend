package com.gg.midend.controller.interhospital;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.DTO.QryThemeAppReq;
import com.gg.midend.domain.DTO.QryThemePatReq;
import com.gg.midend.domain.DTO.ThemeReq;
import com.gg.midend.domain.VO.ThemeAppInfo;
import com.gg.midend.domain.VO.ThemeInfo;
import com.gg.midend.domain.VO.ThemePatInfo;
import com.gg.midend.domain.VO.ThemeStatics;
import com.gg.midend.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller
 * 问诊主题
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-13
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/theme")
public class ThemeController {

    private final ThemeService themeService;

    /**
     * 新增主题信息
     *
     * @param themeReq 主题信息
     * @return themeId
     **/
    @RequestMapping(value = "/creatTheme")
    public ApiResult<Long> creatTheme(@Valid @MultiRequestBody("body") ThemeReq themeReq) {

        return ApiResult.success("themeId", themeService.intThemeInfo(themeReq));
    }

    /**
     * 结帖-终止问诊
     *
     * @param themeId 主题号
     * @return num 更新记录条数
     **/
    @RequestMapping(value = "/stopTheme")
    public ApiResult<Integer> stopTheme(@MultiRequestBody("body") Long themeId) {

        return ApiResult.success("num", themeService.stopTheme(themeId));
    }

    /**
     * 查询单条主题信息
     *
     * @param themeId 主题号
     * @return 单条主题信息
     **/
    @RequestMapping(value = "/QueryTheme")
    public ApiResult<ThemeInfo> QueryTheme(@MultiRequestBody("body") Long themeId) {

        return ApiResult.success(themeService.selThemeInfo(themeId));
    }

    /**
     * 查询问诊主题信息及病人信息
     *
     * @param qryThemePatReq 查询VO
     * @return List<QryThemePatVO>
     */
    @RequestMapping(value = "/queryThemePatInfo")
    public ApiResult<List<ThemePatInfo>> queryThemePatInfo(@Valid @MultiRequestBody("body") QryThemePatReq qryThemePatReq) {

        return ApiResult.success("listInfo", themeService.queryThemePatInfo(qryThemePatReq));
    }


    /**
     * 问诊信息统计
     *
     * @param doctId 医生id 为空时统计所有
     * @return StaticsVO 正在问诊，问诊完成，本周接诊
     */
    @RequestMapping(value = "/staticsStatus")
    public ApiResult<ThemeStatics> staticsStatus(@MultiRequestBody("body") String doctId) {

        return ApiResult.success("listInfo", themeService.staticsStatus(doctId));
    }

    /**
     * 查询问诊主题信息及病人预约信息
     *
     * @param qryThemeAppReq 患者信息
     * @return ThemeAppInfo 患者预约信息
     */
    @RequestMapping(value = "/qryThemeAppInfo")
    public ApiResult<List<ThemeAppInfo>> qryThemeAppInfo(@Valid @MultiRequestBody("body") QryThemeAppReq qryThemeAppReq) {

        return ApiResult.success("listInfo", themeService.qryThemeAppInfo(qryThemeAppReq));
    }
}
