package com.gg.midend.controller;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.entity.dto.LoginRsp;
import com.gg.midend.entity.vo.ChkSignVO;
import com.gg.midend.entity.vo.LoginVO;
import com.gg.midend.utils.HttpUtil;
import com.gg.midend.utils.StrToQRCodeBase64Util;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录数据Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-07
 **/
@RestController
@RequestMapping("/authority-api/login")
public class LoginSignController {

    @Value("${ca_config.baseURL}")
    private String baseURL;

    @Value("${ca_config.loginByPushURI}")
    private String loginByPushURI;

    @Value("${ca_config.loginByQrCodeURI}")
    private String loginByQrCodeURI;

    @Value("${ca_config.chkSignURI}")
    private String chkSignURI;

    @Value("${ca_config.qrWidth}")
    private Integer qrWidth;

    /**
     * 推送手机登录
     *
     * @param jobNum 包含jobNum员工工号
     * @return loginVO 登录信息
     */
    @RequestMapping(value = "/loginByPush")
    public ApiResult<LoginVO> loginByPush(@MultiRequestBody("body") String jobNum) {

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("jobNum", jobNum);

        LoginRsp loginRsp = HttpUtil.post
                (baseURL + loginByPushURI, reqMap, LoginRsp.class);

        LoginVO loginVO = new LoginVO();
        loginVO.setUuid(loginRsp.getUUID());

        return ApiResult.success(loginVO);
    }

    /**
     * 二维码登录
     *
     * @return loginVO 登录信息
     */
    @RequestMapping(value = "/loginByQrCode")
    public ApiResult<LoginVO> loginByQrCode() {

        LoginRsp loginRsp = HttpUtil.post
                (baseURL + loginByQrCodeURI, null, LoginRsp.class);

        // 服务端根据data值生成二维码的base64编码
        Gson gson = new Gson();

        LoginVO loginVO = new LoginVO();
        loginVO.setQrCode(StrToQRCodeBase64Util.QRCodeToBase64(gson.toJson(loginRsp.getData()), qrWidth));
        loginVO.setUuid(loginRsp.getUUID());

        return ApiResult.success(loginVO);
    }

    /**
     * 轮询检查
     *
     * @param uuid 包含uuid 员工工号
     * @return chkSignVO 轮询检查数据
     */
    @RequestMapping(value = "/checkSignResult")
    public ApiResult<ChkSignVO> checkSignResult(@MultiRequestBody("body") String uuid) {

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("uuid", uuid);

        LoginRsp loginRsp = HttpUtil.post
                (baseURL + chkSignURI, reqMap, LoginRsp.class);

        ChkSignVO chkSignVO = new ChkSignVO();
        chkSignVO.setJobNum(loginRsp.getJobNum());
        chkSignVO.setSignData(loginRsp.getSignData());
        chkSignVO.setSignResult(loginRsp.getSignResult());


        return ApiResult.success(chkSignVO);
    }
}