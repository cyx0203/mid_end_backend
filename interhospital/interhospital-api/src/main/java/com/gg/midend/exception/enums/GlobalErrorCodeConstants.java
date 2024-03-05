package com.gg.midend.exception.enums;

import com.gg.core.exception.api.Header;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 * 一般情况下，使用 HTTP 响应状态码
 *
 * @author fun-mean
 */
public interface GlobalErrorCodeConstants {

    Header SUCCESS = new Header("00", "成功");
    Header FAILED = new Header("1", "失败");

    // ========== 客户端错误段 ==========

    Header BAD_REQUEST = new Header("400", "请求参数不正确");
    Header UNAUTHORIZED = new Header("401", "账号未登录");
    Header FORBIDDEN = new Header("403", "没有该操作权限");
    Header NOT_FOUND = new Header("404", "请求未找到");
    Header METHOD_NOT_ALLOWED = new Header("405", "请求方法不正确");
    Header LOCKED = new Header("423", "请求失败，请稍后重试"); // 并发请求，不允许
    Header TOO_MANY_REQUESTS = new Header("429", "请求过于频繁，请稍后重试");

    // ========== 服务端错误段 ==========
    Header INTERNAL_SERVER_ERROR = new Header("500", "系统异常");
    Header NOT_IMPLEMENTED = new Header("501", "功能未实现/未开启");

    // ========== 自定义错误段 ==========
    Header REPEATED_REQUESTS = new Header("900", "重复请求，请稍后重试"); // 重复请求
    Header DEMO_DENY = new Header("901", "演示模式，禁止写操作");
    Header UNKNOWN = new Header("999", "未知错误");

    // ========== 数据库操作错误段 ==========
    Header DATABASE_RECORD_NONE = new Header("1001", "查询无记录");
    Header DATABASE_RECORD_REPEAT = new Header("1002", "违反数据库唯一约束条件");
    Header DATABASE_UPDATE_ERROR = new Header("1003", "数据库更新失败，请稍后重试");
    // ========== 数据校验错误段 ==========
    Header REQ_REQUIRED_BLANK = new Header("2001", "唯一请求字段不能为空");

}
