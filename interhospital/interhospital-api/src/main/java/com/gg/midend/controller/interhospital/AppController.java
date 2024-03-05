package com.gg.midend.controller.interhospital;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.DTO.AppReq;
import com.gg.midend.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 预约信息管理
 * Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-13
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/app")
public class AppController {

    private final AppService appService;

    /**
     * 预约信息创建
     *
     * @param appReq 预约信息
     * @return themeId 主题号
     */
    @PostMapping(value = "/creatAppInfo")
    public ApiResult<Long> creatAppInfo(@Valid @MultiRequestBody("body") AppReq appReq) {

        return ApiResult.success("themeId", appService.intAppInfo(appReq));
    }

}
