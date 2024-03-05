package com.gg.midend.controller.interhospital;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.DTO.AppraisalReq;
import com.gg.midend.service.AppraisalService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 医生护工评价管理
 * Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-03
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/appraisal")
public class AppraisalController {

    private final AppraisalService appraisalService;

    /**
     * 患者关注医生
     *
     * @param appraisalReq 关注信息
     * @return flowNo 唯一ID
     */
    @PostMapping(value = "/followDoct")
    public ApiResult<Long> followDoct(@Valid @MultiRequestBody("body") AppraisalReq appraisalReq) {

        return ApiResult.success("flowNo", appraisalService.intAppraisalInfo(appraisalReq));
    }

    /**
     * 患者取关医生
     *
     * @param flowNo 唯一ID
     * @return num 更改记录条数
     */
    @PostMapping(value = "/unFollowDoct")
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public ApiResult<Integer> unFollowDoct(@Valid @MultiRequestBody("body") Long flowNo) {

        return ApiResult.success("num", appraisalService.delAppraisalInfo(flowNo));
    }
}
