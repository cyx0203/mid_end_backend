package com.gg.midend.entity.dto;

import lombok.Data;

/**
 *  调用CA登录返回数据
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-07
 **/
@Data
public class LoginRsp {

    /**
     * 返回数据
     */
    private Data data;

    @lombok.Data
    public static class Data{

        /**
         * 员工工号
         */
        private String jobNum;

        /**
         * uuid
         */
        private String uuid;

        /**
         * 签名类型
         */
        private String signType;

        /**
         * 签名值
         */
        private String signData;

        /**
         * 签名验签结果
         */
        private String signResult;
    }

    public String getJobNum(){
        return data.getJobNum();
    }

    public String getUUID(){
        return data.getUuid();
    }

    public String getSignType(){
        return data.getSignType();
    }

    public String getSignData(){
        return data.getSignData();
    }

    public String getSignResult(){
        return data.getSignResult();
    }
}
