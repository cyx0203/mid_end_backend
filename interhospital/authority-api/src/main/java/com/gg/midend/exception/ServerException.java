package com.gg.midend.exception;

import com.gg.core.exception.api.Header;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务器异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServerException extends RuntimeException {

    /**
     * 全局错误码
     *
     */
    private String code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServerException() {
    }

    public ServerException(Header header) {
        this.code = header.getRspCode();
        this.message = header.getRspMsg();
    }

    public ServerException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public ServerException setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServerException setMessage(String message) {
        this.message = message;
        return this;
    }

}
