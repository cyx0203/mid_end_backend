package com.gg.midend.controller.interhospital;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.DTO.*;
import com.gg.midend.domain.VO.CommWordsInfo;
import com.gg.midend.domain.VO.DoctInfo;
import com.gg.midend.service.CommWordsService;
import com.gg.midend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 医生端服务
 * Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/doctor")
public class DoctController {

    private final DoctorService doctorService;

    private final CommWordsService commwordsService;

    /**
     * 医生账户登录并返回问诊医生信息
     *
     * @param doctLoginReq 医生登录信息
     * @return DoctInfo 问诊医生信息
     */
    @PostMapping("/login")
    public ApiResult<DoctInfo> login(@Valid @MultiRequestBody("body") DoctLoginReq doctLoginReq) {

        return ApiResult.success(doctorService.doctLoginAndQry(doctLoginReq));
    }

    /**
     * 修改医生登录状态
     *
     * @param modifyStatReq 修改登录状态Req
     * @return num 更新记录条数
     */
    @RequestMapping("/modifyStat")
    public ApiResult<Integer> modifyStat(@Valid @MultiRequestBody("body") ModifyStatReq modifyStatReq) {

        return ApiResult.success("num", doctorService.modifyStat(modifyStatReq));
    }

    /**
     * 修改医生登录密码
     *
     * @param modifyPwdReq 修改登录密码Req
     * @return num 更新记录条数
     */
    @RequestMapping("/modifyPwd")
    public ApiResult<Integer> modifyPwd(@Valid @MultiRequestBody("body") ModifyPwdReq modifyPwdReq) {

        return ApiResult.success("num", doctorService.modifyPwd(modifyPwdReq));
    }

    /**
     * 医生选择常用语
     *
     * @param qryWordsReq 医生常用语请求参数
     * @return CommWordsInfo 医生常用语信息
     */
    @RequestMapping("/selectCommWords")
    public ApiResult<CommWordsInfo> selectCommWords(@Valid @MultiRequestBody("body") QryWordsReq qryWordsReq) {

        return ApiResult.success("listInfo", commwordsService.selCommWordsList(qryWordsReq));
    }

    /**
     * 医生插入常用语
     *
     * @param creatWordsReq 医生常用语请求参数
     * @return num 更新记录条数
     */
    @RequestMapping("/insertCommWords")
    public ApiResult<Integer> insertCommWords(@Valid @MultiRequestBody("body") CreatWordsReq creatWordsReq) {

        return ApiResult.success("listInfo", commwordsService.updOrIntCommWords(creatWordsReq));
    }
}
