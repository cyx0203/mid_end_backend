package com.gg.midend.exception.enums;

import com.gg.core.exception.api.Header;

/**
 * 业务异常的错误码区间，解决：解决各模块错误码定义，
 * 避免重复，在此只声明不做实际使用
 *
 * @author fun-mean
 */
public interface ServiceErrorCodeConstants {

    Header DOCTOR_ACCOUNT_NONE = new Header("10000", "该机构下医生账号不存在");
    Header DOCTOR_ACCOUNT_WRONG = new Header("10001", "账号密码输入有误");
    Header DOCTOR_ACCOUNT_PSD = new Header("10002", "原账号密码与现账号密码不能相同");
    Header DOCTOR_ACCOUNT_ACTIVE = new Header("10003", "该医生未开通在线问诊账号");
    Header COUNT_NOT_NULL = new Header("22000", "当消息来源为患者时，现剩余次数countdown不能为空");
    Header COUNT_SHORTAGE = new Header("22000", "回复失败,剩余次数不足");

}
