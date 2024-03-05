package com.gg.midend.service;

import com.gg.midend.domain.DTO.DoctLoginReq;
import com.gg.midend.domain.DTO.ModifyPwdReq;
import com.gg.midend.domain.DTO.ModifyStatReq;
import com.gg.midend.domain.VO.DoctInfo;

/**
 * 医生相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 */
public interface DoctorService {

    /**
     * 更新医生登录状态
     *
     * @param modifyStatReq 修改医生登录状态
     * @return num 更新记录条数
     */
    int modifyStat(ModifyStatReq modifyStatReq);

    /**
     * 更新医生登录密码
     *
     * @param modifyPwdReq 修改密码请求
     * @return num 更新记录条数
     */
    int modifyPwd(ModifyPwdReq modifyPwdReq);

    /**
     * 登录并返回医生信息
     *
     * @param doctLoginReq 医生登录信息
     * @return 医生信息
     */
    DoctInfo doctLoginAndQry(DoctLoginReq doctLoginReq);
}
