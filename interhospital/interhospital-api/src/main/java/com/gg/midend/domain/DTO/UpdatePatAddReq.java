package com.gg.midend.domain.DTO;

import com.gg.core.annotation.Mobile;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 实体类
 * 修改患者地址信息入参
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-02
 **/
@Data
public class UpdatePatAddReq {
    /**
     * uuid
     */
    @NotNull(message = "uuid不能为空")
    private Long id;
    /**
     * 用户标识
     */
    @NotBlank(message = "用户标识不能为空")
    private String openId;
    /**
     * 收件人名称
     */
    private String name;
    /**
     * 收件人地址
     */
    private String address;
    /**
     * 收件人联系方式
     */
    @Mobile
    private String phone;
    /**
     * 默认地址
     * Y-是，N-否
     */
    private String isDefault;
}
