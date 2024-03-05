package com.gg.midend.exception.enums;

import com.gg.core.exception.api.Header;

/**
 * 业务异常的错误码区间，解决：解决各模块错误码定义，
 * 避免重复，在此只声明不做实际使用
 *
 * @author fun-mean
 */
public interface ServiceErrorCodeConstants {

    Header VAL_TIME_ERR = new Header("10000", "获取时间戳失败!");
    Header VAL_SIGN_ERR = new Header("10001", "验签失败！");

}
