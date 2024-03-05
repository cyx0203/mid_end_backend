package com.gg.midend.controller.com;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.VO.HospInfo;
import com.gg.midend.domain.VO.HospListInfo;
import com.gg.midend.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gg.midend.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 *  医院信息Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-13
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * 获取医院信息
     *
     * @param hospitalId 医院编号
     * @return HospInfo 医院信息
     */
    @RequestMapping(value = "/qryHosInfo")
    public ApiResult<HospInfo> qryHosInfo(@MultiRequestBody("body") String hospitalId) {

        return ApiResult.success(hospitalService.qryHosInfo(hospitalId));
    }

    /**
     * 获取医院列表
     */
    @RequestMapping(value = "/getLoginHospital")
    public ApiResult<HospListInfo> getLoginHospital(@MultiRequestBody("body") String isOpen) {

        String active;
        if ("Y".equals(isOpen.trim())) {
            active = "1";
        } else if ("N".equals(isOpen.trim())) {
            active = "2";
        } else {
            return ApiResult.error(BAD_REQUEST);
        }

        return ApiResult.success("listInfo",hospitalService.getLoginHospital(active));
    }
}
