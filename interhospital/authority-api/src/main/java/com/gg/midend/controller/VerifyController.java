package com.gg.midend.controller;

import cn.hutool.core.util.StrUtil;
import cn.org.hb.HbcaSDK;
import cn.org.hb.constants.Constant;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.entity.dto.VerifyReq;
import com.gg.midend.entity.vo.VerifyVO;
import com.gg.midend.exception.enums.ServiceErrorCodeConstants;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 验证 Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-09
 **/
@RestController
@RequestMapping("/authority-api/verify")
public class VerifyController {

    /**
     * 登录验签
     *
     * @param verifyReq 员工工号
     * @return boolean 登录信息
     */
    @RequestMapping(value = "/verifyLogin")
    public ApiResult<VerifyVO> verifyLogin(@Valid @RequestBody VerifyReq verifyReq) throws Exception {

        VerifyVO verifyVO = new VerifyVO();
        // 验签
        HbcaSDK sdk = new HbcaSDK(Constant.DSS_SERVER);
        if (sdk.SOF_VerifyDetachSignedDataByP7(verifyReq.getVerifyData(), verifyReq.getSignedData())){
            HbcaSDK timeStamp = new HbcaSDK(Constant.TSS_SERVER);
            String timeStampData = timeStamp.SOF_CreateTimeStampBySignData(verifyReq.getSignedData());
            if(StrUtil.isBlank(timeStampData)){

            }
        }else {

        }

        return ApiResult.success(verifyVO);
    }

    private Boolean verifyData(String verifyData, String signedData) throws Exception {

        // 验签
        HbcaSDK sdk = new HbcaSDK(Constant.DSS_SERVER);
        if (sdk.SOF_VerifyDetachSignedDataByP7(verifyData, signedData)) {
            HbcaSDK timeStamp = new HbcaSDK(Constant.TSS_SERVER);
            String timeStampData = timeStamp.SOF_CreateTimeStampBySignData(signedData);
            if(StrUtil.isBlank(timeStampData)){
                throw exception(ServiceErrorCodeConstants.VAL_TIME_ERR);
            }
            return true;
        } else {
            return false;
        }

    }
}
