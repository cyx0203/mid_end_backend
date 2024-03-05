package com.gg.midend.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.gg.midend.domain.DTO.DoctLoginReq;
import com.gg.midend.domain.DTO.ModifyStatReq;
import com.gg.midend.domain.VO.DoctInfo;
import com.gg.midend.domain.DTO.ModifyPwdReq;
import com.gg.midend.domain.VO.DoctPwdInfo;
import com.gg.midend.exception.enums.ServiceErrorCodeConstants;
import com.gg.midend.mapper.DoctorMapper;
import com.gg.midend.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 实现类
 * 医生相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-17
 */
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper doctorMapper;

    private Boolean validDoctPwd(String hospitalId, String doctId, String doctPsd) {

        DoctPwdInfo doctPwdInfo = doctorMapper.selDoctPwd(hospitalId, doctId);
        if (ObjUtil.isNull(doctPwdInfo)) {
            throw exception(ServiceErrorCodeConstants.DOCTOR_ACCOUNT_NONE);
        }
        if (StrUtil.equals(doctPwdInfo.getDoctActive(), "0")) {
            throw exception(ServiceErrorCodeConstants.DOCTOR_ACCOUNT_ACTIVE);
        }
        if (!StrUtil.equals(doctPwdInfo.getDoctPsd(), doctPsd)) {
            throw exception(ServiceErrorCodeConstants.DOCTOR_ACCOUNT_WRONG);
        }
        return true;
    }

    @Override
    public int modifyStat(ModifyStatReq modifyStatReq) {

        return doctorMapper.updDoctStat(modifyStatReq);
    }

    @Override
    public int modifyPwd(ModifyPwdReq modifyPwdReq) {

        if (StrUtil.equals(modifyPwdReq.getOrigInquiryPsd(), modifyPwdReq.getInquiryPsd())) {
            throw exception(ServiceErrorCodeConstants.DOCTOR_ACCOUNT_PSD);
        }

        if (validDoctPwd(modifyPwdReq.getHospitalId(), modifyPwdReq.getDoctId(), modifyPwdReq.getOrigInquiryPsd())) {
            return doctorMapper.updDoctPwd(modifyPwdReq);
        }
        return 0;
    }

    @Override
    public DoctInfo doctLoginAndQry(DoctLoginReq doctLoginReq) {

        if (validDoctPwd(doctLoginReq.getFHospitalid(), doctLoginReq.getFId(), doctLoginReq.getFPasswd())) {
            return doctorMapper.selDoctInfo(doctLoginReq.getFHospitalid(), doctLoginReq.getFId());
        }
        return null;
    }
}
