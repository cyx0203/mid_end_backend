package com.gg.midend.bean;

import com.gg.core.annotation.IdCard;
import com.gg.core.annotation.IntEnum;
import com.gg.core.annotation.Mobile;
import com.gg.core.validation.enums.SexEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * TODO
 *
 * @Description
 * @Auther: Administrator
 * @Date: 2023/3/29 8:36
 */
@Data
public class PersonInfo {

    /**
     * 手机号
     */
    @Mobile
    private String hospitalId;

    /**
     * 患者ID
     */
    private String parId;




}
